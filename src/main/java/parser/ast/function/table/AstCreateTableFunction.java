package parser.ast.function.table;

import parser.ast.function.AstFunction;
import parser.ast.function.AstColumnDefinition;

import java.util.List;

public class AstCreateTableFunction implements AstFunction {
    private final boolean hasNotExistPrefix;
    private final List<AstColumnDefinition> columnDefinitionList;

    public AstCreateTableFunction(boolean hasNotExistPrefix, List<AstColumnDefinition> columnDefinitionList) {
        this.hasNotExistPrefix = hasNotExistPrefix;
        this.columnDefinitionList = columnDefinitionList;
    }

    public boolean isHasNotExistPrefix() {
        return hasNotExistPrefix;
    }

    public List<AstColumnDefinition> getColumnDefinitionList() {
        return columnDefinitionList;
    }

    public void addColumnDefinition(AstColumnDefinition columnDefinition) {
        columnDefinitionList.add(columnDefinition);
    }

    @Override
    public String getType() {
        return AstCreateTableFunction.class.getName();
    }
}
