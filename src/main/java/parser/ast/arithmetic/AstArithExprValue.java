package parser.ast.arithmetic;

import parser.ast.value.AstValue;

public class AstArithExprValue implements AstArithExprPart {
    private final AstValue value;

    public AstArithExprValue(AstValue value) {
        this.value = value;
    }

    @Override
    public String getType() {
        return AstArithExprValue.class.getName();
    }

    public AstValue getValue() {
        return value;
    }
}
