package parser.ast.function.table;

import parser.ast.function.AstFunction;
import parser.ast.function.table.alter.AstAlterTableFunctionBody;
import parser.ast.name.AstTableName;

//todo
public class AstAlterTableFunction implements AstFunction {
    private final boolean hasIfExistPrefix;
    private final AstTableName tableName;
    private final AstAlterTableFunctionBody body;

    public AstAlterTableFunction(boolean hasIfExistPrefix, AstTableName tableName, AstAlterTableFunctionBody body) {
        this.hasIfExistPrefix = hasIfExistPrefix;
        this.tableName = tableName;
        this.body = body;
    }

    public boolean isHasIfExistPrefix() {
        return hasIfExistPrefix;
    }

    public AstTableName getTableName() {
        return tableName;
    }

    public AstAlterTableFunctionBody getBody() {
        return body;
    }

    @Override
    public String getType() {
        return AstAlterTableFunction.class.getName();
    }
}