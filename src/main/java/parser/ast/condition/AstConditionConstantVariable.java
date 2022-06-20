package parser.ast.condition;

import parser.ast.arithmetic.AstArithExpr;

public class AstConditionConstantVariable implements AstConditionPart {
    private final AstArithExpr arithExpr;

    public AstConditionConstantVariable(AstArithExpr arithExpr) {
        this.arithExpr = arithExpr;
    }

    @Override
    public String getType() {
        return AstConditionConstantVariable.class.getName();
    }

    public AstArithExpr getArithExpr() {
        return arithExpr;
    }
}
