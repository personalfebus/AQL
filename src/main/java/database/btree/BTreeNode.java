package database.btree;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;

public class BTreeNode implements Serializable {
    private final long[] keys;
    transient private FieldContainer fieldContainer;
    //minimum number of children
    private final int t;
    //количество полей
    private final int numberOfFields;
    private final BTreeNode[] children;
    //количество ключей в ноде на данный момент
    private int n;
    //является ли вершина лиственной
    boolean isLeaf;

    private static final Logger log = LoggerFactory.getLogger(BTreeNode.class);

    public BTreeNode(int t, int numberOfFields, boolean isLeaf) {
        this.t = t;
        this.numberOfFields = numberOfFields;
        this.isLeaf = isLeaf;
        keys = new long[2*t - 1];
        children = new BTreeNode[2*t];
        this.fieldContainer = new FieldContainer(2*t - 1, numberOfFields);
        this.n = 0;
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
}
