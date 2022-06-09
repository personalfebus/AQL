package database.btree;

import database.btree.annotation.CacheOnlyOperation;
import database.btree.annotation.WriteToDiskRequired;
import database.btree.annotation.ReadFromDiskRequired;
import database.btree.exception.ReadFromDiskError;
import database.btree.exception.WriteToDiskError;
import database.field.Field;
import lombok.Cleanup;
import lombok.extern.slf4j.Slf4j;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.UUID;

@Slf4j
class BTreeNode implements Serializable {
    private final static String pathPrefix = "binaries/";

    //Uuid для записи на диск
    public final UUID uuid;
    public final Field[] keys;
    //Контейнер значений соответствующих ключам (transient?)
    public FieldContainer fieldContainer;
    //minimum number of children
    public final int t;
    //количество полей
    public final int numberOfFields;
    //дочерние вершины дерева
    public transient BTreeNode[] children;
    public final UUID[] childrenUuids;
    //количество ключей в ноде на данный момент
    public int n;
    //является ли вершина лиственной
    boolean isLeaf;

    public BTreeNode(int t, int numberOfFields, boolean isLeaf, int n) {
        this.t = t;
        this.numberOfFields = numberOfFields;
        this.isLeaf = isLeaf;
        keys = new Field[2*t - 1];
        children = new BTreeNode[2*t];
        childrenUuids = new UUID[2*t];
        this.fieldContainer = new FieldContainer(2*t - 1, numberOfFields);
        this.n = n;
        this.uuid = UUID.randomUUID();
    }

    public static BTreeNode readFromDisk(UUID uuid) throws ReadFromDiskError {
        try {
            @Cleanup FileInputStream fileInputStream = new FileInputStream(pathPrefix + uuid.toString());
            @Cleanup ObjectInputStream objectInputStream = new ObjectInputStream(fileInputStream);
            BTreeNode result = (BTreeNode) objectInputStream.readObject();
            result.children = new BTreeNode[2*result.t];
            return result;
        } catch (IOException | ClassNotFoundException e) {
            throw new ReadFromDiskError(e);
        }
    }

    public static void writeToDisk(UUID uuid, BTreeNode node) throws WriteToDiskError {
        try {
            @Cleanup FileOutputStream fileOutputStream = new FileOutputStream(pathPrefix + uuid.toString());
            @Cleanup ObjectOutputStream objectOutputStream = new ObjectOutputStream(fileOutputStream);
            objectOutputStream.writeObject(node);
        } catch (IOException e) {
            throw new WriteToDiskError(e);
        }
    }

    @ReadFromDiskRequired
    public void traverse() throws ReadFromDiskError {
        for (int i = 0; i < n; i++) {
            if (!isLeaf) {
                children[i] = readFromDisk(childrenUuids[i]);
                children[i].traverse();
                children[i] = null;
            }
            System.out.println("-----------------");
            System.out.println("Uuid: " + uuid);
            System.out.println("Key: " + keys[i]);

            if (n != keys.length) {
                System.out.println("AYOOOOOOO N: " + n + "; T: " + t + "; Len: " + keys.length + "; isLeaf: " + isLeaf);
            }

            System.out.println("Values:");
            for (int j = 0; j < numberOfFields; j++) {
                System.out.println(fieldContainer.get(i, j));
            }
            System.out.println("-----------------");
        }

        if (!isLeaf) {
            children[n] = readFromDisk(childrenUuids[n]);
            children[n].traverse();
            children[n] = null;
        }
    }

    @ReadFromDiskRequired
    public BTreeNode searchByKey(Field key) throws ReadFromDiskError {
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

        children[i] = readFromDisk(childrenUuids[i]);
        BTreeNode result = children[i].searchByKey(key);
        children[i] = null;
        return result;
    }

    /**
     * Получить пару ключ-значения по ключу
     * @param key Ключ
     * @return Ключ-значения
     */
    @ReadFromDiskRequired
    public Entry getEntryByKey(Field key) throws ReadFromDiskError {
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

        children[i] = readFromDisk(childrenUuids[i]);
        Entry result = children[i].getEntryByKey(key);
        children[i] = null;
        return result;
    }

    /**
     * Найти позицию ключа в вершине дерева
     * @param key Ключ
     * @return Позиция
     */
    @CacheOnlyOperation
    private int findKey(Field key) {
        int idx = 0;
        while (idx < n && keys[idx].compareTo(key) < 0) {
            idx++;
        }
        return idx;
    }

