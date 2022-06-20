package parser.ast.function.index;

import database.Database;
import lombok.extern.slf4j.Slf4j;
import parser.ast.function.AstFunction;
import parser.ast.name.AstFieldName;
import parser.ast.name.AstIndexName;
import parser.ast.name.AstIndexType;
import parser.ast.name.AstTableName;

@Slf4j
public class AstCreateIndexFunction implements AstFunction {
    private final boolean hasIfNotExistPrefix;
    private final AstIndexName indexName;
    private final AstTableName tableName;
    private final AstFieldName fieldName;
    private final AstIndexType indexType;

    public AstCreateIndexFunction(boolean hasIfNotExistPrefix, AstIndexName indexName, AstTableName tableName, AstFieldName fieldName, AstIndexType indexType) {
        this.hasIfNotExistPrefix = hasIfNotExistPrefix;
        this.indexName = indexName;
        this.tableName = tableName;
        this.fieldName = fieldName;
        this.indexType = indexType;
    }

    public boolean isHasIfNotExistPrefix() {
        return hasIfNotExistPrefix;
    }

    public AstIndexName getIndexName() {
        return indexName;
    }

    public AstTableName getTableName() {
        return tableName;
    }

    public AstFieldName getFieldName() {
        return fieldName;
    }

    public AstIndexType getIndexType() {
        return indexType;
    }

    @Override
    public String getType() {
        return AstCreateIndexFunction.class.getName();
    }

    @Override
    public void execute(Database database) {
        boolean isPresent = database.hasTableByName(tableName.getSchemaName(), tableName.getTableName());

        if (hasIfNotExistPrefix && !isPresent) {
            log.info("Not table found");
        } else {
            if (isPresent) {
                database.getTableByName(tableName.getSchemaName(), tableName.getTableName()).addIndex(fieldName.getName(), indexName.getName());
                log.info("Index added successfully");
            } else {
                log.error("Not table found");
            }
        }
    }
}
