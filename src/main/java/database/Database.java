package database;

import database.btree.exception.ReadFromDiskError;
import database.btree.exception.WriteToDiskError;
import database.structures.Table;
import lombok.Cleanup;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Database implements Serializable {
    private final List<Table> tables;
    private final String databaseName;
    private final UUID databaseUuid;
    private final static String pathPrefix = "binaries/";

    public Database(String databaseName) {
        this.databaseName = databaseName;
        this.tables = new ArrayList<>();
        this.databaseUuid = UUID.nameUUIDFromBytes(databaseName.getBytes(StandardCharsets.UTF_8));
    }

    public Database() {
        this.databaseName = "aql";
        this.tables = new ArrayList<>();
        this.databaseUuid = UUID.nameUUIDFromBytes(databaseName.getBytes(StandardCharsets.UTF_8));
    }

    public Database(List<Table> tables) {
        this.tables = tables;
        this.databaseName = "aql";
        this.databaseUuid = UUID.nameUUIDFromBytes(databaseName.getBytes(StandardCharsets.UTF_8));
    }

    public Database(List<Table> tables, String databaseName) {
        this.tables = tables;
        this.databaseName = databaseName;
        this.databaseUuid = UUID.nameUUIDFromBytes(databaseName.getBytes(StandardCharsets.UTF_8));
    }

    public static Database readFromDisk(UUID uuid) throws ReadFromDiskError {
        try {
            @Cleanup FileInputStream fileInputStream = new FileInputStream(pathPrefix + uuid.toString());
            @Cleanup ObjectInputStream objectInputStream = new ObjectInputStream(fileInputStream);
            return (Database) objectInputStream.readObject();
        } catch (IOException | ClassNotFoundException e) {
            throw new ReadFromDiskError(e);
        }
    }

    public static void writeToDisk(UUID uuid, Database database) throws WriteToDiskError {
        try {
            @Cleanup FileOutputStream fileOutputStream = new FileOutputStream(pathPrefix + uuid.toString());
            @Cleanup ObjectOutputStream objectOutputStream = new ObjectOutputStream(fileOutputStream);
            objectOutputStream.writeObject(database);
        } catch (IOException e) {
            throw new WriteToDiskError(e);
        }
    }

    public void addTable(Table table) {
        tables.add(table);
    }

    public boolean hasTableByName(String schemaName, String tableName) {
        boolean isPresent = false;

        for (Table table : tables) {
            if (table.getTableName().equalsIgnoreCase(tableName) && table.getSchemaName().equalsIgnoreCase(schemaName)) {
                isPresent = true;
                break;
            }
        }

        return isPresent;
    }

    public Table getTableByName(String schemaName, String tableName) {
        for (Table table : tables) {
            if (table.getTableName().equalsIgnoreCase(tableName) && table.getSchemaName().equalsIgnoreCase(schemaName)) {
                return table;
            }
        }

        return null;
    }

    public UUID getDatabaseUuid() {
        return databaseUuid;
    }
}
