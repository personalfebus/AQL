package parser.ast.function.query;

import parser.ast.condition.AstCondition;
import parser.ast.function.AstFunction;
import parser.ast.name.AstFieldName;
import parser.ast.name.AstTableName;

import java.util.List;

//to improve
public class AstSelectFunction implements AstFunction {
    private final List<AstFieldName> columnList;
    private final AstTableName tableName;
    private final AstCondition condition;

    public AstSelectFunction(List<AstFieldName> columnList, AstTableName tableName, AstCondition condition) {
        this.columnList = columnList;
        this.tableName = tableName;
        this.condition = condition;
    }

    public List<AstFieldName> getColumnList() {
        return columnList;
    }

    public AstTableName getTableName() {
        return tableName;
    }

    public AstCondition getCondition() {
        return condition;
    }

    @Override
    public String getType() {
        return AstSelectFunction.class.getName();
    }
}
