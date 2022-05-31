package database.btree;

import database.field.Field;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;

public class BTree implements Serializable {
    private final static Logger log = LoggerFactory.getLogger(BTree.class.getName());

    private BTreeNode root;
    //minimum number of children of one node
    private final int t;
    private final int numberOfFields;

    public BTree(BTreeNode root, int t, int numberOfFields) {
        this.root = root;
        this.t = t;
        this.numberOfFields = numberOfFields;
    }

    public BTree(int t, int numberOfFields) {
        this.t = t;
        this.numberOfFields = numberOfFields;
        this.root = new BTreeNode(t, numberOfFields, true, 0);
        //todo write disk
    }

    public void traverse() {
        root.traverse();
    }

    public BTreeNode searchByKey(Field key) {
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

    public void splitChild(BTreeNode x, int i) {
        BTreeNode y = x.children[i];
        BTreeNode z = new BTreeNode(y.t, y.numberOfFields, y.isLeaf, t - 1);

        for (int j = 0; j < t - 1; j++) {
            z.keys[j] = y.keys[j + t];
            z.fieldContainer.setRow(j, y.fieldContainer.getRow(j + t));
        }

        if (!y.isLeaf) {
            for (int j = 0; j < t; j++) {
                z.children[j] = y.children[j + t];
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
        x.fieldContainer.setRow(i, y.fieldContainer.getRow(t - 1));
        x.n = x.n + 1;
        //todo disk write;
    }

    public void insert(Field key, Field[] values) {
        if (root == null) {
            root = new BTreeNode(t, numberOfFields, true, 0);
        }

        BTreeNode r = root;
        if (root.n == 2*t - 1) {
            BTreeNode s = new BTreeNode(t, numberOfFields, false, 0);
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
            BTreeNode old = root;
            if (root.isLeaf) {
                root = null;
            } else {
                root = root.children[0];
            }
        }
    }

    private void insertNonFull(BTreeNode x, Field key, Field[] values) {
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
            //todo disk write;
        } else {
            while (i >= 0 && key.compareTo(x.keys[i]) < 0) {
                i--;
            }
            i++;
            //todo disk read;

            if (x.children[i].n == 2*t - 1) {
                splitChild(x, i);

                if (key.compareTo(x.keys[i]) > 0) {
                    i++;
                }
            }
            insertNonFull(x.children[i], key, values);
        }
    }

//    public void BTreeInsert(Field key, Field[] values) {
//        if (values.length != numberOfFields) {
//            log.error("Incorrect number of fields in insert");
//            return;
//        }
//        if (root == null) {
//            root = new BTreeNode(t, numberOfFields, true, 0);
//            root.setKey(0, key);
//            root.setValues(0, values);
//
//        }
//    }
}
