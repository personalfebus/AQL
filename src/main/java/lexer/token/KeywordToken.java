package lexer.token;

public class KeywordToken implements Token {
    private final int line;
    private final int position;
    private final String body;

    public KeywordToken(int line, int position, String body) {
        this.line = line;
        this.position = position;
        this.body = body;
    }

    @Override
    public String getType() {
        return KeywordToken.class.getName();
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