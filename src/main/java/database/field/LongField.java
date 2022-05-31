package database.field;

import java.util.Objects;

public class LongField implements Field, Comparable<Field> {
    private long value;

    public LongField(long value) {
        this.value = value;
    }

    @Override
    public String getClazz() {
        return "long";
    }

    @Override
    public int compareTo(Field other) {
        return Field.super.compareTo(other);
    }

    public long getValue() {
        return value;
    }

    public double getDoubleValue() {
        return value;
    }

    @Override
    public String toString() {
        return "LongField{" +
                "value=" + value +
                '}';
    }

    @Override
    public boolean equals(Field other) {
        return Field.super.equals(other);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        LongField longField = (LongField) o;
        return value == longField.value;
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }
}
