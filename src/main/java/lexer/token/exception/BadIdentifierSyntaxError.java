package lexer.token.exception;

import java.io.IOException;

public class BadIdentifierSyntaxError extends IOException {
    private final int line;
    private final int position;

    public BadIdentifierSyntaxError(int line, int position) {
        this.line = line;
        this.position = position;
    }

    @Override
    public String toString() {
        return "Bad Identifier Syntax at (" + line + ", " + position + ");\n";
    }
}
