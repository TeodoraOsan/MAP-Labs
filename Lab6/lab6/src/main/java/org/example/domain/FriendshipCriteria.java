package org.example.domain;

import java.time.Month;
import java.util.function.Predicate;

/**
 * Utility predicates for querying Friendship entities.
 */
public final class FriendshipCriteria{
    private FriendshipCriteria() {}

    /**
     * Matches friendships established in a specific year.
     * @param year the year to match
     * @return predicate testing the creation year
     */
    public static Predicate<org.example.domain.Friendship> createdInYear(int year) {
        return friendship -> friendship != null && friendship.getDate() != null &&
                friendship.getDate().getYear() == year;
    }

    /**
     * Matches friendships established in a specific month.
     * @param month the month to match
     * @return predicate testing the creation month
     */
    public static Predicate<org.example.domain.Friendship> createdInMonth(Month month) {
        return friendship -> friendship != null && friendship.getDate() != null &&
                friendship.getDate().getMonth() == month;
    }

    /**
     * Matches friendships that involve a specific user ID.
     * @param userId the ID of the user
     * @return predicate checking if the user is part of the friendship
     */
    public static Predicate<org.example.domain.Friendship> containsUser(Long userId) {
        return friendship -> friendship != null &&
                (friendship.getFirstUserId().equals(userId) || friendship.getSecondUserId().equals(userId));
    }
}