    /**
     * Удалить ключ и соответсвующие ему значения из вершины
     * @param key Ключ
     */
    @ReadFromDiskRequired
    @WriteToDiskRequired
    public void remove(Field key) throws ReadFromDiskError, WriteToDiskError {
        int idx = findKey(key);

        if (idx < n && keys[idx].compareTo(key) == 0) {
            if (isLeaf) {
                removeFromLeaf(idx);
            } else {
                removeFromNonLeaf(idx);
            }
        } else {
            if (isLeaf) {
                log.info("Key {} does not exist in BTree", key);
            }

            children[idx] = readFromDisk(childrenUuids[idx]);
            boolean flag = (idx == n);
            if (children[idx].n < t) {
                fill(idx);
            }

            if (flag && idx > n) {
                BTreeNode x = readFromDisk(childrenUuids[idx - 1]);
                x.remove(key);
                writeToDisk(x.uuid, x);
//                children[idx - 1].remove(key);
            } else {
                BTreeNode x = readFromDisk(childrenUuids[idx]);
                x.remove(key);
                writeToDisk(x.uuid, x);
//                children[idx].remove(key);
            }
        }
    }

    @CacheOnlyOperation
    private void removeFromLeaf(int idx) {
        for (int i = idx + 1; i < n; ++i) {
            keys[i - 1] = keys[i];
            fieldContainer.setRow(i - 1, fieldContainer.getRow(i));
        }
        //free memory
        keys[n - 1] = null;
        fieldContainer.setRow(n - 1, null);

        n--;
    }

    @ReadFromDiskRequired
    @WriteToDiskRequired
    private void removeFromNonLeaf(int idx) throws ReadFromDiskError, WriteToDiskError {
        Field k = keys[idx];
        children[idx] = readFromDisk(childrenUuids[idx]);
        children[idx + 1] = readFromDisk(childrenUuids[idx + 1]);

        if (children[idx].n >= t) {
            Entry predEntry = getPredEntry(idx);
            keys[idx] = predEntry.getKey();
            fieldContainer.setRow(idx, predEntry.getValues());
            children[idx].remove(predEntry.getKey());
            writeToDisk(children[idx].uuid, children[idx]);
        } else if (children[idx + 1].n >= t) {
            Entry succEntry = getSuccEntry(idx);
            keys[idx] = succEntry.getKey();
            fieldContainer.setRow(idx, succEntry.getValues());
            children[idx + 1].remove(succEntry.getKey());
            writeToDisk(children[idx + 1].uuid, children[idx + 1]);
        } else {
            merge(idx);
            children[idx].remove(k);
            writeToDisk(children[idx].uuid, children[idx]);
        }

        children[idx] = null;
        children[idx + 1] = null;
    }

    @WriteToDiskRequired
    private void merge(int idx) throws WriteToDiskError {
        BTreeNode child = children[idx];
        BTreeNode sibling = children[idx + 1];
//        BTreeNode child = readFromDisk(childrenUuids[idx]);
//        BTreeNode sibling = readFromDisk(childrenUuids[idx + 1]);

        child.keys[t - 1] = keys[idx];
        child.fieldContainer.setRow(t - 1, fieldContainer.getRow(idx));

        for (int i = 0; i < sibling.n; i++) {
            child.keys[i + t] = sibling.keys[i];
            child.fieldContainer.setRow(i + t, sibling.fieldContainer.getRow(i));
        }

        if (!child.isLeaf) {
            for (int i = 0; i <= sibling.n; i++) {
                child.children[i + t] = sibling.children[i];
                child.childrenUuids[i + t] = sibling.childrenUuids[i];
            }
        }

        for (int i = idx + 1; i < n; i++) {
            keys[i - 1] = keys[i];
            fieldContainer.setRow(i - 1, fieldContainer.getRow(i));
        }
        //free memory
        keys[n - 1] = null;
        fieldContainer.setRow(n - 1, null);

        for (int i = idx + 2; i <= n; i++) {
            children[i - 1] = children[i];
            childrenUuids[i - 1] = childrenUuids[i];
        }
        //free memory
        children[n] = null;
        childrenUuids[n] = null;

        child.n += sibling.n + 1;
        n--;

        BTreeNode.writeToDisk(child.uuid, child);
        BTreeNode.writeToDisk(sibling.uuid, sibling);
    }

    @ReadFromDiskRequired
    @WriteToDiskRequired
    private void fill(int idx) throws ReadFromDiskError, WriteToDiskError {
        if (idx != 0) {
            children[idx - 1] = readFromDisk(childrenUuids[idx - 1]);
        }

        children[idx] = readFromDisk(childrenUuids[idx]);

        if (idx != n) {
            children[idx + 1] = readFromDisk(childrenUuids[idx + 1]);
        }

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

        //free memory
        if (idx != 0) {
            children[idx - 1] = null;
        }

        children[idx] = null;

        if (idx != n) {
            children[idx + 1] = null;
        }
    }

