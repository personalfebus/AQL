package database.btree;

import java.io.Serializable;

public class BTreeNode implements Serializable {
    private final long[] keys;
    transient private FieldContainer fieldContainer;
    private final int width;
    //количество полей
    private final int numberOfFields;
    private final BTreeNode[] children;
    //количество ключей в ноде на данный момент
    private int size;
    //является ли вершина лиственной
    boolean isLeaf;

    public BTreeNode(int width, int numberOfFields, boolean isLeaf) {
        this.width = width;
        this.numberOfFields = numberOfFields;
        this.isLeaf = isLeaf;
        keys = new long[width];
        children = new BTreeNode[2*width - 1]; //todo why???
    }

    public void traverse() {

    }
}
