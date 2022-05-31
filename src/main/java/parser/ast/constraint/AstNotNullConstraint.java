package parser.ast.constraint;

import database.structures.TableFieldInformation;
import parser.ast.AstType;
import database.exception.TypeMismatchException;

public class AstNotNullConstraint implements AstConstraint {
    @Override
    public String getType() {
        return AstNotNullConstraint.class.getName();
    }

    @Override
    public void emplaceConstraint(TableFieldInformation information, AstType type) throws TypeMismatchException {
        information.setNotNull(true);
    }
}
