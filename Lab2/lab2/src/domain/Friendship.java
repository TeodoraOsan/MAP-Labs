package domain;

import java.time.LocalDateTime;

/**
 * Represents an undirected friendship relationship between two users in the network
 * The relationship is uniquely identified by a composite identifier {@link Tuple} containing the two user IDs, the IDs are stored in natural order (min, max).
 * It also tracks the timestamp when the relationship was established
 */
public class Friendship extends Entity<Tuple<Long, Long>>{
    private final LocalDateTime friendsSince;

    public Friendship(Long idUser1, Long idUser2) {
        Long minId = Math.min(idUser1, idUser2);
        Long maxId = Math.max(idUser1, idUser2);
        this.setId(new Tuple<>(minId, maxId));
        this.friendsSince = LocalDateTime.now();
    }

    public Friendship(Long idUser1, Long idUser2, LocalDateTime friendsSince) {
        Long minId = Math.min(idUser1, idUser2);
        Long maxId = Math.max(idUser1, idUser2);
        this.setId(new Tuple<>(minId, maxId));
        this.friendsSince = friendsSince;
    }

    /**
     * Returns the identifier of the first user in the friendship
     * @return the ID of the first user
     */
    public Long getFirstUserId() {
        return getId().left();
    }

    /**
     * Returns the identifier of the second user in the friendship
     * @return the ID of the second user
     */
    public Long getSecondUserId() {
        return getId().right();
    }

    /**
     * Returns the date and time when the friendship was established
     * @return the creation timestamp of the friendship
     */
    public LocalDateTime getFriendsFrom() {
        return friendsSince;
    }

    @Override
    public String toString() {
        return "Friendship{" +
                "user1=" + getFirstUserId() +
                ", user2=" + getSecondUserId() +
                ", friendsSince=" + friendsSince +
                '}';
    }
}
