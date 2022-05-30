package parser.ast.function.data;

import parser.ast.name.AstFieldName;
import parser.ast.value.AstValue;

public class AstUpdateValue {
    private final AstFieldName fieldName;
    private final AstValue value;

    public AstUpdateValue(AstFieldName fieldName, AstValue value) {
        this.fieldName = fieldName;
        this.value = value;
    }

    public AstFieldName getFieldName() {
        return fieldName;
    }

    public AstValue getValue() {
        return value;
    }
}
