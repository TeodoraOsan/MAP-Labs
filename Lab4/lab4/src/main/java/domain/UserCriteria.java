package domain;

import java.util.Objects;
import java.util.function.Predicate;

/**
 * Utility predicates for querying User entities.
 */
public final class UserCriteria {
    private UserCriteria() {}

    /**
     * Matches users whose first name starts with the given prefix (case-insensitive).
     * @param prefix the prefix to test
     * @return predicate testing the prefix
     */
    public static Predicate<User> firstNameStartsWith(String prefix) {
        return user -> user != null && user.getFirstName() != null &&
                user.getFirstName().toLowerCase().startsWith(prefix.toLowerCase());
    }

    /**
     * Matches users whose name matches the given name (case-insensitive).
     * @param lastName the exact last name to match
     * @return predicate testing last name equality
     */
    public static Predicate<User> hasFullName(String firstName, String lastName) {
        Objects.requireNonNull(firstName);
        Objects.requireNonNull(lastName);
        return user -> user != null
                && firstName.equalsIgnoreCase(user.getFirstName())
                && lastName.equalsIgnoreCase(user.getLastName());
    }

    /**
     * Matches users who have an even identifier.
     * Method reference target: UserCriteria::hasEvenId
     * @param user the user to test
     * @return true if ID is even, false otherwise
     */
    public static boolean hasEvenId(User user) {
        return user != null && user.getId() != null && user.getId() % 2 == 0;
    }
}
