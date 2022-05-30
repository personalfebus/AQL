package parser.ast.constraint;

public class AstUniqueConstraint implements AstConstraint {
    @Override
    public String getType() {
        return AstUniqueConstraint.class.getName();
    }
}
