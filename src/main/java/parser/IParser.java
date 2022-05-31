package parser;

import parser.ast.AstProgram;
import parser.exception.BadArithmeticExpressionException;
import parser.exception.BadConditionExpressionException;
import parser.exception.SyntaxException;

public interface IParser {
    //return ast-tree head
    //pass the input into parser inside
    AstProgram parse() throws SyntaxException, BadArithmeticExpressionException, BadConditionExpressionException;
}
