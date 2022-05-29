package database.btree;

import java.io.Serializable;

public class BTree implements Serializable {
    private BTreeNode root;
    //minimum number of children of one node
    private final int t;
    private final int numberOfFields;
    private final int keyGenerator;

    public BTree(BTreeNode root, int t, int numberOfFields) {
        this.root = root;
        this.t = t;
        this.numberOfFields = numberOfFields;
        this.keyGenerator = 0;
    }

    public BTree(int t, int numberOfFields, int keyGenerator) {
        this.t = t;
        this.numberOfFields = numberOfFields;
        this.keyGenerator = keyGenerator;
    }

    public void traverse() {
        root.traverse();
    }
}
