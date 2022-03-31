package lexer;

import lexer.token.Token;

public interface ILexer {
    boolean hasTokens();
    Token nextToken();
}
