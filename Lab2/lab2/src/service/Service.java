package service;

import domain.Friendship;
import domain.Tuple;
import domain.User;
import domain.validator.Validator;
import repository.Repository;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Application service that coordinates business operations and domain logic for the social network
 * It is between the presentation layer (UI) and the data access layer (repositories)
 */
public class Service {
    private final Repository<Long, User> userRepository;
    private final Repository<Tuple<Long, Long>, Friendship> friendshipRepository;
    private final Validator<User> userValidator;
    private final Validator<Friendship> friendshipValidator;
    private Long nextUserId = 1L;

    public Service(Repository<Long, User> userRepository,
                   Repository<Tuple<Long, Long>, Friendship> friendshipRepository,
                   Validator<User> userValidator,
                   Validator<Friendship> friendshipValidator) {
        this.userRepository = userRepository;
        this.friendshipRepository = friendshipRepository;
        this.userValidator = userValidator;
        this.friendshipValidator = friendshipValidator;
        syncNextUserId();
    }

    private void syncNextUserId() {
        for (User u : userRepository.findAll()) {
            if (u.getId() != null && u.getId() >= nextUserId) {
                nextUserId = u.getId() + 1;
            }
        }
    }

    /**
     * Adds a new user to the network
     * @param firstName the first name of the user
     * @param lastName  the last name of the user
     * @return the saved user entity
     */
    public User addUser(String firstName, String lastName) {
        User user = new User(firstName, lastName);
        user.setId(nextUserId++);
        userValidator.validate(user);
        userRepository.save(user);
        return user;
    }

    /**
     * Removes a user from the network
     * @param userId the identifier of the user to be removed
     * @return the removed user, or null if the user does not exist
     */
    public User removeUser(Long userId) {
        User removedUser = userRepository.delete(userId);
        if (removedUser != null) {
            List<Tuple<Long, Long>> friendshipsToDelete = new ArrayList<>();
            for (Friendship f : friendshipRepository.findAll()) {
                if (f.getFirstUserId().equals(userId) || f.getSecondUserId().equals(userId)) {
                    friendshipsToDelete.add(f.getId());
                }
            }
            for (Tuple<Long, Long> tupleId : friendshipsToDelete) {
                friendshipRepository.delete(tupleId);
            }
        }
        return removedUser;
    }

    /**
     * Adds a friendship relationship between two users
     * @param id1 the ID of the first user
     * @param id2 the ID of the second user
     * @return the created friendship relationship
     */
    public Friendship addFriendship(Long id1, Long id2) {
        if (userRepository.findOne(id1) == null || userRepository.findOne(id2) == null) {
            throw new IllegalArgumentException("One or both users do not exist!");
        }
        Friendship friendship = new Friendship(id1, id2);
        friendshipValidator.validate(friendship);
        if (friendshipRepository.findOne(friendship.getId()) != null) {
            throw new IllegalArgumentException("The friendship already exists!");
        }
        friendshipRepository.save(friendship);
        return friendship;
    }

    /**
     * Removes a friendship relationship between two users
     * @param id1 the ID of the first user
     * @param id2 the ID of the second user
     * @return the removed friendship entity
     */
    public Friendship removeFriendship(Long id1, Long id2) {
        Long minId = Math.min(id1, id2);
        Long maxId = Math.max(id1, id2);
        Tuple<Long, Long> key = new Tuple<>(minId, maxId);
        Friendship deleted = friendshipRepository.delete(key);
        if (deleted == null) {
            throw new IllegalArgumentException("The friendship does not exist!");
        }
        return deleted;
    }

    /**
     * Returns all users registered in the application
     * @return the complete list of users
     */
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    /**
     * Returns all friendship relationships from the network
     * @return the complete list of friendships
     */
    public List<Friendship> getAllFriendships() {
        return friendshipRepository.findAll();
    }

    /**
     * Calculates the total number of communities (connected components in the network graph)
     * @return the number of connected components
     */
    public int getNumberCommunities() {
        Set<Long> visited = new HashSet<>();
        int k = 0;
        for (User user : userRepository.findAll()) {
            if(!visited.contains(user.getId())) {
                k++;
                DFS(user.getId(), visited);
            }
        }
        return k;
    }

    /**
     * Determines the most sociable community (the connected component with the longest path)
     * @return the list of users belonging to the connected component
     */
    public List<User> getMostSociableCommunity() {
        Set<Long> visited = new HashSet<>();
        List<List<Long>> communities = new ArrayList<>();
        for(User user : userRepository.findAll()) {
            if(!visited.contains(user.getId())) {
                List<Long> currentCom = new ArrayList<>();
                collectCommunity(user.getId(), visited, currentCom);
                communities.add(currentCom);
            }
        }
        List<Long> bestCommunity = new ArrayList<>();
        int maxLenght = -1;
        for(List<Long> community : communities) {
            int longestInComunity = 0;
            for(Long memberId : community) {
                Set<Long> localVisited = new HashSet<>();
                int path = longestPath(memberId, localVisited);
                if (path > longestInComunity) {
                    longestInComunity = path;
                }
            }
            if(longestInComunity > maxLenght) {
                maxLenght = longestInComunity;
                bestCommunity = community;
            }
        }
        List<User> result = new ArrayList<>();
        for(Long id : bestCommunity) {
            User u = userRepository.findOne(id);
            if (u != null) {
                result.add(u);
            }
        }
        return result;
    }

    private List<Long> getFriendsOf(Long userId) {
        List<Long> friends = new ArrayList<>();
        for (Friendship f : friendshipRepository.findAll()) {
            if (f.getFirstUserId().equals(userId)) {
                friends.add(f.getSecondUserId());
            } else if (f.getSecondUserId().equals(userId)) {
                friends.add(f.getFirstUserId());
            }
        }
        return friends;
    }

    private void DFS(Long currentId, Set<Long> visited) {
        visited.add(currentId);
        for (Long friendId : getFriendsOf(currentId)) {
            if (!visited.contains(friendId)) {
                DFS(friendId, visited);
            }
        }
    }

    private void collectCommunity(Long currentId, Set<Long> visited, List<Long> community) {
        visited.add(currentId);
        community.add(currentId);
        for (Long friendId : getFriendsOf(currentId)) {
            if (!visited.contains(friendId)) {
                collectCommunity(friendId, visited, community);
            }
        }
    }

    private int longestPath(Long currentId, Set<Long> visited) {
        visited.add(currentId);
        int maxDepth = 0;
        for (Long friendId : getFriendsOf(currentId)) {
            if (!visited.contains(friendId)) {
                int depth = 1 + longestPath(friendId, visited);
                if (depth > maxDepth) {
                    maxDepth = depth;
                }
            }
        }
        visited.remove(currentId);
        return maxDepth;
    }
}