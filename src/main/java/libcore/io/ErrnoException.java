package libcore.io;

/**
 * Stub from AOSP to be able to retrieve connection error codes.
 */
public final class ErrnoException extends Exception {
    private final String functionName;
    public final int errno;

    public ErrnoException(String functionName, int errno) {
        this.functionName = functionName;
        this.errno = errno;
    }
}