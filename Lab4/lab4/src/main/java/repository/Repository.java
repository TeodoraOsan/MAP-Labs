package repository;

import domain.Entity;
import domain.exception.ValidationException;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * Generic interface for persistence operations (CRUD)
 * @param <ID> the type of the entity identifier
 * @param <E>  the type of the entity, extending {@link Entity}
 */
public interface Repository<ID, E extends Entity<ID>> {
    /**
     * Finds an entity by its identifier
     * @param id the identifier of the entity to be found
     * @return an {@link Optional} containing the removed entity, or {@link Optional#empty()} if it was not found
     * @throws IllegalArgumentException if id is null
     */
    Optional<E> findOne(ID id);

    /**
     * Returns a list containing all entities
     * @return the list of all entities
     */
    List<E> findAll();

    /**
     * Finds and filters entities according to a given predicate.
     * @param filter predicate used to evaluate matches
     * @return list of entities matching the filter predicate
     */
    default List<E> findAll(Predicate<E> filter) {
        Objects.requireNonNull(filter);
        return findAll().stream()
                .filter(filter)
                .collect(Collectors.toList());
    }

    /**
     * Saves an entity in the repository.
     * If the entity does not have an ID, an ID is assigned before saving.
     * @param entity the entity to be saved
     * @return null if the save operation succeeded, or the existing entity if there is an ID conflict
     * @throws ValidationException if the entity is not valid
     * @throws IllegalArgumentException if entity is null
     */
    E save(E entity);

    /**
     * Removes an entity by its identifier
     * @param id the identifier of the entity to be removed
     * @return an {@link Optional} containing the removed entity, or {@link Optional#empty()} if it was not found
     * @throws IllegalArgumentException if id is null
     */
    Optional<E> delete(ID id);
}
