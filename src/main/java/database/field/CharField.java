package database.field;

public class CharField implements Field {
    private char value;

    public CharField(char value) {
        this.value = value;
    }

    public char getValue() {
        return value;
    }

    @Override
    public String getClazz() {
        return "char";
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
    public String toString() {
        return "CharField{" +
                "value=" + value +
                '}';
    }
}
