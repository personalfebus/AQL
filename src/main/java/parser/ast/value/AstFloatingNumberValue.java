package parser.ast.value;

import parser.ast.constraint.AstForeignKeyConstraint;

public class AstFloatingNumberValue implements AstValue {
    private final double value;

    public AstFloatingNumberValue(double value) {
        this.value = value;
    }

    public double getValue() {
        return value;
    }

    @Override
    public String getType() {
        return "double";
    }
}
