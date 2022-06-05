package database.btree;

import database.btree.annotation.CacheOnlyOperation;
import database.btree.annotation.ReadFromDiskRequired;
import database.btree.annotation.WriteToDiskRequired;
import database.btree.exception.ReadFromDiskError;
import database.btree.exception.WriteToDiskError;
import database.field.Field;
import lombok.Cleanup;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.Arrays;
import java.util.UUID;

public class BTree implements Serializable {
    private final static Logger log = LoggerFactory.getLogger(BTree.class.getName());
    private final static String pathPrefix = "binaries/";

    private final UUID uuid;
    private UUID rootUuid;
    private BTreeNode root;

    //minimum number of children of one node
    private final int t;
    private final int numberOfFields;

    public BTree(BTreeNode root, int t, int numberOfFields) {
        this.uuid = UUID.randomUUID();
        this.root = root;
        this.rootUuid = root.uuid;
        this.t = t;
        this.numberOfFields = numberOfFields;
    }

    public BTree(int t, int numberOfFields) throws WriteToDiskError {
        this.uuid = UUID.randomUUID();
        this.t = t;
        this.numberOfFields = numberOfFields;
        BTreeNode x = new BTreeNode(t, numberOfFields, true, 0);
        BTreeNode.writeToDisk(x.uuid, x);
        this.root = x;
        this.rootUuid = x.uuid;
    }

    public static BTree readFromDisk(UUID uuid) throws ReadFromDiskError {
        try {
            @Cleanup FileInputStream fileInputStream = new FileInputStream(pathPrefix + uuid.toString());
            @Cleanup ObjectInputStream objectInputStream = new ObjectInputStream(fileInputStream);
            return (BTree) objectInputStream.readObject();
        } catch (IOException | ClassNotFoundException e) {
            throw new ReadFromDiskError(e);
        }
    }

    public static void writeToDisk(UUID uuid, BTree tree) throws WriteToDiskError {
        try {
            @Cleanup FileOutputStream fileOutputStream = new FileOutputStream(pathPrefix + uuid.toString());
            @Cleanup ObjectOutputStream objectOutputStream = new ObjectOutputStream(fileOutputStream);
            objectOutputStream.writeObject(tree);
        } catch (IOException e) {
            throw new WriteToDiskError(e);
        }
    }

    @ReadFromDiskRequired
    public void traverse() throws ReadFromDiskError {
        root.traverse();
    }

    @ReadFromDiskRequired
    public BTreeNode searchByKey(Field key) throws ReadFromDiskError {
        if (root == null) {
            return null;
        } else {
            return root.searchByKey(key);
        }
    }

    @ReadFromDiskRequired
    public Entry getEntryByKey(Field key) throws ReadFromDiskError {
        if (root == null) {
            return null;
        } else {
            return root.getEntryByKey(key);
        }
    }

    @WriteToDiskRequired
    public void splitChild(BTreeNode x, int i) throws WriteToDiskError {
        //x.children[i] should already be read from disk
        BTreeNode y = x.children[i];

        BTreeNode z = new BTreeNode(y.t, y.numberOfFields, y.isLeaf, t - 1);

        for (int j = 0; j < t - 1; j++) {
            z.keys[j] = y.keys[j + t];
            z.fieldContainer.setRow(j, y.fieldContainer.getRow(j + t));
            //free memory
            y.keys[j + t] = null;
            y.fieldContainer.setRow(j + t, null);
        }

        if (!y.isLeaf) {
            for (int j = 0; j < t; j++) {
                z.children[j] = y.children[j + t];
                z.childrenUuids[j] = y.childrenUuids[j + t];
                //free memory
                y.children[j + t] = null;
                y.childrenUuids[j + t] = null;
            }
        }

        y.n = t - 1;

        for (int j = x.n; j >= i + 1; j--) {
            x.children[j + 1] = x.children[j];
            x.childrenUuids[j + 1] = x.childrenUuids[j];
        }

        x.children[i + 1] = z;
        x.childrenUuids[i + 1] = z.uuid;

        for (int j = x.n - 1; j >= i; j--) {
            x.keys[j + 1] = x.keys[j];
            x.fieldContainer.setRow(j + 1, x.fieldContainer.getRow(j));
        }

        x.keys[i] = y.keys[t - 1];
        //free memory
        y.keys[t - 1] = null;
        x.fieldContainer.setRow(i, y.fieldContainer.getRow(t - 1));
        x.n = x.n + 1;

        //write to disk
        BTreeNode.writeToDisk(y.uuid, y);
        BTreeNode.writeToDisk(x.uuid, x);
        BTreeNode.writeToDisk(z.uuid, z);
    }

    @ReadFromDiskRequired
    @WriteToDiskRequired
    public void insert(Field key, Field[] values) throws WriteToDiskError, ReadFromDiskError {
        if (root == null) {
            BTreeNode x = new BTreeNode(t, numberOfFields, true, 0);
            BTreeNode.writeToDisk(x.uuid, x);
            root = x;
            rootUuid = x.uuid;
        }

        BTreeNode r = root;
        if (root.n == 2*t - 1) {
            BTreeNode s = new BTreeNode(t, numberOfFields, false, 0);
            root = s;
            s.children[0] = r;
            s.childrenUuids[0] = r.uuid;
            splitChild(s, 0);
            insertNonFull(s, key, values);
        } else {
            insertNonFull(r, key, values);
        }
    }

    @ReadFromDiskRequired
    @WriteToDiskRequired
    public void remove(Field key) throws ReadFromDiskError, WriteToDiskError {
        if (root == null) {
            log.error("Remove from empty tree");
            return;
        }
        root.remove(key);
        BTreeNode.writeToDisk(root.uuid, root);

        if (root.n == 0) {
            BTreeNode old = root;
            if (root.isLeaf) {
                root = null;
            } else {
                root = BTreeNode.readFromDisk(root.childrenUuids[0]);
            }
        }
    }


    @ReadFromDiskRequired
    @WriteToDiskRequired
    private void insertNonFull(BTreeNode x, Field key, Field[] values) throws ReadFromDiskError, WriteToDiskError {
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

            //write to disk;
            BTreeNode.writeToDisk(x.uuid, x);
        } else {
            while (i >= 0 && key.compareTo(x.keys[i]) < 0) {
                i--;
            }
            i++;

            //read from disk;
            x.children[i] = BTreeNode.readFromDisk(x.childrenUuids[i]);

            if (x.children[i].n == 2*t - 1) {
                splitChild(x, i);

                if (key.compareTo(x.keys[i]) > 0) {
                    i++;
                }
            }

            insertNonFull(x.children[i], key, values);
        }

        x.children = new BTreeNode[2*t];
    }
}
