package domain.validator;

import domain.Friendship;

public class FriendshipValidator implements Validator<Friendship> {
    /**
     * Validates a friendship relationship.
     * @param entity the friendship to be validated
     * @throws ValidationException if user IDs are null or if a user tries to befriend themselves
     */
    @Override
    public void validate(Friendship entity) throws ValidationException {
        if (entity.getId() == null || entity.getFirstUserId() == null || entity.getSecondUserId() == null) {
            throw new ValidationException("Friendship ID or user IDs must not be null!");
        }
        if (entity.getFirstUserId().equals(entity.getSecondUserId())) {
            throw new ValidationException("A user cannot be friends with themselves!");
        }
    }
}
