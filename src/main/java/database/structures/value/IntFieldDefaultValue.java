package database.structures.value;

import database.field.Field;
import database.field.IntField;
import database.structures.value.FieldDefaultValue;
import parser.ast.value.AstIntegerNumberValue;

public class IntFieldDefaultValue implements FieldDefaultValue {
    private final int value;

    public IntFieldDefaultValue(AstIntegerNumberValue value) {
        this.value = (int)value.getValue();
    }

    @Override
    public Field getNext() {
        return new IntField(value);
    }
}
