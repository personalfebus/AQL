package database.structures;

import database.btree.BTree;
import database.btree.cache.CacheBTree;
import database.field.Field;

import java.io.Serializable;
import java.util.UUID;

public class Index implements Serializable {
    private final static String pathPrefix = "binaries/";
    private static final int T = 10;

    private final TableFieldInformation keyInformation;
    private CacheBTree table;
    private String name;

    public Index(TableFieldInformation keyInformation, String name) {
        this.keyInformation = keyInformation;
        table = new CacheBTree(T, 1);
        this.name = name;
    }

    public void insert(Field key, Field value) {
        table.insert(key, new Field[]{value});
    }

    public TableFieldInformation getKeyInformation() {
        return keyInformation;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
