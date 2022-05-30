package parser.ast.constraint;

import parser.ast.name.AstFieldName;
import parser.ast.name.AstTableName;

public class AstForeignKeyConstraint implements AstConstraint {
    private final AstTableName referencedTableName;
    private final AstFieldName referenceFieldName;

    public AstForeignKeyConstraint(AstTableName referencedTableName, AstFieldName referenceFieldName) {
        this.referencedTableName = referencedTableName;
        this.referenceFieldName = referenceFieldName;
    }

    public AstTableName getReferencedTableName() {
        return referencedTableName;
    }

    public AstFieldName getReferenceFieldName() {
        return referenceFieldName;
    }

    @Override
    public String getType() {
        return AstForeignKeyConstraint.class.getName();
    }
}
