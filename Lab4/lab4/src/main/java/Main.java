import domain.Friendship;
import domain.Tuple;
import domain.User;
import domain.exception.ValidationException;
import domain.validator.FriendshipValidator;
import domain.validator.UserValidator;
import domain.validator.Validator;
import repository.Repository;
import repository.RepositoryFactory;
import service.CommunityService;
import service.FriendshipService;
import service.UserService;
import ui.ConsoleUI;

/**
 * Application entry point.
 * Initializes configuration, repositories, domain services, and user interface.
 */
public class Main {
    public static void main(String[] args) {
        // Compunere validator lambda + andThen fara clasa noua
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