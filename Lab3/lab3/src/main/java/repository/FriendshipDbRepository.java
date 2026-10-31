package repository;

import domain.Friendship;
import domain.Tuple;
import domain.validator.Validator;

import java.util.List;

/**
 * Skeleton repository implementation for persisting {@link Friendship} entities in a relational database.
 * Methods currently throw {@link UnsupportedOperationException} until JDBC is implemented.
 */
public class FriendshipDbRepository implements Repository<Tuple<Long, Long>, Friendship> {
    private final Validator<Friendship> validator;
    private final String url;
    private final String username;
    private final String password;

    /**
     * Constructs a new FriendshipDbRepository with connection parameters and validator.
     *
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
     * @throws UnsupportedOperationException always, as database operations are pending implementation
     */
    @Override
    public Friendship findOne(Tuple<Long, Long> tuple) {
        throw new UnsupportedOperationException("Database repository not implemented yet!");
    }

    /**
     * @throws UnsupportedOperationException always, as database operations are pending implementation
     */
    @Override
    public List<Friendship> findAll() {
        throw new UnsupportedOperationException("Database repository not implemented yet!");
    }

    /**
     * @throws UnsupportedOperationException always, as database operations are pending implementation
     */
    @Override
    public Friendship save(Friendship entity) {
        throw new UnsupportedOperationException("Database repository not implemented yet!");
    }

    /**
     * @throws UnsupportedOperationException always, as database operations are pending implementation
     */
    @Override
    public Friendship delete(Tuple<Long, Long> tuple) {
        throw new UnsupportedOperationException("Database repository not implemented yet!");
    }
}
