package database.field;

import java.util.Objects;

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
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CharField charField = (CharField) o;
        return value == charField.value;
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }

    @Override
    public String toString() {
        return "CharField{" +
                "value=" + value +
                '}';
    }
}
