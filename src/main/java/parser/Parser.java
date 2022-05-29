package parser;

import lexer.ILexer;
import lexer.Lexer;
import lexer.token.EofToken;
import lexer.token.Token;
import lexer.token.Tokens;
import parser.ast.function.AstFunction;
import parser.ast.AstProgram;
import parser.exception.SyntaxException;

public class Parser implements IParser {
    private final ILexer lexer;
    private Token currentToken;

    public Parser(String input) {
        lexer = new Lexer(input);
    }

    public Parser(ILexer lexer) {
        this.lexer = lexer;
    }

    private void nextToken() {
        if (lexer.hasTokens()) {
            currentToken = lexer.nextToken();
        } else {
            currentToken = new EofToken();
        }
    }

    @Override
    public AstProgram parse(String input) {
        return parseProgram();
    }

    private AstProgram parseProgram() {
        nextToken();
        AstProgram program = new AstProgram();

        for (;;) {
            AstFunction function = parseFunction();
            program.addFunction(function);

            if (currentToken.getType().equals(Tokens.eofType)) {
                break;
            }
        }

        return program;
    }

    //todo interface subclasses + parse
    private AstFunction parseFunction() {
        return null;
    }

    private void assertTokenType(String type) throws SyntaxException {
        if (!currentToken.getType().equalsIgnoreCase(type)) {
            throw new SyntaxException(type, currentToken.getBody(), currentToken.getLine(), currentToken.getPosition());
        }
    }

    private void assertTokenBody(String body) throws SyntaxException {
        if (!currentToken.getBody().equalsIgnoreCase(body)) {
            throw new SyntaxException(body, currentToken.getBody(), currentToken.getLine(), currentToken.getPosition());
        }
    }
}
