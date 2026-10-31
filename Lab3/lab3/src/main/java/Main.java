import domain.Friendship;
import domain.Tuple;
import domain.User;
import domain.validator.FriendshipValidator;
import domain.validator.UserValidator;
import repository.InMemoryRepository;
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
        UserValidator userValidator = new UserValidator();
        FriendshipValidator friendshipValidator = new FriendshipValidator();

        RepositoryFactory factory = new RepositoryFactory();

        Repository<Long, User> userRepository = factory.createUserRepository(userValidator);
        Repository<Tuple<Long, Long>, Friendship> friendshipRepository = factory.createFriendshipRepository(friendshipValidator);

        UserService userService = new UserService(userRepository, userValidator);
        FriendshipService friendshipService = new FriendshipService(friendshipRepository, userRepository, friendshipValidator);
        CommunityService communityService = new CommunityService(userRepository, friendshipRepository);

        ConsoleUI consoleUI = new ConsoleUI(userService, friendshipService, communityService);
        consoleUI.run();
    }
}