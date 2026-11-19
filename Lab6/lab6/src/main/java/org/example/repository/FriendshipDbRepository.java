package org.example.repository;

import org.example.domain.Friendship;
import org.example.domain.Tuple;
import org.example.domain.User;
import org.example.domain.exception.RepositoryException;
import org.example.domain.validator.Validator;
import org.example.util.paging.Page;
import org.example.util.paging.Pageable;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Repository implementation for persisting {@link Friendship} entities in a PostgreSQL database using JDBC.
 */
public class FriendshipDbRepository implements Repository<Tuple<Long, Long>, Friendship> {
    private final Validator<Friendship> validator;
    private final String url;
    private final String username;
    private final String password;

    /**
     * Constructs a new FriendshipDbRepository with connection parameters and validator.
     * @param validator the entity validator
     * @param url       the database connection URL
     * @param username  the database username
     * @param password  the database password
     */
    public FriendshipDbRepository(Validator<Friendship> validator, String url, String username, String password) {
        this.validator = validator;
        this.url = url;
        this.username = username;
        this.password = password;
    }

    /**
     * Finds a friendship by its composite identifier (Tuple of two User IDs).
     * @param id the composite identifier of the friendship to be found
     * @return an {@link Optional} containing the found friendship, or {@link Optional#empty()} if no friendship was found
     * @throws IllegalArgumentException if the provided id is null
     * @throws RepositoryException      if a database access error occurs
     */
    @Override
    public Optional<Friendship> findOne(Tuple<Long, Long> id) {
        if (id == null) {
            throw new IllegalArgumentException("ID must not be null!");
        }

        String sql = "SELECT * FROM friendships WHERE id1 = ? AND id2 = ?";

        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setLong(1, id.left());
            statement.setLong(2, id.right());

            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    Long id1 = resultSet.getLong("id1");
                    Long id2 = resultSet.getLong("id2");
                    LocalDateTime friendsFrom = resultSet.getTimestamp("friends_from").toLocalDateTime();

                    Friendship friendship = new Friendship(id1, id2, friendsFrom);
                    return Optional.of(friendship);
                }
            }
        } catch (SQLException e) {
            throw new RepositoryException("Database error: " + e.getMessage(), e);
        }
        return Optional.empty();
    }

    /**
     * Retrieves all friendships from the database.
     * @return a {@link List} containing all friendships stored in the database
     * @throws RepositoryException if a database access error occurs
     */
    @Override
    public List<Friendship> findAll() {
        List<Friendship> friendships = new ArrayList<>();
        String sql = "SELECT * FROM friendships";

        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                Long id1 = resultSet.getLong("id1");
                Long id2 = resultSet.getLong("id2");
                LocalDateTime friendsFrom = resultSet.getTimestamp("friends_from").toLocalDateTime();

                Friendship friendship = new Friendship(id1, id2, friendsFrom);
                friendships.add(friendship);
            }
        } catch (SQLException e) {
            throw new RepositoryException("Database error: " + e.getMessage(), e);
        }
        return friendships;
    }

    /**
     * Saves a given friendship to the database.
     * @param entity the friendship to be saved
     * @return null if the entity was saved successfully
     * @throws IllegalArgumentException if the entity is null
     * @throws org.example.domain.exception.ValidationException if the entity is not valid
     * @throws RepositoryException      if a database access error occurs or the save operation fails
     */
    @Override
    public Friendship save(Friendship entity) {
        if (entity == null) {
            throw new IllegalArgumentException("Entity must not be null!");
        }
        validator.validate(entity);

        String sql = "INSERT INTO friendships (id1, id2, friends_from) VALUES (?, ?, ?)";

        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setLong(1, entity.getFirstUserId());
            statement.setLong(2, entity.getSecondUserId());
            statement.setTimestamp(3, Timestamp.valueOf(entity.getDate()));

            int affectedRows = statement.executeUpdate();
            if (affectedRows == 0) {
                throw new RepositoryException("Friendship save failed!");
            }
            return null;

        } catch (SQLException e) {
            throw new RepositoryException("Database error: " + e.getMessage(), e);
        }
    }

    /**
     * Deletes a friendship by its composite identifier.
     * @param id the composite identifier of the friendship to be deleted
     * @return an {@link Optional} containing the deleted friendship, or {@link Optional#empty()} if no friendship was found
     * @throws IllegalArgumentException if the provided id is null
     * @throws RepositoryException      if a database access error occurs
     */
    @Override
    public Optional<Friendship> delete(Tuple<Long, Long> id) {
        if (id == null) {
            throw new IllegalArgumentException("ID must not be null!");
        }
        Optional<Friendship> friendshipToDelete = findOne(id);

        if (friendshipToDelete.isPresent()) {
            String sql = "DELETE FROM friendships WHERE id1 = ? AND id2 = ?";
            try (Connection connection = DriverManager.getConnection(url, username, password);
                 PreparedStatement statement = connection.prepareStatement(sql)) {

                statement.setLong(1, id.left());
                statement.setLong(2, id.right());
                statement.executeUpdate();

            } catch (SQLException e) {
                throw new RepositoryException("Database error: " + e.getMessage(), e);
            }
        }
        return friendshipToDelete;
    }

    /**
     * Retrieves a page of friends for a specific user using a single SQL JOIN query.
     * @param userId   the ID of the user whose friends are retrieved
     * @param pageable pagination parameters
     * @return a Page containing the user's friends on the requested page and the total friend count
     * @throws IllegalArgumentException if userId is null, pageNumber < 0, or pageSize <= 0
     */
    public Page<User> findFriendsOnPage(Long userId, Pageable pageable) {
        if (userId == null || pageable == null || pageable.getPageNumber() < 0 || pageable.getPageSize() <= 0) {
            throw new IllegalArgumentException("Invalid arguments for paged friends query.");
        }

        List<User> friends = new ArrayList<>();
        int totalCount = 0;

        String countSql = """
            SELECT COUNT(*) AS count
            FROM friendships f
            JOIN users u ON (u.id = f.id1 OR u.id = f.id2)
            WHERE (f.id1 = ? OR f.id2 = ?) AND u.id <> ?
            """;

        String pageSql = """
            SELECT u.*
            FROM friendships f
            JOIN users u ON (u.id = f.id1 OR u.id = f.id2)
            WHERE (f.id1 = ? OR f.id2 = ?) AND u.id <> ?
            ORDER BY u.id
            LIMIT ? OFFSET ?
            """;

        try (Connection connection = DriverManager.getConnection(url, username, password)) {
            try (PreparedStatement countStmt = connection.prepareStatement(countSql)) {
                countStmt.setLong(1, userId);
                countStmt.setLong(2, userId);
                countStmt.setLong(3, userId);
                try (ResultSet rs = countStmt.executeQuery()) {
                    if (rs.next()) {
                        totalCount = rs.getInt("count");
                    }
                }
            }

            try (PreparedStatement pageStmt = connection.prepareStatement(pageSql)) {
                pageStmt.setLong(1, userId);
                pageStmt.setLong(2, userId);
                pageStmt.setLong(3, userId);
                pageStmt.setInt(4, pageable.getPageSize());
                pageStmt.setInt(5, pageable.getPageNumber() * pageable.getPageSize());

                try (ResultSet rs = pageStmt.executeQuery()) {
                    while (rs.next()) {
                        friends.add(extractUser(rs));
                    }
                }
            }
        } catch (SQLException e) {
            throw new RepositoryException("Database error while fetching paged friends", e);
        }
        return new Page<>(friends, totalCount);
    }

    private User extractUser(ResultSet rs) throws SQLException {
        Long id = rs.getLong("id");
        String firstName = rs.getString("first_name");
        String lastName = rs.getString("last_name");
        User user = new User(firstName, lastName);
        user.setId(id);
        return user;
    }
}
