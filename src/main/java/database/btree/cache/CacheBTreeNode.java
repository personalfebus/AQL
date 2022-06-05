package database.btree.cache;

import database.btree.Entry;
import database.btree.FieldContainer;
import database.field.Field;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;

public class CacheBTreeNode implements Serializable {
    private final static Logger log = LoggerFactory.getLogger(CacheBTreeNode.class.getName());

    public final Field[] keys;
    transient public FieldContainer fieldContainer;
    //minimum number of children
    public final int t;
    //количество полей
    public final int numberOfFields;
    public final CacheBTreeNode[] children;
    //количество ключей в ноде на данный момент
    public int n;
    //является ли вершина лиственной
    boolean isLeaf;

    public CacheBTreeNode(int t, int numberOfFields, boolean isLeaf, int n) {
        this.t = t;
        this.numberOfFields = numberOfFields;
        this.isLeaf = isLeaf;
        keys = new Field[2*t - 1];
        children = new CacheBTreeNode[2*t];
        this.fieldContainer = new FieldContainer(2*t - 1, numberOfFields);
        this.n = n;
    }

    public void traverse() {
        for (int i = 0; i < n; i++) {
            if (!isLeaf) {
                children[i].traverse();
            }
            System.out.println("-----------------");
            System.out.println("Key: " + keys[i]);

            System.out.println("Values:");
            for (int j = 0; j < numberOfFields; j++) {
                System.out.println(fieldContainer.get(i, j));
            }
            System.out.println("-----------------");
        }

        if (!isLeaf) {
            children[n].traverse();
        }
    }

    public CacheBTreeNode searchByKey(Field key) {
        int i = 0;

        while (i < n && key.compareTo(keys[i]) > 0) {
            i++;
        }

        if (i < n && keys[i].compareTo(key) == 0) {
            return this;
        }

        if (isLeaf) {
            return null;
        }

        return children[i].searchByKey(key);
    }

    public Entry getEntryByKey(Field key) {
        int i = 0;

        while (i < n && key.compareTo(keys[i]) > 0) {
            i++;
        }

        if (i < n && keys[i].compareTo(key) == 0) {
            return new Entry(keys[i], fieldContainer.getRow(i));
        }

        if (isLeaf) {
            return null;
        }

        return children[i].getEntryByKey(key);
    }

    /**
     * @param key Key
     * @return position in node
     */
    private int findKey(Field key) {
        int idx = 0;
        while (idx < n && keys[idx].compareTo(key) < 0) {
            idx++;
        }
        return idx;
    }

    public void remove(Field key) {
        int idx = findKey(key);

        if (idx < n && keys[idx].compareTo(key) == 0) {
            if (isLeaf) {
                removeFromLeaf(idx);
            } else {
                removeFromNonLeaf(idx);
            }
        } else {
            if (isLeaf) {
                log.info("Key {} does not exist in CacheBTree", key);
            }

            boolean flag = (idx == n);
            if (children[idx].n < t) {
                fill(idx);
            }

            if (flag && idx > n) {
                children[idx - 1].remove(key);
            } else {
                children[idx].remove(key);
            }
        }
    }

    //opt
    private void removeFromLeaf(int idx) {
        for (int i = idx + 1; i < n; ++i) {
            keys[i - 1] = keys[i];
            fieldContainer.setRow(i - 1, fieldContainer.getRow(i));
        }
        //opt
        keys[n - 1] = null;
        fieldContainer.setRow(n - 1, null);

        n--;
    }

    private void removeFromNonLeaf(int idx) {
        Field k = keys[idx];

        if (children[idx].n >= t) {
            Entry predEntry = getPredEntry(idx);
            keys[idx] = predEntry.getKey();
            fieldContainer.setRow(idx, predEntry.getValues());
            children[idx].remove(predEntry.getKey());
        } else if (children[idx + 1].n >= t) {
            Entry succEntry = getSuccEntry(idx);
            keys[idx] = succEntry.getKey();
            fieldContainer.setRow(idx, succEntry.getValues());
            children[idx + 1].remove(succEntry.getKey());
        } else {
            merge(idx);
            children[idx].remove(k);
        }
    }

