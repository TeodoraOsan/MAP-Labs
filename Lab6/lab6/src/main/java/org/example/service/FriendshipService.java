package org.example.service;

import org.example.domain.Friendship;
import org.example.domain.Tuple;
import org.example.domain.User;
import org.example.domain.exception.DuplicateEntityException;
import org.example.domain.exception.EntityNotFoundException;
import org.example.util.paging.Page;
import org.example.util.paging.Pageable;
import org.example.repository.FriendshipDbRepository;
import org.example.repository.Repository;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Service handling friendships.
 */
public class FriendshipService {
    private final Repository<Tuple<Long, Long>, Friendship> friendshipRepository;
    private final Repository<Long, User> userRepository;

    /**
     * Constructs a FriendshipService with the necessary repositories and validator.
     * @param friendshipRepository repository for friendships
     * @param userRepository repository for users
     */
    public FriendshipService(Repository<Tuple<Long, Long>, Friendship> friendshipRepository,
                             Repository<Long, User> userRepository) {
        this.friendshipRepository = friendshipRepository;
        this.userRepository = userRepository;
    }

    /**
     * Creates and saves a friendship between two existing users.
     * @param id1 ID of the first user
     * @param id2 ID of the second user
     * @return the created Friendship instance
     * @throws EntityNotFoundException if one or both users do not exist
     * @throws DuplicateEntityException if the friendship already exists
     */
    public Friendship addFriendship(Long id1, Long id2) {
        if (userRepository.findOne(id1).isEmpty() || userRepository.findOne(id2).isEmpty()) {
            throw new EntityNotFoundException("One or both users do not exist!");
        }
        Friendship friendship = new Friendship(id1, id2);
        if (friendshipRepository.findOne(friendship.getId()).isPresent()) {
            throw new DuplicateEntityException("The friendship already exists!");
        }
        friendshipRepository.save(friendship);
        return friendship;
    }

    /**
     * Removes an existing friendship between two users.
     * @param id1 ID of the first user
     * @param id2 ID of the second user
     * @return the deleted Friendship instance
     * @throws EntityNotFoundException if the friendship does not exist
     */
    public Friendship removeFriendship(Long id1, Long id2) {
        Long minId = Math.min(id1, id2);
        Long maxId = Math.max(id1, id2);
        Tuple<Long, Long> key = new Tuple<>(minId, maxId);
        return friendshipRepository.delete(key)
                .orElseThrow(() -> new EntityNotFoundException("The friendship does not exist!"));
    }

    /**
     * Removes all friendships associated with a given user ID to preserve consistency on deletion.
     * @param userId ID of the user whose friendships must be purged
     */
    public void removeAllFriendshipsOfUser(Long userId) {
        List<Tuple<Long, Long>> toDelete = friendshipRepository.findAll().stream()
                .filter(f -> f.getFirstUserId().equals(userId) || f.getSecondUserId().equals(userId))
                .map(Friendship::getId)
                .collect(Collectors.toList());

        toDelete.forEach(friendshipRepository::delete);
    }

    /**
     * Returns all friendship relations registered in the network.
     * @return a list of all friendships
     */
    public List<Friendship> getAllFriendships() {
        return friendshipRepository.findAll();
    }

    /**
     * Retrieves a page of friends for a given user.
     * @param userId   the ID of the user whose friends are requested
     * @param pageable pagination parameters (page number and page size)
     * @return a Page containing the user's friends on the requested page and the total friend count
     * @throws IllegalArgumentException      if userId is null, pageable is null, pageNumber < 0, or pageSize <= 0
     * @throws UnsupportedOperationException if the repository does not support pagination
     */
    public Page<User> getFriendsOnPage(Long userId, Pageable pageable) {
        if (userId == null || pageable == null || pageable.getPageNumber() < 0 || pageable.getPageSize() <= 0) {
            throw new IllegalArgumentException("Invalid pagination parameters.");
        }
        if (friendshipRepository instanceof FriendshipDbRepository dbRepo) {
            return dbRepo.findFriendsOnPage(userId, pageable);
        }
        throw new UnsupportedOperationException("Pagination is only supported for DATABASE repositories.");
    }
}
