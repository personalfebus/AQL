package database.field;

import java.util.Objects;

public class IntField implements Field, Comparable<Field> {
    private int value;

    public IntField(int value) {
        this.value = value;
    }

    @Override
    public String getClazz() {
        return "int";
    }

    @Override
    public int compareTo(Field other) {
        return Field.super.compareTo(other);
    }

    @Override
    public boolean equals(Field other) {
        return Field.super.equals(other);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        IntField intField = (IntField) o;
        return value == intField.value;
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }

    public int getValue() {
        return value;
    }

    public long getLongValue() {
        return value;
    }

    public double getDoubleValue() {
        return value;
    }

    @Override
    public String toString() {
        return "IntField{" +
                "value=" + value +
                '}';
    }
}
