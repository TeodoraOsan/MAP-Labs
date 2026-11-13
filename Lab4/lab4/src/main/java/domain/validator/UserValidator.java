package domain.validator;

import domain.User;
import domain.exception.ValidationException;

/**
 * Validator responsible for verifying the integrity and correctness of a {@link User} entity.
 */
public class UserValidator implements Validator<User> {
    /**
     * Validates the data of a user, verifying that the ID is strictly positive, the first name and last name are not empty and contain only letters
     * @param entity the user to be validated
     * @throws ValidationException if the entity is null, the ID is non-positive, or first name/last name are empty/invalid
     */
    @Override
    public void validate(User entity) throws ValidationException {
        if (entity == null) {
            throw new ValidationException("User entity must not be null!");
        }
        StringBuilder errors = new StringBuilder();
        if (entity.getId() == null || entity.getId() <= 0) {
            errors.append("ID must be strictly positive! ");
        }
        if (entity.getFirstName() == null || entity.getFirstName().trim().isEmpty()) {
            errors.append("First name must not be empty! ");
        } else if(!isOnlyLetters(entity.getFirstName().trim()))
            errors.append("Must contain only letters! ");
        if (entity.getLastName() == null || entity.getLastName().trim().isEmpty()) {
            errors.append("Last name must not be empty! ");
        } else if (!isOnlyLetters(entity.getLastName().trim()))
            errors.append("Must contain only letters! ");
        if (!errors.isEmpty()) {
            throw new ValidationException(errors.toString().trim());
        }
    }

    private boolean isOnlyLetters(String name) {
        return name.matches("^[a-zA-Z -]+$");
    }
}
