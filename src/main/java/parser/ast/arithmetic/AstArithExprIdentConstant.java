package parser.ast.arithmetic;

import parser.ast.name.AstFieldReference;

public class AstArithExprIdentConstant implements AstArithExprPart {
    private final AstFieldReference fieldName;

    public AstArithExprIdentConstant(AstFieldReference fieldName) {
        this.fieldName = fieldName;
    }

    public AstFieldReference getFieldName() {
        return fieldName;
    }

    @Override
    public String getType() {
        return AstArithExprIdentConstant.class.getName();
    }
}
