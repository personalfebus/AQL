package parser.ast.condition;

public class AstConditionOperator implements AstConditionPart {
    private final String operator;
    private final int priority; // ! 2 | > < == != >= <= 6 | && 7 | || 8
    private final boolean isRightAssociative;
    private final String subType;

    public AstConditionOperator(String operator) {
        this.operator = operator;

        switch (operator) {
            case "!": {
                priority = 2;
                isRightAssociative = true;
                subType = "UNARY_OPERATOR";
                break;
            }
            case ">":
            case "<":
            case "==":
            case "!=":
            case ">=":
            case "<=": {
                priority = 6;
                isRightAssociative = false;
                subType = "BINARY_OPERATOR";
                break;
            }
            case "&&": {
                priority = 7;
                isRightAssociative = false;
                subType = "BINARY_OPERATOR";
                break;
            }
            case "||":
            default: {
                //WTF
                priority = 9;
                isRightAssociative = false;
                subType = "BINARY_OPERATOR";
            }
        }
    }

    @Override
    public String getType() {
        return AstConditionOperator.class.getName();
    }

    public String getSubType() {
        return subType;
    }

    public int getPriority() {
        return priority;
    }

    public boolean isRightAssociative() {
        return isRightAssociative;
    }
}
