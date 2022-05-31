package database.exception;

import java.io.IOException;

public class NotNullFieldNotInsideInsertException extends IOException {
    private final int i;

    public NotNullFieldNotInsideInsertException(int i) {
        this.i = i;
    }
}
