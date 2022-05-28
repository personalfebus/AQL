package database.field;

public class IntField implements Field, Comparable<Field> {
    private int value;

    public IntField(int value) {
        this.value = value;
    }

    @Override
    public String getClazz() {
        return int.class.getName();
    }

    @Override
    public int compareTo(Field other) {
        return Field.super.compareTo(other);
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