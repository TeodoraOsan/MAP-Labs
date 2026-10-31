package ui;

import domain.Friendship;
import domain.User;
import domain.validator.ValidationException;
import service.Service;

import java.util.List;
import java.util.Scanner;

/**
 *Console interface providing interactive user management and network operations
 * Acting as the presentation layer, this class handles command-line input and output formatting, captures user actions and delegates business operations to the {@link service.Service}
 */
public class ConsoleUI {
    private final Service service;
    private final Scanner scanner;

    public ConsoleUI(Service service) {
        this.service = service;
        this.scanner = new Scanner(System.in);
    }

    private void printMenu() {
        System.out.println("--- MENIU SOCIAL NETWORK ---");
        System.out.println("1. Add user");
        System.out.println("2. Remove user");
        System.out.println("3. Add friendship");
        System.out.println("4. Remove friendship");
        System.out.println("5. Show users");
        System.out.println("6. Show friendships");
        System.out.println("7. Number of communities");
        System.out.println("8. Most sociable community");
        System.out.println("0. Exit");
    }

    /**
     * Starts the interactive command-line interface loop
     * Continuously displays the menu options, reads user commands from standard input and delegates execution to the corresponding methods until the exit command is selected
     */
    public void run() {
        while(true) {
            printMenu();
            System.out.println("Choose option: ");
            String cmd = scanner.nextLine().trim();
            switch (cmd) {
                case "1" -> uiAddUser();
                case "2" -> uiRemoveUser();
                case "3" -> uiAddFriendship();
                case "4" -> uiRemoveFriendship();
                case "5" -> showAllUsers();
                case "6" -> showAllFriendships();
                case "7" -> uiShowNumberOfCommunities();
                case "8" -> uiShowMostSociableCommunity();
                case "0" -> {
                    return;
                }
                default -> System.out.println("Invalid option!");
            }
            System.out.println();
        }
    }

    /**
     * Shows all users
     */
    private void showAllUsers() {
        List<User> users = service.getAllUsers();
        if(users.isEmpty()) {
            System.out.println("No users");
        }
        for(User u : users) {
            System.out.println("ID: " + u.getId() + " | Name: " + u.getFirstName() + " " + u.getLastName());
        }
    }

    /**
     * Shows all friendships
     */
    private void showAllFriendships() {
        List<Friendship> friendships = service.getAllFriendships();
        if (friendships.isEmpty()) {
            System.out.println("No friendships");
            return;
        }
        for (Friendship f : friendships) {
            System.out.println("Friendship between User " + f.getFirstUserId() +
                    " and User " + f.getSecondUserId() +
                    " (since: " + f.getFriendsFrom() + ")");
        }
    }

    private void uiAddUser() {
        try{
            System.out.println("First name: ");
            String firstName = scanner.nextLine();
            System.out.println("Last name: ");
            String lastName = scanner.nextLine();
            User user = service.addUser(firstName, lastName);
            System.out.println("User added successfully with ID: " + user.getId());
        }
        catch (ValidationException | IllegalArgumentException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private void uiRemoveUser() {
        try{
            System.out.println("User ID to be removed: ");
            Long id = Long.parseLong(scanner.nextLine().trim());
            User deleted = service.removeUser(id);
            if (deleted != null) {
                System.out.println("User " + deleted.getFirstName() + " " + deleted.getLastName() + " was deleted successfully.");
            } else {
                System.out.println("User with ID " + id + " does not exist.");
            }
        }
        catch (NumberFormatException e) {
            System.out.println("Error: The ID must be a valid number!");
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private void uiAddFriendship() {
        try {
            System.out.print("First user ID: ");
            Long id1 = Long.parseLong(scanner.nextLine().trim());
            System.out.print("Second user ID: ");
            Long id2 = Long.parseLong(scanner.nextLine().trim());
            service.addFriendship(id1, id2);
            System.out.println("The friendship was added successfully!");
        } catch (NumberFormatException e) {
            System.out.println("Error: The IDs must be valid numbers!");
        } catch (ValidationException | IllegalArgumentException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private void uiRemoveFriendship() {
        try {
            System.out.print("First user ID: ");
            Long id1 = Long.parseLong(scanner.nextLine().trim());
            System.out.print("Second user ID: ");
            Long id2 = Long.parseLong(scanner.nextLine().trim());
            service.removeFriendship(id1, id2);
            System.out.println("The friendship was deleted successfully!");
        } catch (NumberFormatException e) {
            System.out.println("Error: The IDs must be valid numbers!");
        } catch (IllegalArgumentException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private void uiShowNumberOfCommunities() {
        int count = service.getNumberCommunities();
        System.out.println("Number of communities: " + count);
    }

    private void uiShowMostSociableCommunity() {
        List<User> community = service.getMostSociableCommunity();
        if (community.isEmpty()) {
            System.out.println("There are no communities in the network.");
            return;
        }
        System.out.println("=== Most sociable community ===");
        for (User u : community) {
            System.out.println("ID: " + u.getId() + " - " + u.getFirstName() + " " + u.getLastName());
        }
    }
}
