package repository;

import domain.Entity;
import domain.exception.EntityNotFoundException;
import domain.validator.Validator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * In-memory implementation of the {@link Repository} interface
 * Manages entities of type {@code E} identified by an ID of type {@code ID} using an internal {@link java.util.Map}
 * @param <ID> the type of the entity identifier
 * @param <E>  the type of the entity, extending {@link Entity}
 */
public class InMemoryRepository<ID, E extends Entity<ID>> implements Repository<ID, E> {
    private final Validator<E> validator;
    private final Map<ID, E> entities = new HashMap<>();

    public InMemoryRepository(Validator<E> validator) {
        this.validator = validator;
    }

    /**
     * Finds an entity by its identifier
     * @param id the identifier of the entity to be found
     * @return the found entity, or null if it does not exist
     * @throws IllegalArgumentException if the provided id is null
     */
    @Override
    public E findOne(ID id) {
        if (id == null) {
            throw new IllegalArgumentException("The ID must not be null!");
        }
        return entities.get(id);
    }

    /**
     * Returns a list containing all entities
     * @return the list of all entities
     */
    @Override
    public List<E> findAll() {
        return new ArrayList<>(entities.values());
    }

    private ID generateNextId() {
        long maxId = 1L;
        for (ID id : entities.keySet()) {
            if (id instanceof Long) {
                long current = (Long) id;
                if (current > maxId) {
                    maxId = current;
                }
            }
        }
        return (ID) Long.valueOf(maxId + 1);
    }

    /**
     * Saves an entity in the repository
     * @param entity the entity to be saved
     * @return null if the save operation succeeded, or the existing entity if there is an ID conflict
     * @throws IllegalArgumentException if the entity is null
     */
    @Override
    public E save(E entity) {
        if (entity == null) {
            throw new IllegalArgumentException("The entity must not be null!");
        }
        if (entity.getId() == null) {
            entity.setId(generateNextId());
        }
        if (validator != null) {
            validator.validate(entity);
        }
        if (entities.containsKey(entity.getId())) {
            return entities.get(entity.getId());
        }
        entities.put(entity.getId(), entity);
        return null;
    }

    /**
     * Removes an entity by its identifier
     * @param id the identifier of the entity to be removed
     * @return the removed entity, or null if it was not found
     * @throws IllegalArgumentException if the provided id is null
     */
    @Override
    public E delete(ID id) {
        if (id == null) {
            throw new IllegalArgumentException("The ID must not be null!");
        }
        return entities.remove(id);
    }
}
