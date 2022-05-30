package parser.ast.function.data;

import parser.ast.condition.AstCondition;
import parser.ast.function.AstFunction;
import parser.ast.name.AstTableName;

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
}
