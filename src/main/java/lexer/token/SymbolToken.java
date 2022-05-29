package lexer.token;

import lexer.token.exception.BadSymbolSyntaxError;

public class SymbolToken implements Token {
    private final int line;
    private final int position;
    private final char body;

    public SymbolToken(int line, int position, String body) throws BadSymbolSyntaxError {
        this.line = line;
        this.position = position;
        this.body = interpretBody(body);
    }

    private char interpretBody(String str) throws BadSymbolSyntaxError {
        if (str.length() == 0) return 0;
        if (str.length() == 1) return str.charAt(0);
        if (str.length() < 3) throw new BadSymbolSyntaxError(line, position);
        assertChar(str, 0, '\\');
        assertChar(str, 1, 'u');
        String code = str.substring(2);
        return (char) Integer.parseInt(code);
    }

    private void assertChar(String str, int i, char a) throws BadSymbolSyntaxError {
        if (str.charAt(i) != a) throw new BadSymbolSyntaxError(line, position);
    }

    @Override
    public String getType() {
        return SymbolToken.class.getName();
    }

    @Override
    public String getBody() {
        return String.valueOf(body);
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
