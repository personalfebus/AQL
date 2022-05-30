package parser.ast.function.table.alter;

import parser.ast.name.AstFieldName;

public class AstDropColumnFunction implements AstAlterTableFunctionBody {
    private final boolean hasIfExistPrefix;
    private final AstFieldName fieldName;

    public AstDropColumnFunction(boolean hasIfExistPrefix, AstFieldName fieldName) {
        this.hasIfExistPrefix = hasIfExistPrefix;
        this.fieldName = fieldName;
    }

    public boolean isHasIfExistPrefix() {
        return hasIfExistPrefix;
    }

    public AstFieldName getFieldName() {
        return fieldName;
    }

    @Override
    public String getType() {
        return AstDropColumnFunction.class.getName();
    }
}
