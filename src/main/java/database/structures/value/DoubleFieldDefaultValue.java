package database.structures.value;

import database.field.DoubleField;
import database.field.Field;
import parser.ast.value.AstFloatingNumberValue;

public class DoubleFieldDefaultValue implements FieldDefaultValue {
    private double value;

    public DoubleFieldDefaultValue(AstFloatingNumberValue value) {
        this.value = value.getValue();
    }

    @Override
    public Field getNext() {
        return new DoubleField(value);
    }
}
