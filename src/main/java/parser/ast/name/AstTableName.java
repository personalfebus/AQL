package parser.ast.name;

public class AstTableName {
    private final String schemaName;
    private final String tableName;

    public AstTableName(String schemaName, String tableName) {
        this.schemaName = schemaName;
        this.tableName = tableName;
    }

    public String getSchemaName() {
        return schemaName;
    }

    public String getTableName() {
        return tableName;
    }
}
