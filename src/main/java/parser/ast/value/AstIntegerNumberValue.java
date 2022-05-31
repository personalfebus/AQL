package parser.ast.value;

public class AstIntegerNumberValue implements AstValue {
    private final long value;

    public AstIntegerNumberValue(long value) {
        this.value = value;
    }

    public long getValue() {
        return value;
    }

    @Override
    public String getType() {
        return "long";
    }
}
