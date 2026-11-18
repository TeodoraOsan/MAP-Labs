package org.example;

import org.example.domain.Friendship;
import org.example.domain.Tuple;
import org.example.domain.User;
import org.example.domain.exception.ValidationException;
import org.example.domain.validator.FriendshipValidator;
import org.example.domain.validator.UserValidator;
import org.example.domain.validator.Validator;
import org.example.repository.Repository;
import org.example.repository.RepositoryFactory;
import org.example.service.CommunityService;
import org.example.service.FriendshipService;
import org.example.service.UserService;
import org.example.ui.ConsoleUI;

/**
 * Application entry point.
 * Initializes configuration, repositories, domain services, and user interface.
 */
public class Main {
    public static void main(String[] args) {
        Validator<User> baseUserValidator = new UserValidator();
        Validator<User> composedUserValidator = baseUserValidator.andThen(user -> {
            if (user.getFirstName().length() < 2) {
                throw new ValidationException("First name must contain at least 2 characters!");
            }
        });

        FriendshipValidator friendshipValidator = new FriendshipValidator();

        RepositoryFactory factory = new RepositoryFactory();

        Repository<Long, User> userRepository = factory.createUserRepository(composedUserValidator);
        Repository<Tuple<Long, Long>, Friendship> friendshipRepository = factory.createFriendshipRepository(friendshipValidator);

        UserService userService = new UserService(userRepository);
        FriendshipService friendshipService = new FriendshipService(friendshipRepository, userRepository);
        CommunityService communityService = new CommunityService(userRepository, friendshipRepository);

        ConsoleUI consoleUI = new ConsoleUI(userService, friendshipService, communityService);
        consoleUI.run();
    }
}