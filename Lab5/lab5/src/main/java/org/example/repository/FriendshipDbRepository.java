package org.example.repository;

import org.example.domain.Friendship;
import org.example.domain.Tuple;
import org.example.domain.exception.RepositoryException;
import org.example.domain.validator.Validator;

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
}
