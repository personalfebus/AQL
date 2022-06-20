package parser.ast.function.query;

import database.Database;
import database.btree.exception.ReadFromDiskError;
import database.exception.TypeMismatchException;
import database.exception.UnknownFieldException;
import database.exception.UnsupportedOperationException;
import database.structures.SelectOutputRow;
import database.structures.Table;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import parser.ast.condition.AstCondition;
import parser.ast.function.AstFunction;
import parser.ast.name.AstFieldName;
import parser.ast.name.AstTableName;

import java.util.List;

//to improve
public class AstSelectFunction implements AstFunction {
    private final static Logger log = LoggerFactory.getLogger(AstSelectFunction.class.getName());

    private final List<AstFieldName> columnList;
    private final AstTableName tableName;
    private final AstCondition condition;

    public AstSelectFunction(List<AstFieldName> columnList, AstTableName tableName, AstCondition condition) {
        this.columnList = columnList;
        this.tableName = tableName;
        this.condition = condition;
    }

    public List<AstFieldName> getColumnList() {
        return columnList;
    }

    public AstTableName getTableName() {
        return tableName;
    }

    public AstCondition getCondition() {
        return condition;
    }

    @Override
    public String getType() {
        return AstSelectFunction.class.getName();
    }

    @Override
    public void execute(Database database) {
        if (database.hasTableByName(tableName.getSchemaName(), tableName.getTableName())) {
            Table table = database.getTableByName(tableName.getSchemaName(), tableName.getTableName());
            try {
                List<SelectOutputRow> rows = table.select(columnList, condition);
                System.out.println(rows);
            } catch (TypeMismatchException | UnknownFieldException e) {
                log.error("Error in select", e);
            } catch (ReadFromDiskError e) {
                log.error("Internal filesystem error occurred during command execution", e);
            } catch (UnsupportedOperationException e) {
                log.info("Unsupported operation");
            }
        } else {
            log.error("Table does not exist");
        }
    }
}
