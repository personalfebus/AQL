package lexer.token;

public class StringToken implements Token {
    private final int line;
    private final int position;
    private final String body;

    public StringToken(int line, int position, String body) {
        this.line = line;
        this.position = position;
        this.body = body;
    }

    @Override
    public String getType() {
        return StringToken.class.getName();
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
