package parser.exception;

import java.io.IOException;

public class SyntaxException extends IOException {
    private final String message;

    public SyntaxException(String expected, String found, int line, int position) {
        if (expected == null) {
            message = "Syntax Error: Expected eof, found " + found + "\n";
        } else if (found == null) {
            message = "Bad syntax at (" + line + "," + position + "): Expected " + expected + ", found eof\n";
        } else {
            message = "Bad syntax at (" + line + "," + position + "): Expected " + expected + ", found " + found + "\n";;
        }
    }

    @Override
    public String toString() {
        return message;
    }
}
