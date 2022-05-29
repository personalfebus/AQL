package lexer.token.exception;

import java.io.IOException;

public class BadSymbolClosureError extends IOException {
    private final int line;
    private final int position;

    public BadSymbolClosureError(int line, int position) {
        this.line = line;
        this.position = position;
    }

    @Override
    public String toString() {
        return "Symbol Closure Error at (" + line + ", " + position + ");\n";
    }
}