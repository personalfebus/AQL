package parser.ast.constraint;

import parser.ast.value.AstValue;

public class AstDefaultConstraint implements AstConstraint {
    private final AstValue value;

    public AstDefaultConstraint(AstValue value) {
        this.value = value;
    }

    public AstValue getValue() {
        return value;
    }

    @Override
    public String getType() {
        return AstDefaultConstraint.class.getName();
    }
}
