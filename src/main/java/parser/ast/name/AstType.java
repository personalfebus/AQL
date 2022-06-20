package parser.ast.name;

public class AstType {
    private final String typeName;

    public AstType(String typeName) {
        this.typeName = typeName;
    }

    public String getTypeName() {
        return typeName;
    }

    public boolean isSerial() {
        return typeName.contains("serial");
    }

    public String getMappedName() {
        if (typeName.equalsIgnoreCase("serial")) {
            return "int";
        } else if (typeName.equalsIgnoreCase("bigserial")) {
            return "long";
        } else {
            return typeName;
        }
    }
}
