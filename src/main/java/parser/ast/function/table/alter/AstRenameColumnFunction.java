package parser.ast.function.table.alter;

import parser.ast.name.AstFieldName;

public class AstRenameColumnFunction implements AstRenameFunction {
    private final boolean hasIfExistPrefix;
    private final AstFieldName oldName;
    private final AstFieldName newName;

    public AstRenameColumnFunction(boolean hasIfExistPrefix, AstFieldName oldName, AstFieldName newName) {
        this.hasIfExistPrefix = hasIfExistPrefix;
        this.oldName = oldName;
        this.newName = newName;
    }

    public boolean isHasIfExistPrefix() {
        return hasIfExistPrefix;
    }

    public AstFieldName getOldName() {
        return oldName;
    }

    public AstFieldName getNewName() {
        return newName;
    }

    @Override
    public String getType() {
        return AstRenameColumnFunction.class.getName();
    }
}
