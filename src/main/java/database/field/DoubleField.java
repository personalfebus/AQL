package database.field;

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
