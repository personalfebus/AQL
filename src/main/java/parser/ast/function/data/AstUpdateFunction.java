package parser.ast.function.data;

import database.Database;
import database.btree.exception.ReadFromDiskError;
import database.btree.exception.WriteToDiskError;
import database.exception.TypeMismatchException;
import database.exception.UnknownFieldException;
import database.exception.UnsupportedOperationException;
import lombok.extern.slf4j.Slf4j;
import parser.ast.condition.AstCondition;
import parser.ast.function.AstFunction;
import parser.ast.name.AstTableName;

import java.util.List;

@Slf4j
public class AstUpdateFunction implements AstFunction {
    private final AstTableName tableName;
    private final List<AstUpdateValue> updateValueList;
    private final AstCondition condition;

    public AstUpdateFunction(AstTableName tableName, List<AstUpdateValue> updateValueList, AstCondition condition) {
        this.tableName = tableName;
        this.updateValueList = updateValueList;
        this.condition = condition;
    }

    public AstTableName getTableName() {
        return tableName;
    }

    public List<AstUpdateValue> getUpdateValueList() {
        return updateValueList;
    }

    public AstCondition getCondition() {
        return condition;
    }

    @Override
    public String getType() {
        return AstUpdateFunction.class.getName();
    }

    @Override
    public void execute(Database database) {
        boolean isPresent = database.hasTableByName(tableName.getSchemaName(), tableName.getTableName());

        if (isPresent) {
            try {
                database.getTableByName(tableName.getSchemaName(), tableName.getTableName()).updateByPrimaryKey(updateValueList, condition);
            } catch (TypeMismatchException e) {
                log.error("Type mismatch error");
            }  catch (UnsupportedOperationException e) {
                log.info("Unsupported operation");
            } catch (WriteToDiskError | ReadFromDiskError e) {
                log.error("Internal filesystem error occurred during command execution", e);
            } catch (UnknownFieldException e) {
                log.error("Unknown field error");
            }
        } else {
            log.error("Table was not found");
        }
    }
}
