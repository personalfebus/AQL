package database.structures.value;

import database.field.Field;
import database.field.LongField;

public class BigSerialDefaultValue implements FieldDefaultValue {
    private long value = 0L;

    @Override
    public Field getNext() {
        return new LongField(value++);
    }
}