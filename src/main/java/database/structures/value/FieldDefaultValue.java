package database.structures.value;

import database.field.Field;

import java.io.Serializable;

public interface FieldDefaultValue extends Serializable {
    Field getNext();
}
