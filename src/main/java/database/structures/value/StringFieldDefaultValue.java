package database.structures.value;

import database.field.DoubleField;
import database.field.Field;
import database.field.StringField;
import parser.ast.value.AstFloatingNumberValue;
import parser.ast.value.AstStringValue;

public class StringFieldDefaultValue implements FieldDefaultValue {
    private String value;

    public StringFieldDefaultValue(AstStringValue value) {
        this.value = value.getValue();
    }

    @Override
    public Field getNext() {
        return new StringField(value);
    }
}