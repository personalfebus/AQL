package database.structures.value;

import database.field.Field;
import database.field.LongField;

import java.io.Serializable;

public class BigSerialDefaultValue implements FieldDefaultValue, Serializable {
    private long value = 0L;

    @Override
    public Field getNext() {
        return new LongField(value++);
    }
}