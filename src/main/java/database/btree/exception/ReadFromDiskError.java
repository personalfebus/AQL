package database.btree.exception;

public class ReadFromDiskError extends Exception {
    public ReadFromDiskError(Throwable cause) {
        super(cause);
    }
}
