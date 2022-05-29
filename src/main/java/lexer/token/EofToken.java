package lexer.token;

public class EofToken implements Token {

    public EofToken() {

    }

    @Override
    public String getType() {
        return EofToken.class.getName();
    }

    @Override
    public String getBody() {
        return null;
    }

    @Override
    public int getLine() {
        return 0;
    }

    @Override
    public int getPosition() {
        return 0;
    }
}