package database.field;

import java.util.Objects;

public class DoubleField implements Field, Comparable<Field> {
    private double value;

    public DoubleField(double value) {
        this.value = value;
    }

    @Override
    public String getClazz() {
        return double.class.getName();
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
        DoubleField that = (DoubleField) o;
        return Double.compare(that.value, value) == 0;
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }

    public double getValue() {
        return value;
    }

    @Override
    public String toString() {
        return "DoubleField{" +
                "value=" + value +
                '}';
    }
}
