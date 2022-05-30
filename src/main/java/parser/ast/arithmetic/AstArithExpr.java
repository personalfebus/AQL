package parser.ast.arithmetic;

import parser.exception.BadArithmeticExpressionException;

import java.util.*;

public class AstArithExpr {
    /**
     * выражение из операторов и значений в обратной польской нотации
     */
    private final List<AstArithExprPart> parts;
    private final Deque<AstArithExprPart> stack;

    public AstArithExpr() {
        this.parts = new ArrayList<>();
        this.stack = new ArrayDeque<>();
    }

    public List<AstArithExprPart> getParts() {
        return parts;
    }

    public void addPart(AstArithExprPart part) throws BadArithmeticExpressionException {
        if (part.getType().equals(AstArithExprParts.astArithExprOperatorType)) {
            AstArithExprOperator operator = (AstArithExprOperator)part;

            if (operator.getSubType().equals("UNARY_OPERATOR")) {
                stack.push(part);
            } else if (operator.getSubType().equals("BINARY_OPERATOR")) {
                while (stack.peekLast() != null && stack.peekLast().getType().equals(AstArithExprParts.astArithExprOperatorType)) {
                    AstArithExprOperator operator2 = (AstArithExprOperator)stack.peekLast();
                    if (operator2.getPriority() < operator.getPriority()
                            || (operator2.getPriority() == operator.getPriority() && !operator.isRightAssociative())) {
                        parts.add(stack.pop());
                    } else break;
                }
                stack.push(part);
            } else {
                throw new BadArithmeticExpressionException();
            }
        } else if (part.getType().equals(AstArithExprParts.astArithExprType)) {
            System.out.println("WTF");
            throw new BadArithmeticExpressionException();
        } else if (part.getType().equals("SEPARATOR_OPEN")) {
            stack.push(part);
        } else if (part.getType().equals("SEPARATOR_CLOSE")) {
            while (stack.peekLast() != null && !stack.peekLast().getType().equals("SEPARATOR_OPEN")) {
                parts.add(stack.pop());
            }

            if (stack.peekLast() == null) {
                System.out.println("NOT CLOSED SEPARATORS");
                throw new BadArithmeticExpressionException();
            }
            stack.pop();
        } else {
            parts.add(part);
        }
    }

    public void emptyStack() throws BadArithmeticExpressionException {
        while (!stack.isEmpty()) {
            if (!stack.peekLast().getType().equals(AstArithExprParts.astArithExprOperatorType)) throw new BadArithmeticExpressionException();
            else parts.add(stack.pop());
        }
    }

    public String getType() {
        return AstArithExpr.class.getName();
    }

    @Override
    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }
}
