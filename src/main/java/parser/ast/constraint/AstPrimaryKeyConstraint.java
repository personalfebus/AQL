package parser.ast.constraint;

public class AstPrimaryKeyConstraint implements AstConstraint {
    @Override
    public String getType() {
        return AstPrimaryKeyConstraint.class.getName();
    }
}
