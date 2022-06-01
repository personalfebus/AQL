package parser.ast.condition;

import parser.exception.BadConditionExpressionException;

import java.util.*;

public class AstCondition implements AstConditionPart {
    /**
     * выражение из операторов и значений в обратной польской нотации
     */
    private List<AstConditionPart> parts;
    private Deque<AstConditionPart> stack;

    public AstCondition() {
        parts = new ArrayList<>();
        stack = new ArrayDeque<>();
    }

    public void addPart(AstConditionPart part) throws BadConditionExpressionException {
        if (part.getType().equals(AstConditionParts.astConditionOperatorType)) {
            AstConditionOperator operator = (AstConditionOperator) part;

            if (operator.getSubType().equals("UNARY_OPERATOR")) {
                stack.push(part);
            } else if (operator.getSubType().equals("BINARY_OPERATOR")) {
                while (stack.peekFirst() != null && stack.peekFirst().getType().equals(AstConditionParts.astConditionOperatorType)) {
                    AstConditionOperator operator2 = (AstConditionOperator) stack.peekFirst();
                    if (operator2.getPriority() < operator.getPriority()
                            || (operator2.getPriority() == operator.getPriority() && !operator.isRightAssociative())) {
                        parts.add(stack.pop());
                    } else break;
                }
                stack.push(part);
            } else {
                //WTF
                throw new BadConditionExpressionException();
            }
        } else if (part.getType().equals(AstConditionParts.astConditionType)) {
            System.out.println("WTF");
            throw new BadConditionExpressionException();
        } else if (part.getType().equals("SEPARATOR_OPEN")) {
            stack.push(part);
        } else if (part.getType().equals("SEPARATOR_CLOSE")) {
            while (stack.peekFirst() != null && !stack.peekFirst().getType().equals("SEPARATOR_OPEN")) {
                parts.add(stack.pop());
            }

            if (stack.peekFirst() == null) {
                System.out.println("NOT CLOSED SEPARATORS");
                throw new BadConditionExpressionException();
            }

            stack.pop();
        } else {
            parts.add(part);
        }
    }

    public void emptyStack() throws BadConditionExpressionException {
        while (!stack.isEmpty()) {
            if (!stack.peekFirst().getType().equals(AstConditionParts.astConditionOperatorType)) {
                throw new BadConditionExpressionException();
            } else parts.add(stack.pop());
        }
    }

    @Override
    public String getType() {
        return AstCondition.class.getName();
    }

    public List<AstConditionPart> getParts() {
        return parts;
    }
}
