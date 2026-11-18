package org.example.domain.validator;

import org.example.domain.exception.ValidationException;

import java.util.Objects;

/**
 * Functional interface for entity validation.
 * @param <T> the type of the entity to be validated
 */
@FunctionalInterface
public interface Validator<T> {
    /**
     * Validates an entity
     * @param entity the entity to be validated
     * @throws ValidationException if the entity is null or invalid
     */
    void validate (T entity) throws ValidationException;

    /**
     * Returns a composed validator that performs, in sequence, this operation followed by the after operation.
     * @param after the validator to be executed after this validator
     * @return a composed {@link Validator}
     */
    default Validator<T> andThen(Validator<T> after) {
        Objects.requireNonNull(after);
        return (T entity) -> {
            this.validate(entity);
            after.validate(entity);
        };
    }
}
