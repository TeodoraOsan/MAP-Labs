package domain.exception;

/**
 * Thrown when an entity with a specific identifier cannot be found.
 */
public class EntityNotFoundException extends Exception {
    public EntityNotFoundException(String message) {
        super(message);
    }
}
