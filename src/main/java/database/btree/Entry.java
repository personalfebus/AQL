package database.btree;

import database.field.Field;

public class Entry {
    private final Field key;
    private final Field[] values;

    public Entry(Field key, Field[] values) {
        this.key = key;
        this.values = values;
    }

    public Field getKey() {
        return key;
    }

    public Field[] getValues() {
        return values;
    }
}
