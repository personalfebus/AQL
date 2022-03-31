package database.structures;

import database.field.Field;

import java.util.List;

public class BTreeNode {
    private List<Long> keys;
    private List<List<Field>> payload;

}
