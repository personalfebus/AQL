package parser.ast.name;

public class AstFieldReference {
    private final AstFieldName fieldName;
    //private final AstTableName tableName; to improve think

    public AstFieldReference(AstFieldName fieldName) {
        this.fieldName = fieldName;
    }

    public AstFieldName getFieldName() {
        return fieldName;
    }
}
