package parser.ast.constraint;

import database.structures.TableFieldInformation;
import parser.ast.name.AstType;
import database.exception.TypeMismatchException;

public interface AstConstraint {
    String getType();
    void emplaceConstraint(TableFieldInformation information, AstType type) throws TypeMismatchException;
}