    //opt
    private void merge(int idx) {
        CacheBTreeNode child = children[idx];
        CacheBTreeNode sibling = children[idx + 1];

        child.keys[t - 1] = keys[idx];
        child.fieldContainer.setRow(t - 1, fieldContainer.getRow(idx));

        for (int i = 0; i < sibling.n; i++) {
            child.keys[i + t] = sibling.keys[i];
            child.fieldContainer.setRow(i + t, sibling.fieldContainer.getRow(i));
        }

        if (!child.isLeaf) {
            for (int i = 0; i <= sibling.n; i++) {
                child.children[i + t] = sibling.children[i];
            }
        }

        for (int i = idx + 1; i < n; i++) {
            keys[i - 1] = keys[i];
            fieldContainer.setRow(i - 1, fieldContainer.getRow(i));
        }
        //opt
        keys[n - 1] = null;
        fieldContainer.setRow(n - 1, null);

        for (int i = idx + 2; i <= n; i++) {
            children[i - 1] = children[i];
        }
        //opt
        children[n] = null;

        child.n += sibling.n + 1;
        n--;
    }

    private void fill(int idx) {
        if (idx != 0 && children[idx - 1].n >= t) {
            borrowFromPrev(idx);
        } else if (idx != n && children[idx + 1].n >= t) {
            borrowFromNext(idx);
        } else {
            if (idx != n) {
                merge(idx);
            } else {
                merge(idx - 1);
            }
        }
    }

    //opt
    private void borrowFromNext(int idx) {
        CacheBTreeNode child = children[idx];
        CacheBTreeNode sibling = children[idx + 1];

        child.keys[child.n] = keys[idx];
        child.fieldContainer.setRow(child.n, fieldContainer.getRow(idx));

        if (!child.isLeaf) {
            child.children[child.n - 1] = sibling.children[0];
        }

        keys[idx] = sibling.keys[0];
        fieldContainer.setRow(idx, sibling.fieldContainer.getRow(0));

        for (int i = 1; i < sibling.n; i++) {
            sibling.keys[i - 1] = sibling.keys[i];
            sibling.fieldContainer.setRow(i - 1, sibling.fieldContainer.getRow(i));
        }
        //opt
        sibling.keys[sibling.n - 1] = null;
        sibling.fieldContainer.setRow(sibling.n - 1, null);

        if (!sibling.isLeaf) {
            for (int i = 1; i <= sibling.n; i++) {
                sibling.children[i - 1] = sibling.children[i];
            }
            //opt
            sibling.children[sibling.n] = null;
        }

        child.n = child.n + 1;
        sibling.n = sibling.n - 1;
    }

    //opt
    private void borrowFromPrev(int idx) {
        CacheBTreeNode child = children[idx];
        CacheBTreeNode sibling = children[idx - 1];

        for (int i = child.n - 1; i >= 0; i--) {
            child.keys[i + 1] = child.keys[i];
            child.fieldContainer.setRow(i + 1, child.fieldContainer.getRow(i));
        }

        if (!child.isLeaf) {
            for (int i = child.n; i >= 0; i--) {
                child.children[i + 1] = child.children[i];
            }
        }

        child.keys[0] = keys[idx - 1];
        child.fieldContainer.setRow(0, fieldContainer.getRow(idx - 1));

        if (!child.isLeaf) {
            child.children[0] = sibling.children[sibling.n];
        }
        //opt
        sibling.children[sibling.n] = null;

        keys[idx - 1] = sibling.keys[sibling.n - 1];
        fieldContainer.setRow(idx - 1, sibling.fieldContainer.getRow(sibling.n - 1));
        //opt
        sibling.keys[sibling.n - 1] = null;
        sibling.fieldContainer.setRow(sibling.n - 1, null);

        child.n = child.n + 1;
        sibling.n = sibling.n - 1;
    }

    private Entry getPredEntry(int idx) {
        CacheBTreeNode current = children[idx];

        while (!current.isLeaf) {
            current = current.children[current.n];
        }

        return new Entry(current.keys[current.n - 1], current.fieldContainer.getRow(current.n - 1));
    }

    private Entry getSuccEntry(int idx) {
        CacheBTreeNode current = children[idx + 1];

        while (!current.isLeaf) {
            current = current.children[0];
        }

        return new Entry(current.keys[0], current.fieldContainer.getRow(0));
    }
}