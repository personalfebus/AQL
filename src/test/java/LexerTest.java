import lexer.Lexer;
import lexer.token.Token;
import lexer.token.Tokens;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class LexerTest {
    @Test
    public void testLexer() {
        String string = "rename 123.78687 rank123 1;";
        Lexer lexer = new Lexer(string);
        Token token;

        assertTrue(lexer.hasTokens());
        token = lexer.nextToken();
        assertEquals(token.getClass().getName(), Tokens.keywordType);

        assertTrue(lexer.hasTokens());
        token = lexer.nextToken();
        assertEquals(token.getClass().getName(), Tokens.floatingNumberType);

        assertTrue(lexer.hasTokens());
        token = lexer.nextToken();
        assertEquals(token.getClass().getName(), Tokens.identifierType);

        assertTrue(lexer.hasTokens());
        token = lexer.nextToken();
        assertEquals(token.getClass().getName(), Tokens.integerNumberType);

        assertTrue(lexer.hasTokens());
        token = lexer.nextToken();
        assertEquals(token.getClass().getName(), Tokens.operatorType);
    }
}