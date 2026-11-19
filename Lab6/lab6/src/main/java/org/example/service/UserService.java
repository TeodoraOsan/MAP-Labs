package org.example.service;

import org.example.domain.User;
import org.example.domain.UserCriteria;
import org.example.util.paging.Page;
import org.example.util.paging.Pageable;
import org.example.repository.PagingRepository;
import org.example.repository.Repository;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Predicate;

/**
 * Service managing user operations.
 */
public class UserService {
    private final Repository<Long, User> userRepository;
    private FriendshipService friendshipService;

    /**
     * Constructs a new UserService with the specified repository.
     * @param userRepository repository used for User persistence
     */
    public UserService(Repository<Long, User> userRepository, FriendshipService friendshipService) {
        this.userRepository = Objects.requireNonNull(userRepository, "userRepository must not be null");
        this.friendshipService = Objects.requireNonNull(friendshipService, "friendshipService must not be null");
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
    public Optional<User> removeUser(Long userId) {

        Optional<User> deleted = userRepository.delete(userId);
        deleted.ifPresent(u -> friendshipService.removeAllFriendshipsOfUser(userId));
        return deleted;
    }

    /**
     * Finds a user by their ID.
     * @param id the ID of the user to locate
     * @return the found User, or {@code null} if not found
     */
    public Optional<User> findUser(Long id) {
        return userRepository.findOne(id);
    }

    /**
     * Returns all registered users in the network.
     * @return an unmodifiable or new list of all users
     */
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    /**
     * Filters registered users according to a specified predicate.
     * @param filter the functional condition evaluated against each user, must not be null
     * @return a {@link List} of users satisfying the given filter criteria
     */
    public List<User> filterUsers(Predicate<User> filter) {
        return userRepository.findAll(filter);
    }

    /**
     * Finds the unique ID of a user by first name and last name using UserCriteria.
     * @param firstName the first name of the user
     * @param lastName  the last name of the user
     * @return an Optional containing the ID, or Optional.empty() if no user matches
     */
    public Optional<Long> findIdByName(String firstName, String lastName) {
        return userRepository.findAll(UserCriteria.hasFullName(firstName, lastName))
                .stream()
                .findFirst()
                .map(User::getId);
    }

    /**
     * Retrieves a page of users.
     * @param pageable pagination parameters (page number and page size)
     * @return a Page containing the users on the requested page and the total user count
     * @throws IllegalArgumentException      if pageable is null, pageNumber < 0, or pageSize <= 0
     * @throws UnsupportedOperationException if the repository does not support pagination
     */
    public Page<User> getUsersOnPage(Pageable pageable) {
        if (pageable == null || pageable.getPageNumber() < 0 || pageable.getPageSize() <= 0) {
            throw new IllegalArgumentException("Invalid pagination parameters: pageNumber must be >= 0 and pageSize > 0");
        }
        if (userRepository instanceof PagingRepository<Long, User> pagingRepo) {
            return pagingRepo.findAllOnPage(pageable);
        }
        throw new UnsupportedOperationException("Pagination is only supported for DATABASE repositories.");
    }
}
