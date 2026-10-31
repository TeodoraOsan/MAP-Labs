package repository;

import domain.User;
import domain.validator.Validator;

import java.util.List;

/**
 * Skeleton repository implementation for persisting {@link User} entities in a relational database.
 * Methods currently throw {@link UnsupportedOperationException} until JDBC is implemented.
 */
public class UserDbRepository implements Repository<Long, User> {
    private final Validator<User> validator;
    private final String url;
    private final String username;
    private final String password;

    /**
     * Constructs a new UserDbRepository with connection parameters and validator.
     *
     * @param validator the entity validator
     * @param url       the database connection URL
     * @param username  the database username
     * @param password  the database password
     */
    public UserDbRepository(Validator<User> validator, String url, String username, String password) {
        this.validator = validator;
        this.url = url;
        this.username = username;
        this.password = password;
    }

    /**
     * @throws UnsupportedOperationException always, as database operations are pending implementation
     */
    @Override
    public User findOne(Long aLong) {
        throw new UnsupportedOperationException("Database repository not implemented yet!");
    }

    /**
     * @throws UnsupportedOperationException always, as database operations are pending implementation
     */
    @Override
    public List<User> findAll() {
        throw new UnsupportedOperationException("Database repository not implemented yet!");
    }

    /**
     * @throws UnsupportedOperationException always, as database operations are pending implementation
     */
    @Override
    public User save(User entity) {
        throw new UnsupportedOperationException("Database repository not implemented yet!");
    }

    /**
     * @throws UnsupportedOperationException always, as database operations are pending implementation
     */
    @Override
    public User delete(Long aLong) {
        throw new UnsupportedOperationException("Database repository not implemented yet!");
    }
}
