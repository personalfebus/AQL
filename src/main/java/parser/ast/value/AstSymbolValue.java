package parser.ast.value;

public class AstSymbolValue implements AstValue {
    private final char value;

    public AstSymbolValue(char value) {
        this.value = value;
    }

    public char getValue() {
        return value;
    }

    @Override
    public String getType() {
        return AstSymbolValue.class.getName();
    }
}
