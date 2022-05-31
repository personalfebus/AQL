package parser.ast.function.table;

import database.Database;
import parser.ast.function.AstFunction;
import parser.ast.name.AstTableName;

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

    //todo
    @Override
    public void execute(Database database) {

    }
}
