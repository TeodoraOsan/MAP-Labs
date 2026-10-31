package service;

import domain.User;
import domain.validator.Validator;
import repository.Repository;

import java.util.List;

/**
 * Service managing user operations.
 */
public class UserService {
    private final Repository<Long, User> userRepository;

    /**
     * Constructs a new UserService with the specified repository.
     * @param userRepository repository used for User persistence
     * @param userValidator  validator used for user validation rules
     */
    public UserService(Repository<Long, User> userRepository, Validator<User> userValidator) {
        this.userRepository = userRepository;
    }

    /**
     * Creates and saves a new user with the given details.
     * @param firstName the first name of the user
     * @param lastName  the last name of the user
     * @return the newly created and persisted User entity
     */
    public User addUser(String firstName, String lastName) {
        User user = new User(firstName, lastName);
        userRepository.save(user);
        return user;
    }

    /**
     * Removes a user by their identifier.
     * @param userId the ID of the user to be removed
     * @return the removed User entity, or {@code null} if no user with that ID was found
     */
    public User removeUser(Long userId) {
        return userRepository.delete(userId);
    }

    /**
     * Finds a user by their ID.
     * @param id the ID of the user to locate
     * @return the found User, or {@code null} if not found
     */
    public User findUser(Long id) {
        return userRepository.findOne(id);
    }

    /**
     * Returns all registered users in the network.
     * @return an unmodifiable or new list of all users
     */
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }
}
