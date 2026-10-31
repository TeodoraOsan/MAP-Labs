package repository;

import domain.Entity;
import domain.exception.ValidationException;

import java.util.List;

/**
 * Generic interface for persistence operations (CRUD)
 * @param <ID> the type of the entity identifier
 * @param <E>  the type of the entity, extending {@link Entity}
 */
public interface Repository<ID, E extends Entity<ID>> {
    /**
     * Finds an entity by its identifier
     * @param id the identifier of the entity to be found
     * @return the found entity, or null if it does not exist
     * @throws IllegalArgumentException if id is null
     */
    E findOne(ID id);

    /**
     * Returns a list containing all entities
     * @return the list of all entities
     */
    List<E> findAll();

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
     * @return the removed entity, or null if it was not found
     * @throws IllegalArgumentException if id is null
     */
    E delete(ID id);
}
