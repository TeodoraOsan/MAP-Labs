package org.example.service;

import org.example.domain.Friendship;
import org.example.domain.Tuple;
import org.example.domain.User;
import org.example.repository.Repository;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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
        this.userRepository = Objects.requireNonNull(userRepository);
        this.friendshipRepository = Objects.requireNonNull(friendshipRepository);
    }

    /**
     * Calculates the total number of communities (connected components) in the network.
     * @return the number of connected components
     */
    public int getNumberCommunities() {
        return getCommunities().size();
    }

    /**
     * Determines the most sociable community, defined as the component containing the longest path.
     * @return the list of users belonging to the most sociable community
     */
    public List<User> getMostSociableCommunity() {
        Comparator<List<Long>> communityComparator = Comparator
                .comparingInt(this::calculateMaxPathInCommunity)
                .thenComparing(List::size); // criteriu secundar: numarul de membri
        return getCommunities().stream()
                .max(communityComparator)
                .map(community -> community.stream()
                        .map(userRepository::findOne)
                        .filter(Optional::isPresent)
                        .map(Optional::get)
                        .collect(Collectors.toList()))
                .orElseGet(Collections::emptyList);
    }

    private List<List<Long>> getCommunities() {
        Set<Long> visited = new HashSet<>();
        return userRepository.findAll().stream()
                .map(User::getId)
                .filter(visited::add)
                .map(id -> {
                    List<Long> community = new ArrayList<>();
                    collectCommunity(id, visited, community);
                    return community;
                })
                .collect(Collectors.toList());
    }

    private int calculateMaxPathInCommunity(List<Long> community) {
        return community.stream()
                .mapToInt(memberId -> longestPath(memberId, new HashSet<>()))
                .max()
                .orElse(0);
    }

    private List<Long> getFriendsOf(Long userId) {
        return friendshipRepository.findAll().stream()
                .flatMap(f -> {
                    if (f.getFirstUserId().equals(userId)) {
                        return Stream.of(f.getSecondUserId());
                    } else if (f.getSecondUserId().equals(userId)) {
                        return Stream.of(f.getFirstUserId());
                    }
                    return Stream.empty();
                })
                .collect(Collectors.toList());
    }

    private void collectCommunity(Long currentId, Set<Long> visited, List<Long> community) {
        community.add(currentId);
        getFriendsOf(currentId).stream()
                .filter(visited::add)
                .forEach(friendId -> collectCommunity(friendId, visited, community));
    }

    private int longestPath(Long currentId, Set<Long> visited) {
        visited.add(currentId);
        int maxDepth = getFriendsOf(currentId).stream()
                .filter(friendId -> !visited.contains(friendId))
                .mapToInt(friendId -> 1 + longestPath(friendId, visited))
                .max()
                .orElse(0);
        visited.remove(currentId);
        return maxDepth;
    }
}
