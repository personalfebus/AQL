package database.field;

public class StringField implements Field {
    private String value;

    public StringField(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    @Override
    public String getClazz() {
        return "string";
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
        return "StringField{" +
                "value='" + value + '\'' +
                '}';
    }
}
