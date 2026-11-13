package domain.exception;

/**
 * Thrown when attempting to create an entity that already exists.
 */
public class DuplicateEntityException extends Exception {
    public DuplicateEntityException(String message) {
        super(message);
    }
}
