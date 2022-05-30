package parser.ast.function.table.alter;

import parser.ast.name.AstIndexName;

public class AstRenameIndexFunction implements AstRenameFunction {
    private final boolean hasIfExistPrefix;
    private final AstIndexName oldName;
    private final AstIndexName newName;

    public AstRenameIndexFunction(boolean hasIfExistPrefix, AstIndexName oldName, AstIndexName newName) {
        this.hasIfExistPrefix = hasIfExistPrefix;
        this.oldName = oldName;
        this.newName = newName;
    }

    public boolean isHasIfExistPrefix() {
        return hasIfExistPrefix;
    }

    public AstIndexName getOldName() {
        return oldName;
    }

    public AstIndexName getNewName() {
        return newName;
    }

    @Override
    public String getType() {
        return AstRenameIndexFunction.class.getName();
    }
}
