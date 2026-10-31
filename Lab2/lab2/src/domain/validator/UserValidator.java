package domain.validator;

import domain.User;

public class UserValidator implements Validator<User> {
    /**
     * Validates the data of a user, verifying that the first name and last nameare not empty and contain only letters
     * @param entity the user to be validated
     * @throws ValidationException if the first name or last name is null, empty or contains non-alphabetic characters
     */
    @Override
    public void validate(User entity) throws ValidationException {
        StringBuilder errors = new StringBuilder();
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
