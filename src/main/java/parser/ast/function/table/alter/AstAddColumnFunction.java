package parser.ast.function.table.alter;

import parser.ast.function.AstColumnDefinition;

public class AstAddColumnFunction implements AstAlterTableFunctionBody {
    private final boolean hasIfNotExistPrefix;
    private final AstColumnDefinition columnDefinition;

    public AstAddColumnFunction(boolean hasIfNotExistPrefix, AstColumnDefinition columnDefinition) {
        this.hasIfNotExistPrefix = hasIfNotExistPrefix;
        this.columnDefinition = columnDefinition;
    }

    public boolean isHasIfNotExistPrefix() {
        return hasIfNotExistPrefix;
    }

    public AstColumnDefinition getColumnDefinition() {
        return columnDefinition;
    }

    @Override
    public String getType() {
        return AstAddColumnFunction.class.getName();
    }
}
