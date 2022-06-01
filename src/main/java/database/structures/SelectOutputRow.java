package database.structures;

import database.field.Field;

import java.util.Arrays;

public class SelectOutputRow {
    private final Field[] fields;

    public SelectOutputRow(Field[] fields) {
        this.fields = fields;
    }

    @Override
    public String toString() {
        return "SelectOutputRow{" +
                "fields=" + Arrays.toString(fields) +
                '}';
    }
}
