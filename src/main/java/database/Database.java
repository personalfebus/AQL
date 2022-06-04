package database;

import database.structures.Table;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Database {
    private final List<Table> tables;
    private final String databaseName;
    private final UUID databaseUuid;

    public Database(String databaseName) {
        this.databaseName = databaseName;
        this.tables = new ArrayList<>();
        this.databaseUuid = UUID.randomUUID();
    }

    public Database() {
        this.databaseName = "aql";
        this.tables = new ArrayList<>();
        this.databaseUuid = UUID.randomUUID();
    }

    public Database(List<Table> tables) {
        this.tables = tables;
        this.databaseName = "aql";
        this.databaseUuid = UUID.randomUUID();
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
