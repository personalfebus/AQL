package lexer.token.exception;

import java.io.IOException;

public class UnexpectedSymbolError extends IOException {
    private final int line;
    private final int position;

    public UnexpectedSymbolError(int line, int position) {
        this.line = line;
        this.position = position;
    }

    @Override
    public String toString() {
        return "Unexpected Symbol at (" + line + ", " + position + ");\n";
    }
}