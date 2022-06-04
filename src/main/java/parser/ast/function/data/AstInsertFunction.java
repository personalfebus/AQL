package parser.ast.function.data;

import database.Database;
import database.btree.exception.ReadFromDiskError;
import database.btree.exception.WriteToDiskError;
import database.exception.FieldNumberMismatchException;
import database.exception.NotNullFieldNotInsideInsertException;
import database.exception.TypeMismatchException;
import database.exception.UnknownFieldException;
import database.structures.Table;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import parser.ast.function.AstFunction;
import parser.ast.function.table.AstCreateTableFunction;
import parser.ast.name.AstFieldName;
import parser.ast.name.AstTableName;

import java.util.List;

public class AstInsertFunction implements AstFunction {
    private final static Logger log = LoggerFactory.getLogger(AstInsertFunction.class.getName());

    private final AstTableName tableName;
    private final List<AstFieldName> columnList;
    private final List<AstInsertRow> rowList;

    public AstInsertFunction(AstTableName tableName, List<AstFieldName> columnList, List<AstInsertRow> rowList) {
        this.tableName = tableName;
        this.columnList = columnList;
        this.rowList = rowList;
    }

    public AstTableName getTableName() {
        return tableName;
    }

    public List<AstFieldName> getColumnList() {
        return columnList;
    }

    public List<AstInsertRow> getRowList() {
        return rowList;
    }

    @Override
    public String getType() {
        return AstInsertFunction.class.getName();
    }

    @Override
    public void execute(Database database) {
        if (database.hasTableByName(tableName.getSchemaName(), tableName.getTableName())) {
            Table table = database.getTableByName(tableName.getSchemaName(), tableName.getTableName());

            try {
                table.insertValues(columnList, rowList);
                log.info("Values inserted successfully into table {}", tableName);
            } catch (UnknownFieldException | FieldNumberMismatchException | TypeMismatchException | NotNullFieldNotInsideInsertException e) {
                log.error("Value insert crushed", e);
            } catch (WriteToDiskError | ReadFromDiskError e) {
                log.error("Internal filesystem error occurred during command execution", e);
            }
        } else {
            log.error("Table does not exist");
        }
    }
}
