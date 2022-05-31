package database.structures.value;

import database.field.Field;
import database.field.LongField;
import database.structures.value.FieldDefaultValue;
import parser.ast.value.AstIntegerNumberValue;

public class LongFieldDefaultValue implements FieldDefaultValue {
    private final long value;

    public LongFieldDefaultValue(AstIntegerNumberValue value) {
        this.value = value.getValue();
    }

    @Override
    public Field getNext() {
        return new LongField(value);
    }
}
