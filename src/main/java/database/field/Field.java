package database.field;

public interface Field {
    String getClazz();
    default int compareTo(Field other) {
        return Fields.compare(this, other);
    }

    default boolean equals(Field other) {
        if (this == other) return true;
        return this.compareTo(other) == 0;
    }
}
