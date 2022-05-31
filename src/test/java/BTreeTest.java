import database.btree.BTree;
import database.btree.Entry;
import database.field.DoubleField;
import database.field.Field;
import database.field.IntField;
import database.field.LongField;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class BTreeTest {
    @Test
    public void testBTree() {
        BTree bTree = new BTree(2, 2);

        for (int i = 0; i < 100; i++) {
            Field key = new IntField(i);
            Field[] values = {new DoubleField(1.0d * i), new LongField(i)};
            bTree.insert(key, values);
        }

        Entry entry = bTree.getEntryByKey(new IntField(5));
        assertEquals(new LongField(5L), entry.getValues()[1]);
        Entry entry1 = bTree.getEntryByKey(new IntField(55));
        assertEquals(new LongField(55L), entry1.getValues()[1]);
        Entry entry2 = bTree.getEntryByKey(new IntField(95));
        assertEquals(new LongField(95L), entry2.getValues()[1]);
        Entry entry3 = bTree.getEntryByKey(new IntField(105));
        assertNull(entry3);

        for (int i = 0; i < 50; i++) {
            Field key1 = new IntField(i);
            bTree.remove(key1);
        }

        Entry entry4 = bTree.getEntryByKey(new IntField(5));
        assertNull(entry4);
        Entry entry5 = bTree.getEntryByKey(new IntField(95));
        assertEquals(new LongField(95L), entry5.getValues()[1]);
    }
}