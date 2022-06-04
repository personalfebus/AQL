package database.btree.exception;

public class WriteToDiskError extends Exception {
    public WriteToDiskError(Throwable cause) {
        super(cause);
    }
}
