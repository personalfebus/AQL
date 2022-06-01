package parser.ast.condition;

import parser.ast.arithmetic.AstArithExpr;

public class AstConditionConstantRValue implements AstConditionPart {
    private final AstArithExpr arithExpr;

    public AstConditionConstantRValue(AstArithExpr arithExpr) {
        this.arithExpr = arithExpr;
    }

    @Override
    public String getType() {
        return AstConditionConstantRValue.class.getName();
    }

    public AstArithExpr getArithExpr() {
        return arithExpr;
    }
}
