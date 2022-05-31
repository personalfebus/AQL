package parser.ast.constraint;

import database.structures.TableFieldInformation;
import database.structures.value.CharFieldDefaultValue;
import database.structures.value.IntFieldDefaultValue;
import database.structures.value.LongFieldDefaultValue;
import database.structures.value.StringFieldDefaultValue;
import parser.ast.AstType;
import parser.ast.value.*;
import database.exception.TypeMismatchException;

public class AstDefaultConstraint implements AstConstraint {
    private final AstValue value;

    public AstDefaultConstraint(AstValue value) {
        this.value = value;
    }

    public AstValue getValue() {
        return value;
    }

    @Override
    public String getType() {
        return AstDefaultConstraint.class.getName();
    }

    @Override
    public void emplaceConstraint(TableFieldInformation information, AstType type) throws TypeMismatchException {
        if (value.getType().equalsIgnoreCase(AstValues.astIntegerNumberType)
        && type.getMappedName().equalsIgnoreCase("int")) {
            information.setDefaultValue(new IntFieldDefaultValue((AstIntegerNumberValue) value));
        } else if (value.getType().equalsIgnoreCase(AstValues.astIntegerNumberType)
                && type.getMappedName().equalsIgnoreCase("long")) {
            information.setDefaultValue(new LongFieldDefaultValue((AstIntegerNumberValue) value));
        }  else if (value.getType().equalsIgnoreCase(AstValues.astStringType)
                && type.getMappedName().equalsIgnoreCase("string")) {
            information.setDefaultValue(new StringFieldDefaultValue((AstStringValue) value));
        }  else if (value.getType().equalsIgnoreCase(AstValues.astSymbolType)
                && type.getMappedName().equalsIgnoreCase("char")) {
            information.setDefaultValue(new CharFieldDefaultValue((AstSymbolValue) value));
        } else {
            throw new TypeMismatchException();
        }
    }
}
