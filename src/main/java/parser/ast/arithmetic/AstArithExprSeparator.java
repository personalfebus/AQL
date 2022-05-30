package parser.ast.arithmetic;

public class AstArithExprSeparator implements AstArithExprPart {
    /**
     * 2 типа -
     * SEPARATOR_OPEN
     * SEPARATOR_CLOSE
     */
    private final String type;

    public AstArithExprSeparator(String type) {
        this.type = type;
    }

    @Override
    public String getType() {
        return type;
    }
}
