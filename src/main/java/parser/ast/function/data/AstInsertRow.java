package parser.ast.function.data;

import parser.ast.value.AstValue;

import java.util.List;

public class AstInsertRow {
    private final List<AstValue> valueList;

    public AstInsertRow(List<AstValue> valueList) {
        this.valueList = valueList;
    }

    public List<AstValue> getValueList() {
        return valueList;
    }
}
