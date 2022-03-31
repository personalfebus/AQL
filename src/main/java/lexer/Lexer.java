package lexer;

import lexer.token.Token;

public class Lexer implements ILexer{
    private final String input;
    private int position;
    private int line;
    private int linePosition;
    private Token currentToken;

    public Lexer(String input) {
        this.input = input;
        this.position = 0;
        this.line = 1;
        this.linePosition = 1;
    }

    @Override
    public boolean hasTokens() {
        if (position >= input.length()) {
            return false;
        } else {
            try {
                nextToken();
                return currentToken != null;
            } catch (Exception ex) {
                //replace println with logs
                System.out.println(ex);
                return hasTokens();
            }
        }
    }

    @Override
    public Token nextToken() {
        //todo
        return null;
    }
}
