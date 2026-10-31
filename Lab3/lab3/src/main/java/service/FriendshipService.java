package service;

import domain.Friendship;
import domain.Tuple;
import domain.User;
import domain.exception.DuplicateEntityException;
import domain.exception.EntityNotFoundException;
import domain.validator.Validator;
import repository.Repository;

import java.util.ArrayList;
import java.util.List;

/**
 * Service handling friendships.
 */
public class FriendshipService {
    private final Repository<Tuple<Long, Long>, Friendship> friendshipRepository;
    private final Repository<Long, User> userRepository;
    private final Validator<Friendship> friendshipValidator;

    /**
     * Constructs a FriendshipService with the necessary repositories and validator.
     * @param friendshipRepository repository for friendships
     * @param userRepository repository for users
     * @param friendshipValidator validator for friendship business rules
     */
    public FriendshipService(Repository<Tuple<Long, Long>, Friendship> friendshipRepository,
                             Repository<Long, User> userRepository,
                             Validator<Friendship> friendshipValidator) {
        this.friendshipRepository = friendshipRepository;
        this.userRepository = userRepository;
        this.friendshipValidator = friendshipValidator;
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
        if (userRepository.findOne(id1) == null || userRepository.findOne(id2) == null) {
            throw new EntityNotFoundException("One or both users do not exist!");
        }
        Friendship friendship = new Friendship(id1, id2);
        friendshipValidator.validate(friendship);
        if (friendshipRepository.findOne(friendship.getId()) != null) {
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
        Friendship deleted = friendshipRepository.delete(key);
        if (deleted == null) {
            throw new EntityNotFoundException("The friendship does not exist!");
        }
        return deleted;
    }

    /**
     * Removes all friendships associated with a given user ID to preserve consistency on deletion.
     * @param userId ID of the user whose friendships must be purged
     */
    public void removeAllFriendshipsOfUser(Long userId) {
        List<Tuple<Long, Long>> toDelete = new ArrayList<>();
        for (Friendship f : friendshipRepository.findAll()) {
            if (f.getFirstUserId().equals(userId) || f.getSecondUserId().equals(userId)) {
                toDelete.add(f.getId());
            }
        }
        for (Tuple<Long, Long> key : toDelete) {
            friendshipRepository.delete(key);
        }
    }

    /**
     * Returns all friendship relations registered in the network.
     * @return a list of all friendships
     */
    public List<Friendship> getAllFriendships() {
        return friendshipRepository.findAll();
    }
}
