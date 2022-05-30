package parser.ast.function;

import parser.ast.AstType;
import parser.ast.constraint.AstConstraint;
import parser.ast.name.AstFieldName;

public class AstColumnDefinition {
    private final AstFieldName name;
    private final AstType type;
    private AstConstraint constraint;

    public AstColumnDefinition(AstFieldName name, AstType type, AstConstraint constraint) {
        this.name = name;
        this.type = type;
        this.constraint = constraint;
    }

    public AstColumnDefinition(AstFieldName name, AstType type) {
        this.name = name;
        this.type = type;
        this.constraint = null;
    }

    public AstFieldName getName() {
        return name;
    }

    public AstType getType() {
        return type;
    }

    public AstConstraint getConstraint() {
        return constraint;
    }
}
