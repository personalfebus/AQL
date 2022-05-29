package lexer.token;

public interface Token {
    String getType();
    String getBody();
    int getLine();
    int getPosition();
}
