package org.example.ui;

import org.example.domain.Friendship;
import org.example.domain.User;
import org.example.domain.exception.DuplicateEntityException;
import org.example.domain.exception.EntityNotFoundException;
import org.example.domain.exception.RepositoryException;
import org.example.domain.exception.ValidationException;
import org.example.service.CommunityService;
import org.example.service.FriendshipService;
import org.example.service.UserService;
import org.example.util.paging.Page;
import org.example.util.paging.Pageable;

import java.util.List;
import java.util.Scanner;
import java.util.function.Function;

/**
 *Console interface providing interactive user management and network operations
 * Acting as the presentation layer, this class handles command-line input and output formatting, captures user actions and delegates business operations to the Service
 */
public class ConsoleUI {
    private final UserService userService;
    private final FriendshipService friendshipService;
    private final CommunityService communityService;
    private final Scanner scanner;
    private final int pageSize;

    public ConsoleUI(UserService userService,
                     FriendshipService friendshipService,
                     CommunityService communityService,
                     int pageSize) {
        this.userService = userService;
        this.friendshipService = friendshipService;
        this.communityService = communityService;
        this.scanner = new Scanner(System.in);
        this.pageSize = pageSize;
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
        System.out.println("9. Show users (paged)");
        System.out.println("10. Show friends (paged)");
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
                case "1":
                    uiAddUser();
                    break;
                case "2":
                    uiRemoveUser();
                    break;
                case "3":
                    uiAddFriendship();
                    break;
                case "4":
                    uiRemoveFriendship();
                    break;
                case "5":
                    showAllUsers();
                    break;
                case "6":
                    showAllFriendships();
                    break;
                case "7":
                    uiShowNumberOfCommunities();
                    break;
                case "8":
                    uiShowMostSociableCommunity();
                    break;
                case "9":
                    showUsersPaged();
                    break;
                case "10":
                    showFriendsPaged();
                    break;
                case "0":
                    return;
                default:
                    System.out.println("Invalid option!");
                    break;
            }
            System.out.println();
        }
    }

    /**
     * Shows all users.
     */
    private void showAllUsers() {
        List<User> users = userService.getAllUsers();
        if (users.isEmpty()) {
            System.out.println("No users");
            return;
        }
        users.stream()
                .map(u -> "ID: " + u.getId() + " | Name: " + u.getFirstName() + " " + u.getLastName())
                .forEach(System.out::println);
    }

    /**
     * Shows all friendships.
     */
    private void showAllFriendships() {
        List<Friendship> friendships = friendshipService.getAllFriendships();
        if (friendships.isEmpty()) {
            System.out.println("No friendships");
            return;
        }
        friendships.stream()
                .map(f -> "Friendship between User " + f.getFirstUserId() +
                        " and User " + f.getSecondUserId() +
                        " (since: " + f.getDate() + ")")
                .forEach(System.out::println);
    }

    private void uiAddUser() {
        try {
            System.out.print("First name: ");
            String firstName = scanner.nextLine();
            System.out.print("Last name: ");
            String lastName = scanner.nextLine();
            User user = userService.addUser(firstName, lastName);
            System.out.println("User added successfully with ID: " + user.getId());
        } catch (ValidationException e) {
            System.out.println("Validation error: " + e.getMessage());
        } catch (RepositoryException e) {
            System.out.println("Persistence error: " + e.getMessage());
        } catch (EntityNotFoundException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private void uiRemoveUser() {
        try {
            System.out.print("User ID to be removed: ");
            Long id = Long.parseLong(scanner.nextLine().trim());
            userService.removeUser(id).ifPresentOrElse(
                    deleted -> System.out.println("User " + deleted.getFirstName() + " " + deleted.getLastName() + " was deleted successfully."),
                    () -> System.out.println("User with ID " + id + " does not exist.")
            );
        } catch (NumberFormatException e) {
            System.out.println("Input error: The ID must be a valid number!");
        } catch (EntityNotFoundException e) {
            System.out.println("Error: " + e.getMessage());
        } catch (RepositoryException e) {
            System.out.println("Persistence error: " + e.getMessage());
        }
    }

    private void uiAddFriendship() {
        try {
            System.out.print("First user ID: ");
            Long id1 = Long.parseLong(scanner.nextLine().trim());
            System.out.print("Second user ID: ");
            Long id2 = Long.parseLong(scanner.nextLine().trim());
            friendshipService.addFriendship(id1, id2);
            System.out.println("The friendship was added successfully!");
        } catch (NumberFormatException e) {
            System.out.println("Input error: The IDs must be valid numbers!");
        } catch (EntityNotFoundException e) {
            System.out.println("Not found error: " + e.getMessage());
        } catch (DuplicateEntityException e) {
            System.out.println("Duplicate error: " + e.getMessage());
        } catch (ValidationException e) {
            System.out.println("Validation error: " + e.getMessage());
        } catch (RepositoryException e) {
            System.out.println("Persistence error: " + e.getMessage());
        }
    }

    private void uiRemoveFriendship() {
        try {
            System.out.print("First user ID: ");
            Long id1 = Long.parseLong(scanner.nextLine().trim());
            System.out.print("Second user ID: ");
            Long id2 = Long.parseLong(scanner.nextLine().trim());
            friendshipService.removeFriendship(id1, id2);
            System.out.println("The friendship was deleted successfully!");
        } catch (NumberFormatException e) {
            System.out.println("Input error: The IDs must be valid numbers!");
        } catch (EntityNotFoundException e) {
            System.out.println("Not found error: " + e.getMessage());
        } catch (RepositoryException e) {
            System.out.println("Persistence error: " + e.getMessage());
        }
    }

    private void uiShowNumberOfCommunities() {
        int count = communityService.getNumberCommunities();
        System.out.println("Number of communities: " + count);
    }

    private void uiShowMostSociableCommunity() {
        List<User> community = communityService.getMostSociableCommunity();
        if (community.isEmpty()) {
            System.out.println("There are no communities in the network.");
            return;
        }
        System.out.println("=== Most sociable community ===");
        community.stream()
                .map(u -> "ID: " + u.getId() + " - " + u.getFirstName() + " " + u.getLastName())
                .forEach(System.out::println);
    }

    private void navigatePages(String title, Function<Pageable, Page<User>> pageFetcher) {
        int currentPage = 0;

        while (true) {
            Page<User> page = pageFetcher.apply(new Pageable(currentPage, pageSize));
            int totalElements = page.getTotalNumberOfElements();
            int totalPages = (int) Math.ceil((double) totalElements / pageSize);

            System.out.println("\n=== " + title + " ===");
            if (totalElements == 0) {
                System.out.println("No records found. (Page 0 of 0)");
                return;
            }

            System.out.println("Page " + (currentPage + 1) + " of " + totalPages + " (Total: " + totalElements + ")");
            page.getElementsOnPage().forEach(System.out::println);

            System.out.print("[p] Previous | [n] Next | [q] Quit: ");
            String cmd = scanner.nextLine().trim().toLowerCase();

            if (cmd.equals("n")) {
                if (currentPage + 1 < totalPages) {
                    currentPage++;
                } else {
                    System.out.println("You are already on the last page.");
                }
            } else if (cmd.equals("p")) {
                if (currentPage > 0) {
                    currentPage--;
                } else {
                    System.out.println("You are already on the first page.");
                }
            } else if (cmd.equals("q")) {
                break;
            }
        }
    }

    private void showUsersPaged() {
        try {
            navigatePages("Users List", userService::getUsersOnPage);
        } catch (UnsupportedOperationException | IllegalArgumentException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private void showFriendsPaged() {
        try {
            System.out.print("Enter user ID: ");
            Long userId = Long.parseLong(scanner.nextLine().trim());
            navigatePages("Friends of user " + userId,
                    pageable -> friendshipService.getFriendsOnPage(userId, pageable));
        } catch (NumberFormatException e) {
            System.out.println("Invalid ID!");
        } catch (UnsupportedOperationException | IllegalArgumentException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }
}
