package database.structures.value;

import database.field.Field;
import database.field.IntField;
import database.structures.value.FieldDefaultValue;

public class SerialDefaultValue implements FieldDefaultValue {
    private int value = 0;

    @Override
    public Field getNext() {
        return new IntField(value++);
    }
}