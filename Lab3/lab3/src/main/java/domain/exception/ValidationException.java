package domain.exception;

/**
 * Thrown when an entity violates business or format rules.
 */
public class ValidationException extends Exception{
    public ValidationException (String message) {
        super(message);
    }
}
