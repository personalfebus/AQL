package database.structures.value;

import database.field.CharField;
import database.field.DoubleField;
import database.field.Field;
import parser.ast.value.AstFloatingNumberValue;
import parser.ast.value.AstSymbolValue;

public class CharFieldDefaultValue implements FieldDefaultValue {
    private char value;

    public CharFieldDefaultValue(AstSymbolValue value) {
        this.value = value.getValue();
    }

    @Override
    public Field getNext() {
        return new CharField(value);
    }
}