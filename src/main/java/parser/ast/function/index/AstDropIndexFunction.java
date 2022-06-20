package parser.ast.function.index;

import database.Database;
import database.structures.Index;
import database.structures.Table;
import lombok.extern.slf4j.Slf4j;
import parser.ast.function.AstFunction;
import parser.ast.name.AstIndexName;
import parser.ast.name.AstTableName;

@Slf4j
public class AstDropIndexFunction implements AstFunction {
    private final boolean hasIfExistPrefix;
    private final AstIndexName indexName;
    private final AstTableName tableName;

    public AstDropIndexFunction(boolean hasIfExistPrefix, AstIndexName indexName, AstTableName tableName) {
        this.hasIfExistPrefix = hasIfExistPrefix;
        this.indexName = indexName;
        this.tableName = tableName;
    }

    public boolean isHasIfExistPrefix() {
        return hasIfExistPrefix;
    }

    public AstIndexName getIndexName() {
        return indexName;
    }

    public AstTableName getTableName() {
        return tableName;
    }

    @Override
    public String getType() {
        return AstDropIndexFunction.class.getName();
    }

    @Override
    public void execute(Database database) {
        boolean isTablePresent = database.hasTableByName(tableName.getSchemaName(), tableName.getTableName());

        if (!isTablePresent) {
            if (hasIfExistPrefix) {
                log.info("Table does not exists");
            } else {
                log.error("Table does not exists");
            }
        } else {
            Table table = database.getTableByName(tableName.getSchemaName(), tableName.getTableName());
            table.removeIndexByName(indexName.getName());
        }
    }
}
