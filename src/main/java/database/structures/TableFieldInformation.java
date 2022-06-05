package database.structures;

import database.field.Field;
import database.structures.value.FieldDefaultValue;

import java.io.Serializable;

public class TableFieldInformation implements Serializable {
    private String fieldName;
    private FieldDefaultValue defaultValue;
    private String fieldType;
    private boolean isPrimary;
    private boolean isForeign;
    private boolean needsIndex;
    private boolean isNotNull;
    private int indexPosition;
    private boolean isPresentInInsert;
    private int insertPosition;

    public TableFieldInformation(String fieldName, FieldDefaultValue defaultValue, String fieldType, int indexPosition) {
        this.fieldName = fieldName;
        this.defaultValue = defaultValue;
        this.fieldType = fieldType;
        this.indexPosition = indexPosition;
    }

    public TableFieldInformation(String fieldName, String fieldType) {
        this.fieldName = fieldName;
        this.fieldType = fieldType;
        defaultValue = null;
        this.indexPosition = -1;
    }

    public TableFieldInformation(String fieldName, FieldDefaultValue defaultValue, String fieldType) {
        this.fieldName = fieldName;
        this.defaultValue = defaultValue;
        this.fieldType = fieldType;
        this.indexPosition = -1;
    }

    public String getFieldType() {
        return fieldType;
    }

    public int getInsertPosition() {
        return insertPosition;
    }

    public void setInsertPosition(int insertPosition) {
        this.insertPosition = insertPosition;
    }

    public boolean isPresentInInsert() {
        return isPresentInInsert;
    }

    public void setPresentInInsert(boolean presentInInsert) {
        isPresentInInsert = presentInInsert;
    }

    public void setIndexPosition(int indexPosition) {
        this.indexPosition = indexPosition;
    }

    public boolean isNotNull() {
        return isNotNull;
    }

    public void setNotNull(boolean notNull) {
        isNotNull = notNull;
    }

    public boolean isForeign() {
        return isForeign;
    }

    public void setForeign(boolean foreign) {
        isForeign = foreign;
    }

    public boolean isPrimary() {
        return isPrimary;
    }

    public boolean isNeedsIndex() {
        return needsIndex;
    }

    public void setPrimary(boolean primary) {
        isPrimary = primary;
    }

    public void setNeedsIndex(boolean needsIndex) {
        this.needsIndex = needsIndex;
    }

    public boolean hasIndex() {
        return indexPosition >= 0;
    }

    public String getFieldName() {
        return fieldName;
    }

    public boolean hasFieldDefaultValue() {
        return defaultValue != null;
    }

    public FieldDefaultValue getFieldDefaultValue() {
        return defaultValue;
    }

    public Field getDefaultValue() {
        if (defaultValue == null) {
            return null;
        } else {
            return defaultValue.getNext();
        }
    }

    public int getIndexPosition() {
        return indexPosition;
    }

    public void setDefaultValue(FieldDefaultValue defaultValue) {
        this.defaultValue = defaultValue;
    }
}
