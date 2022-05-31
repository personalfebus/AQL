package parser.ast.function.data;

import database.Database;
import parser.ast.condition.AstCondition;
import parser.ast.function.AstFunction;
import parser.ast.name.AstTableName;

import java.util.List;

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

    //todo
    @Override
    public void execute(Database database) {

    }
}
