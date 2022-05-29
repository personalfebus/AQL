package lexer.token.exception;

import java.io.IOException;

public class BadStringClosureError extends IOException {
    private final int line;
    private final int position;

    public BadStringClosureError(int line, int position) {
        this.line = line;
        this.position = position;
    }

    @Override
    public String toString() {
        return "String Closure Error at (" + line + ", " + position + ");\n";
    }
}