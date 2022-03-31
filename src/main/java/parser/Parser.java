package parser;

import lexer.ILexer;
import lexer.Lexer;
import lexer.token.Token;

public class Parser implements IParser {
    private final ILexer lexer;
    private Token currentToken;

    public Parser(String input) {
        lexer = new Lexer(input);
    }

    @Override
    public void parse(String input) {
        //todo
    }
}
