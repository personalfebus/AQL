package parser.ast.constraint;

import database.structures.TableFieldInformation;
import parser.ast.name.AstType;
import database.exception.TypeMismatchException;

public class AstUniqueConstraint implements AstConstraint {
    @Override
    public String getType() {
        return AstUniqueConstraint.class.getName();
    }

    @Override
    public void emplaceConstraint(TableFieldInformation information, AstType type) throws TypeMismatchException {
        information.setNeedsIndex(true);
    }
}
