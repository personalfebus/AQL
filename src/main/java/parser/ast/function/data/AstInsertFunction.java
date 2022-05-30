package parser.ast.function.data;

import parser.ast.function.AstFunction;
import parser.ast.name.AstFieldName;
import parser.ast.name.AstTableName;

import java.util.List;

public class AstInsertFunction implements AstFunction {
    private final AstTableName tableName;
    private final List<AstFieldName> columnList;
    private final List<AstInsertRow> rowList;

    public AstInsertFunction(AstTableName tableName, List<AstFieldName> columnList, List<AstInsertRow> rowList) {
        this.tableName = tableName;
        this.columnList = columnList;
        this.rowList = rowList;
    }

    public AstTableName getTableName() {
        return tableName;
    }

    public List<AstFieldName> getColumnList() {
        return columnList;
    }

    public List<AstInsertRow> getRowList() {
        return rowList;
    }

    @Override
    public String getType() {
        return AstInsertFunction.class.getName();
    }
}
