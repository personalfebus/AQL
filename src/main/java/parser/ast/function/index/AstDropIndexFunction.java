package parser.ast.function.index;

import database.Database;
import parser.ast.function.AstFunction;
import parser.ast.name.AstIndexName;
import parser.ast.name.AstTableName;

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

    //todo
    @Override
    public void execute(Database database) {

    }
}
