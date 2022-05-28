package database.field;

public class LongField implements Field, Comparable<Field> {
    private long value;

    public LongField(long value) {
        this.value = value;
    }

    @Override
    public String getClazz() {
        return long.class.getName();
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
}
