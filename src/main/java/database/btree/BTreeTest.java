package database.btree;

import database.field.DoubleField;
import database.field.Field;
import database.field.IntField;
import database.field.LongField;
import org.junit.jupiter.api.Test;

import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;

class BTreeTest {
    @Test
    public void testInsert() {
        Random random = new Random();
        BTree bTree = new BTree(2, 2, 0);

        for (int i = 0; i < 100; i++) {
            //(int)Math.round(random.nextDouble()*100)
            Field key1 = new IntField(i);
            Field[] values1 = {new DoubleField(random.nextDouble()*i), new LongField(i)};
            bTree.insert(key1, values1);
            System.out.println("================================= " + i + " =================================");
            bTree.traverse();
        }

        bTree.traverse();
        assertTrue(true);
    }
}