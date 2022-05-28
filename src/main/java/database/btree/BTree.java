package database.btree;

import java.io.Serializable;

public class BTree implements Serializable {
    private BTreeNode root;
    //number of keys in one node
    private final int width;
    private final int numberOfFields;
    private final int keyGenerator;

    public BTree(BTreeNode root, int width, int numberOfFields) {
        this.root = root;
        this.width = width;
        this.numberOfFields = numberOfFields;
        this.keyGenerator = 0;
    }

    public BTree(int width, int numberOfFields, int keyGenerator) {
        this.width = width;
        this.numberOfFields = numberOfFields;
        this.keyGenerator = keyGenerator;
    }

    public void traverse() {
        root.traverse();
    }
}
