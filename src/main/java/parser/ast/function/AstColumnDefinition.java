package parser.ast.function;

import database.structures.TableFieldInformation;
import database.structures.value.BigSerialDefaultValue;
import database.structures.value.SerialDefaultValue;
import parser.ast.name.AstType;
import parser.ast.constraint.AstConstraint;
import parser.ast.name.AstFieldName;
import database.exception.TypeMismatchException;

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

    public TableFieldInformation getInformation() throws TypeMismatchException {
        TableFieldInformation information = new TableFieldInformation(name.getName(), type.getMappedName());
        if (constraint != null) {
            constraint.emplaceConstraint(information, type);
        }

        if (type.isSerial()) {
            if (type.getMappedName().equals("int")) {
                information.setDefaultValue(new SerialDefaultValue());
            } else if (type.getMappedName().equals("long")) {
                information.setDefaultValue(new BigSerialDefaultValue());
            }
        }
        return information;
    }
}
