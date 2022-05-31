package parser.ast.value;

public class AstStringValue implements AstValue {
    private final String value;

    public AstStringValue(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    @Override
    public String getType() {
        return "string";
    }
}
