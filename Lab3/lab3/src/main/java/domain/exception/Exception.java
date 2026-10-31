package domain.exception;

/**
 * Base unchecked exception for all application-specific errors.
 */
public class Exception extends RuntimeException{
    public Exception(String message) {
        super(message);
    }

    public Exception(String message, Throwable cause) {
        super(message, cause);
    }
}
