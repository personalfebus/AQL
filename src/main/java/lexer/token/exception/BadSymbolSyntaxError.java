package lexer.token.exception;

import java.io.IOException;

public class BadSymbolSyntaxError extends IOException {
    private final int line;
    private final int position;

    public BadSymbolSyntaxError(int line, int position) {
        this.line = line;
        this.position = position;
    }

    @Override
    public String toString() {
        return "Could not interpret symbol at (" + line + ", " + position + ");\n";
    }
}
