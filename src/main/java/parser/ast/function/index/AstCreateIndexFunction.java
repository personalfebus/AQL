package parser.ast.function.index;

import parser.ast.function.AstFunction;
import parser.ast.name.AstFieldName;
import parser.ast.name.AstIndexName;
import parser.ast.name.AstIndexType;
import parser.ast.name.AstTableName;

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
}
