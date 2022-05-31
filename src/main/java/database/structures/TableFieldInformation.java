package database.structures;

import database.field.Field;
import database.structures.value.FieldDefaultValue;

public class TableFieldInformation {
    private String fieldName;
    private FieldDefaultValue defaultValue;
    private int indexPosition;

    public TableFieldInformation(String fieldName, FieldDefaultValue defaultValue, int indexPosition) {
        this.fieldName = fieldName;
        this.defaultValue = defaultValue;
        this.indexPosition = indexPosition;
    }

    public TableFieldInformation(String fieldName) {
        this.fieldName = fieldName;
        defaultValue = null;
    }

    public TableFieldInformation(String fieldName, FieldDefaultValue defaultValue) {
        this.fieldName = fieldName;
        this.defaultValue = defaultValue;
        this.indexPosition = -1;
    }

    public boolean hasIndex() {
        return indexPosition >= 0;
    }

    public String getFieldName() {
        return fieldName;
    }

    public FieldDefaultValue getFieldDefaultValue() {
        return defaultValue;
    }

    public Field getDefaultValue() {
        return defaultValue.getNext();
    }

    public int getIndexPosition() {
        return indexPosition;
    }
}