    @ReadFromDiskRequired
    @WriteToDiskRequired
    private void borrowFromNext(int idx) throws ReadFromDiskError, WriteToDiskError {
//        BTreeNode child = children[idx];
//        BTreeNode sibling = children[idx + 1];
        BTreeNode child = readFromDisk(childrenUuids[idx]);
        BTreeNode sibling = readFromDisk(childrenUuids[idx + 1]);

        child.keys[child.n] = keys[idx];
        child.fieldContainer.setRow(child.n, fieldContainer.getRow(idx));

        if (!child.isLeaf) {
            child.children[child.n - 1] = sibling.children[0];
            child.childrenUuids[child.n - 1] = sibling.childrenUuids[0];
        }

        keys[idx] = sibling.keys[0];
        fieldContainer.setRow(idx, sibling.fieldContainer.getRow(0));

        for (int i = 1; i < sibling.n; i++) {
            sibling.keys[i - 1] = sibling.keys[i];
            sibling.fieldContainer.setRow(i - 1, sibling.fieldContainer.getRow(i));
        }
        //free memory
        sibling.keys[sibling.n - 1] = null;
        sibling.fieldContainer.setRow(sibling.n - 1, null);

        if (!sibling.isLeaf) {
            for (int i = 1; i <= sibling.n; i++) {
                sibling.children[i - 1] = sibling.children[i];
                sibling.childrenUuids[i - 1] = sibling.childrenUuids[i];
            }
            //free memory
            sibling.children[sibling.n] = null;
            sibling.childrenUuids[sibling.n] = null;
        }

        child.n = child.n + 1;
        sibling.n = sibling.n - 1;
        writeToDisk(child.uuid, child);
        writeToDisk(sibling.uuid, sibling);
    }

    @WriteToDiskRequired
    @ReadFromDiskRequired
    private void borrowFromPrev(int idx) throws ReadFromDiskError, WriteToDiskError {
//        BTreeNode child = children[idx];
//        BTreeNode sibling = children[idx - 1];
        BTreeNode child = readFromDisk(childrenUuids[idx]);
        BTreeNode sibling = readFromDisk(childrenUuids[idx - 1]);

        for (int i = child.n - 1; i >= 0; i--) {
            child.keys[i + 1] = child.keys[i];
            child.fieldContainer.setRow(i + 1, child.fieldContainer.getRow(i));
        }

        if (!child.isLeaf) {
            for (int i = child.n; i >= 0; i--) {
                child.children[i + 1] = child.children[i];
                child.childrenUuids[i + 1] = child.childrenUuids[i];
            }
        }

        child.keys[0] = keys[idx - 1];
        child.fieldContainer.setRow(0, fieldContainer.getRow(idx - 1));

        if (!child.isLeaf) {
            child.children[0] = sibling.children[sibling.n];
            child.childrenUuids[0] = sibling.childrenUuids[sibling.n];
        }
        //free memory
        sibling.children[sibling.n] = null;
        sibling.childrenUuids[sibling.n] = null;

        keys[idx - 1] = sibling.keys[sibling.n - 1];
        fieldContainer.setRow(idx - 1, sibling.fieldContainer.getRow(sibling.n - 1));
        //free memory
        sibling.keys[sibling.n - 1] = null;
        sibling.fieldContainer.setRow(sibling.n - 1, null);

        child.n = child.n + 1;
        sibling.n = sibling.n - 1;
        writeToDisk(child.uuid, child);
        writeToDisk(sibling.uuid, sibling);
    }

    @ReadFromDiskRequired
    private Entry getPredEntry(int idx) throws ReadFromDiskError {
        //children[idx] should already be loaded into memory
        BTreeNode current = children[idx];

        while (!current.isLeaf) {
            current = BTreeNode.readFromDisk(current.childrenUuids[current.n]);
        }

        return new Entry(current.keys[current.n - 1], current.fieldContainer.getRow(current.n - 1));
    }

    @ReadFromDiskRequired
    private Entry getSuccEntry(int idx) throws ReadFromDiskError {
        //children[idx + 1] should already be loaded into memory
        BTreeNode current = children[idx + 1];

        while (!current.isLeaf) {
            current = BTreeNode.readFromDisk(current.childrenUuids[0]);
        }

        return new Entry(current.keys[0], current.fieldContainer.getRow(0));
    }
}
