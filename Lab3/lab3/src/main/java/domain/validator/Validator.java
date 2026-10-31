package domain.validator;

import domain.exception.ValidationException;

/**
 * Generic interface for entity validation.
 * @param <T> the type of the entity to be validated
 */
public interface Validator<T> {
    /**
     * Validates an entity
     * @param entity the entity to be validated
     * @throws ValidationException if the entity is null or invalid
     */
    void validate (T entity) throws ValidationException;
}
