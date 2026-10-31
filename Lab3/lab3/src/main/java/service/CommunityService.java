package service;

import domain.Friendship;
import domain.Tuple;
import domain.User;
import repository.Repository;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Service calculating graph and network metrics.
 */
public class CommunityService {
    private final Repository<Long, User> userRepository;
    private final Repository<Tuple<Long, Long>, Friendship> friendshipRepository;

    /**
     * Constructs a CommunityService using the user and friendship repositories.
     * @param userRepository repository containing user data
     * @param friendshipRepository repository containing friendship connections
     */
    public CommunityService(Repository<Long, User> userRepository,
                            Repository<Tuple<Long, Long>, Friendship> friendshipRepository) {
        this.userRepository = userRepository;
        this.friendshipRepository = friendshipRepository;
    }

    /**
     * Calculates the total number of communities (connected components) in the network.
     * @return the number of connected components
     */
    public int getNumberCommunities() {
        Set<Long> visited = new HashSet<>();
        int count = 0;
        for (User user : userRepository.findAll()) {
            if (!visited.contains(user.getId())) {
                count++;
                dfs(user.getId(), visited);
            }
        }
        return count;
    }

    /**
     * Determines the most sociable community, defined as the component containing the longest path.
     * @return the list of users belonging to the most sociable community
     */
    public List<User> getMostSociableCommunity() {
        Set<Long> visited = new HashSet<>();
        List<List<Long>> communities = new ArrayList<>();
        for (User user : userRepository.findAll()) {
            if (!visited.contains(user.getId())) {
                List<Long> currentCommunity = new ArrayList<>();
                collectCommunity(user.getId(), visited, currentCommunity);
                communities.add(currentCommunity);
            }
        }

        List<Long> bestCommunity = new ArrayList<>();
        int maxLength = -1;
        for (List<Long> community : communities) {
            int longestInCommunity = 0;
            for (Long memberId : community) {
                Set<Long> localVisited = new HashSet<>();
                int path = longestPath(memberId, localVisited);
                if (path > longestInCommunity) {
                    longestInCommunity = path;
                }
            }
            if (longestInCommunity > maxLength) {
                maxLength = longestInCommunity;
                bestCommunity = community;
            }
        }

        List<User> result = new ArrayList<>();
        for (Long id : bestCommunity) {
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

    private void dfs(Long currentId, Set<Long> visited) {
        visited.add(currentId);
        for (Long friendId : getFriendsOf(currentId)) {
            if (!visited.contains(friendId)) {
                dfs(friendId, visited);
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
