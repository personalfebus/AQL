package parser.ast.condition;

import parser.ast.value.AstValue;

public class AstConditionConstant implements AstConditionPart {
    private final AstValue value;

    public AstConditionConstant(AstValue value) {
        this.value = value;
    }

    @Override
    public String getType() {
        return AstConditionConstant.class.getName();
    }
}
