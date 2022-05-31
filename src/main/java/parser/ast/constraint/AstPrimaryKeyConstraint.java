package parser.ast.constraint;

import database.structures.TableFieldInformation;
import parser.ast.AstType;
import database.exception.TypeMismatchException;

public class AstPrimaryKeyConstraint implements AstConstraint {
    @Override
    public String getType() {
        return AstPrimaryKeyConstraint.class.getName();
    }

    @Override
    public void emplaceConstraint(TableFieldInformation information, AstType type) throws TypeMismatchException {
        information.setPrimary(true);
    }
}
