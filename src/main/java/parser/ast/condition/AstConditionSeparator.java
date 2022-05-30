package parser.ast.condition;

public class AstConditionSeparator implements AstConditionPart {
    /**
     * 2 типа -
     * SEPARATOR_OPEN
     * SEPARATOR_CLOSE
     */
    private final String type;

    public AstConditionSeparator(String type) {
        this.type = type;
    }

    @Override
    public String getType() {
        return type;
    }
}
