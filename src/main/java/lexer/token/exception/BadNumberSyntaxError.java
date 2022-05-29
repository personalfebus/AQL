package lexer.token.exception;

import java.io.IOException;

public class BadNumberSyntaxError extends IOException {
    private final int line;
    private final int position;

    public BadNumberSyntaxError(int line, int position) {
        this.line = line;
        this.position = position;
    }

    @Override
    public String toString() {
        return "Bad Number Syntax at (" + line + ", " + position + ");\n";
    }
}
