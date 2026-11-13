package domain.validator;

import domain.Friendship;
import domain.exception.ValidationException;

/**
 * Validator responsible for verifying the integrity of a {@link Friendship} relationship.
 */
public class FriendshipValidator implements Validator<Friendship> {
    /**
     * Validates a friendship relationship.
     * @param entity the friendship to be validated
     * @throws ValidationException if entity is null, user IDs are null, or a user tries to befriend themselves
     */
    @Override
    public void validate(Friendship entity) throws ValidationException {
        if (entity == null) {
            throw new ValidationException("Friendship entity must not be null!");
        }
        if (entity.getId() == null || entity.getFirstUserId() == null || entity.getSecondUserId() == null) {
            throw new ValidationException("Friendship ID or user IDs must not be null!");
        }
        if (entity.getFirstUserId().equals(entity.getSecondUserId())) {
            throw new ValidationException("A user cannot be friends with themselves!");
        }
    }
}
