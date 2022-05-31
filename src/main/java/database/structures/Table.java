package database.structures;

import database.btree.BTree;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

public class Table {
    private static final Logger log = LoggerFactory.getLogger(Table.class.getName());
    private static final int T = 10;

    private BTree table;
    private List<Index> indices;
    private String tableName;
    private String schemaName;
    private int numberOfFields;
    private List<TableFieldInformation> fieldInformation;

    public Table(String tableName, String schemaName, List<TableFieldInformation> fieldInformation) {
        this.numberOfFields = fieldInformation.size();
        table = new BTree(T,numberOfFields);
        indices = new ArrayList<>();
        this.tableName = tableName;
        this.schemaName = schemaName;
        this.fieldInformation = fieldInformation;
    }

    //todo
}
