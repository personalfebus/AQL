package database.btree.cache;

import database.btree.Entry;
import database.field.Field;
import lombok.extern.slf4j.Slf4j;

import java.io.Serializable;

@Slf4j
public class CacheBTree implements Serializable {
    private CacheBTreeNode root;
    //minimum number of children of one node
    private final int t;
    private final int numberOfFields;

    public CacheBTree(CacheBTreeNode root, int t, int numberOfFields) {
        this.root = root;
        this.t = t;
        this.numberOfFields = numberOfFields;
    }

    public CacheBTree(int t, int numberOfFields) {
        this.t = t;
        this.numberOfFields = numberOfFields;
        this.root = new CacheBTreeNode(t, numberOfFields, true, 0);
    }

    public void traverse() {
        root.traverse();
    }

    public CacheBTreeNode searchByKey(Field key) {
        if (root == null) {
            return null;
        } else {
            return root.searchByKey(key);
        }
    }

    public Entry getEntryByKey(Field key) {
        if (root == null) {
            return null;
        } else {
            return root.getEntryByKey(key);
        }
    }

    public void splitChild(CacheBTreeNode x, int i) {
        CacheBTreeNode y = x.children[i];
        CacheBTreeNode z = new CacheBTreeNode(y.t, y.numberOfFields, y.isLeaf, t - 1);

        for (int j = 0; j < t - 1; j++) {
            z.keys[j] = y.keys[j + t];
            z.fieldContainer.setRow(j, y.fieldContainer.getRow(j + t));
            //opt
            y.keys[j + t] = null;
            y.fieldContainer.setRow(j + t, null);
        }

        if (!y.isLeaf) {
            for (int j = 0; j < t; j++) {
                z.children[j] = y.children[j + t];
                //opt
                y.children[j + t] = null;
            }
        }

        y.n = t - 1;

        for (int j = x.n; j >= i + 1; j--) {
            x.children[j + 1] = x.children[j];
        }

        x.children[i + 1] = z;

        for (int j = x.n - 1; j >= i; j--) {
            x.keys[j + 1] = x.keys[j];
            x.fieldContainer.setRow(j + 1, x.fieldContainer.getRow(j));
        }

        x.keys[i] = y.keys[t - 1];
        //opt
        y.keys[t - 1] = null;
        x.fieldContainer.setRow(i, y.fieldContainer.getRow(t - 1));
        x.n = x.n + 1;
    }

    public void insert(Field key, Field[] values) {
        if (root == null) {
            root = new CacheBTreeNode(t, numberOfFields, true, 0);
        }

        CacheBTreeNode r = root;
        if (root.n == 2 * t - 1) {
            CacheBTreeNode s = new CacheBTreeNode(t, numberOfFields, false, 0);
            root = s;
            s.children[0] = r;
            splitChild(s, 0);
            insertNonFull(s, key, values);
        } else {
            insertNonFull(r, key, values);
        }
    }

    public void remove(Field key) {
        if (root == null) {
            log.error("Remove from empty tree");
            return;
        }
        root.remove(key);

        if (root.n == 0) {
            CacheBTreeNode old = root;
            if (root.isLeaf) {
                root = null;
            } else {
                root = root.children[0];
            }
        }
    }

    private void insertNonFull(CacheBTreeNode x, Field key, Field[] values) {
        int i = x.n - 1;

        if (x.isLeaf) {
            while (i >= 0 && key.compareTo(x.keys[i]) < 0) {
                x.keys[i + 1] = x.keys[i];
                x.fieldContainer.setRow(i + 1, x.fieldContainer.getRow(i));
                i--;
            }
            x.keys[i + 1] = key;
            x.fieldContainer.setRow(i + 1, values);
            x.n = x.n + 1;
        } else {
            while (i >= 0 && key.compareTo(x.keys[i]) < 0) {
                i--;
            }
            i++;

            if (x.children[i].n == 2 * t - 1) {
                splitChild(x, i);

                if (key.compareTo(x.keys[i]) > 0) {
                    i++;
                }
            }
            insertNonFull(x.children[i], key, values);
        }
    }
}