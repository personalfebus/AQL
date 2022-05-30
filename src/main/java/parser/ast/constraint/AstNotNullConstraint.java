package parser.ast.constraint;

public class AstNotNullConstraint implements AstConstraint {
    @Override
    public String getType() {
        return AstNotNullConstraint.class.getName();
    }
}
