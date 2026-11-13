package domain;

import java.util.Objects;

/**
 * Generic base class representing an entity in the domain model
 * Each entity is uniquely identified by an identifier of type {@code ID}
 * @param <ID> the type of the unique identifier for this entity
 */
public class Entity<ID> {
    private ID id;

    /**
     * Gets the unique identifier of the entity
     * @return the identifier of the entity
     */
    public ID getId() {
        return id;
    }

    /**
     * Sets the unique identifier of the entity
     * @param id the identifier to be assigned to the entity
     */
    public void setId (ID id) {
        this.id = id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Entity<?> entity = (Entity<?>) o;
        return Objects.equals(id, entity.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
