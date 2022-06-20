package parser.ast.constraint;

import database.structures.TableFieldInformation;
import parser.ast.name.AstType;
import parser.ast.name.AstFieldName;
import parser.ast.name.AstTableName;
import database.exception.TypeMismatchException;

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

    @Override
    public void emplaceConstraint(TableFieldInformation information, AstType type) throws TypeMismatchException {
        information.setForeign(true);
        information.setReferenceSchemaName(referencedTableName.getSchemaName());
        information.setReferencedTableName(referencedTableName.getTableName());
        information.setReferencedFieldName(referenceFieldName.getName());
    }
}
