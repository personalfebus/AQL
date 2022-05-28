package database.btree;

import database.field.Field;

import java.io.*;

public class FieldContainer implements Serializable {
    private final Field[][] fields;

    private final static String pathPrefix = "binaries/";

    public static FieldContainer factory(String path) throws IOException, ClassNotFoundException {
        FileInputStream fileInputStream = new FileInputStream(pathPrefix + path);
        ObjectInputStream objectInputStream = new ObjectInputStream(fileInputStream);
        return (FieldContainer) objectInputStream.readObject();
    }

    public FieldContainer(Field[][] fields) {
        this.fields = fields;
    }

    public FieldContainer(int n, int m) {
        fields = new Field[n][m];
    }

    public void set(int i, int j, Field field) {
        fields[i][j] = field;
    }

    public Field get(int i, int j) {
        return fields[i][j];
    }
}
