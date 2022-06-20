package parser.ast.function.data;

import database.Database;
import database.btree.exception.ReadFromDiskError;
import database.btree.exception.WriteToDiskError;
import database.exception.TypeMismatchException;
import database.exception.UnsupportedOperationException;
import lombok.extern.slf4j.Slf4j;
import parser.ast.condition.AstCondition;
import parser.ast.function.AstFunction;
import parser.ast.name.AstTableName;

@Slf4j
public class AstDeleteFunction implements AstFunction {
    private final AstTableName tableName;
    private final AstCondition condition;

    public AstDeleteFunction(AstTableName tableName, AstCondition condition) {
        this.tableName = tableName;
        this.condition = condition;
    }

    public AstTableName getTableName() {
        return tableName;
    }

    public AstCondition getCondition() {
        return condition;
    }

    @Override
    public String getType() {
        return AstDeleteFunction.class.getName();
    }

    @Override
    public void execute(Database database) {
        boolean isPresent = database.hasTableByName(tableName.getSchemaName(), tableName.getTableName());

        if (isPresent) {
            try {
                database.getTableByName(tableName.getSchemaName(), tableName.getTableName()).delete(condition);
                log.info("Elements were successfully deleted");
            } catch (UnsupportedOperationException e) {
                log.info("Unsupported operation");
            } catch (WriteToDiskError | ReadFromDiskError e) {
                log.error("Internal filesystem error occurred during command execution", e);
            } catch (TypeMismatchException e) {
                log.error("Type mismatch error");
            }
        } else {
            log.info("No table for deletion found");
        }
    }
}
