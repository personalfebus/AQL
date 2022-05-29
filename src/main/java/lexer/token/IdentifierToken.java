package lexer.token;

import lexer.token.exception.BadIdentifierSyntaxError;

public class IdentifierToken implements Token {
    private final int line;
    private final int position;
    private final String body;

    public IdentifierToken(int line, int position, String body) throws BadIdentifierSyntaxError {
        this.line = line;
        this.position = position;
        this.body = body;
        assertBody();
    }

    private void assertBody() throws BadIdentifierSyntaxError {
        // [A-Za-z\_] [A-Za-z0-9\_]*
        for (int i = 0; i < body.length(); i++) {
            if (!isValidSymbol(body.charAt(i))) {
                throw new BadIdentifierSyntaxError(line, position);
            }
        }
    }

    private boolean isValidSymbol(char a) {
        return (a >= 'a') && (a <= 'z') ||
                (a >= 'A') && (a <= 'Z') ||
                (a >= '0') && (a <= '9') ||
                (a == '_');
    }

    @Override
    public String getType() {
        return IdentifierToken.class.getName();
    }

    @Override
    public String getBody() {
        return body;
    }

    @Override
    public int getLine() {
        return line;
    }

    @Override
    public int getPosition() {
        return position;
    }
}
