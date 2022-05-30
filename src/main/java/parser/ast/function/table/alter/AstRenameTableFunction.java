package parser.ast.function.table.alter;

import parser.ast.name.AstTableName;

public class AstRenameTableFunction implements AstRenameFunction {
    private final boolean hasIfExistPrefix;
    private final AstTableName oldName;
    private final AstTableName newName;

    public AstRenameTableFunction(boolean hasIfExistPrefix, AstTableName oldName, AstTableName newName) {
        this.hasIfExistPrefix = hasIfExistPrefix;
        this.oldName = oldName;
        this.newName = newName;
    }

    public boolean isHasIfExistPrefix() {
        return hasIfExistPrefix;
    }

    public AstTableName getOldName() {
        return oldName;
    }

    public AstTableName getNewName() {
        return newName;
    }

    @Override
    public String getType() {
        return AstRenameTableFunction.class.getName();
    }
}
