package database.field;

public interface Field {
    String getClazz();
    default int compareTo(Field other) {
        return Fields.compare(this, other);
    }
}
