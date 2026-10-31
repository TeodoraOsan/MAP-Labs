import domain.*;
import domain.validator.FriendshipValidator;
import domain.validator.UserValidator;
import repository.InMemoryRepository;
import repository.Repository;
import service.Service;
import ui.ConsoleUI;

public class Main {
    public static void main (String[] args) {
        Repository<Long, User> userRepository = new InMemoryRepository<>();
        Repository<Tuple<Long, Long>, Friendship> friendshipRepository = new InMemoryRepository<>();

        UserValidator userValidator = new UserValidator();
        FriendshipValidator friendshipValidator = new FriendshipValidator();

        Service service = new Service(userRepository, friendshipRepository, userValidator, friendshipValidator);

        loadMockData(service);


        ConsoleUI consoleUI = new ConsoleUI(service);
        consoleUI.run();
    }

    private static void loadMockData(Service service) {
        User u1 = service.addUser("Ion", "Popescu");
        User u2 = service.addUser("Maria", "Ionescu");
        User u3 = service.addUser("George", "Radu");
        User u4 = service.addUser("Elena", "Dumitrescu");
        User u5 = service.addUser("Andrei", "Stan");

        // comunitate formata din 1 - 2 - 3
        service.addFriendship(u1.getId(), u2.getId());
        service.addFriendship(u2.getId(), u3.getId());

        // alta comunitate separata: 4 - 5
        service.addFriendship(u4.getId(), u5.getId());
    }
}
