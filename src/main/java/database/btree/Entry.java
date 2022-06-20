package database.btree;

import database.field.Field;

public class Entry {
    private Field key;
    private Field[] values;

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

    public void setKey(Field key) {
        this.key = key;
    }

    public void setValues(Field[] values) {
        this.values = values;
    }
}
