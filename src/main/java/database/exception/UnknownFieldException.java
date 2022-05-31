package database.exception;

import java.io.IOException;

public class UnknownFieldException  extends IOException {
    private final int i;

    public UnknownFieldException(int i) {
        this.i = i;
    }
}