package parser.ast.function.table;

import database.Database;
import lombok.extern.slf4j.Slf4j;
import parser.ast.function.AstFunction;
import parser.ast.name.AstTableName;

@Slf4j
public class AstDropTableFunction implements AstFunction {
    private final boolean hasIfExistPrefix;
    private final AstTableName tableName;

    public AstDropTableFunction(boolean hasIfExistPrefix, AstTableName tableName) {
        this.hasIfExistPrefix = hasIfExistPrefix;
        this.tableName = tableName;
    }

    public boolean isHasIfExistPrefix() {
        return hasIfExistPrefix;
    }

    public AstTableName getTableName() {
        return tableName;
    }

    @Override
    public String getType() {
        return AstDropTableFunction.class.getName();
    }
    
    @Override
    public void execute(Database database) {
        boolean isPresent = database.hasTableByName(tableName.getSchemaName(), tableName.getTableName());

        if (!isPresent) {
            if (hasIfExistPrefix) {
                log.info("Table does not exists");
            } else {
                log.error("Table does not exists");
            }
        } else {
            database.deleteTable(tableName.getSchemaName(), tableName.getTableName());
            log.error("Table was successfully deleted");
        }
    }
}
