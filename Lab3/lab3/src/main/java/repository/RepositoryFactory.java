package repository;

import domain.Friendship;
import domain.Tuple;
import domain.User;
import domain.exception.EntityNotFoundException;
import domain.exception.PropertyNotFoundException;
import domain.exception.RepositoryException;
import domain.validator.Validator;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Factory class responsible for creating repository instances according to the configured persistence type.
 */
public class RepositoryFactory {
    private RepositoryType repoType;
    private String usersFilePath;
    private String friendshipsFilePath;
    private String dbUrl;
    private String dbUser;
    private String dbPassword;

    private void validateProperties(String key, Properties p) {
        if (p.getProperty(key) == null) {
            throw new PropertyNotFoundException("Missing property " + key);
        }
    }

    /**
     * Constructs a new RepositoryFactory and initializes configuration parameters from file.
     * @throws RepositoryException if configuration cannot be loaded or contains invalid values
     */
    public RepositoryFactory() {
        loadConfig();
    }

    /**
     * Loads the persistence configuration parameters from config.properties.
     * @throws RepositoryException if the file is missing, unreadable, or invalid
     */
    private void loadConfig() {
        Properties properties = new Properties();
        try (InputStream input = getClass().getClassLoader().getResourceAsStream("config.properties")) {
            if (input != null) {
                properties.load(input);
            } else {
                try (InputStream fileInput = new FileInputStream("src/main/resources/config.properties")) {
                    properties.load(fileInput);
                }
            }
        } catch (IOException e) {
            throw new RepositoryException("Failed to read configuration properties file", e);
        }
        String typeStr = properties.getProperty("repository.type");
        if (typeStr == null || typeStr.trim().isEmpty()) {
            throw new RepositoryException("Missing 'repository.type' property in config.properties");
        }
        try {
            this.repoType = RepositoryType.valueOf(typeStr.trim().toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new RepositoryException("Unknown repository type specified: " + typeStr, e);
        }
        try {
            this.usersFilePath = properties.getProperty("repo.file.users");
            validateProperties(usersFilePath, properties);
            this.friendshipsFilePath = properties.getProperty("repo.file.friendships");
            validateProperties(friendshipsFilePath, properties);
            this.dbUrl = properties.getProperty("db.url");
            validateProperties(dbUrl, properties);
            this.dbUser = properties.getProperty("db.user");
            validateProperties(dbUser, properties);
            this.dbPassword = properties.getProperty("db.password");
            validateProperties(dbPassword, properties);
        } catch (PropertyNotFoundException e) {
            throw new RepositoryException("Repository exception: ", e);
        }
    }

    /**
     * Creates and returns a repository instance for managing {@link User} entities.
     * @param validator validator instance for user validation
     * @return the concrete {@link Repository} configured for the current persistence type
     * @throws RepositoryException if the persistence type is unsupported
     */
    public Repository<Long, User> createUserRepository(Validator<User> validator) {
        switch (repoType) {
            case MEMORY:
                return new InMemoryRepository<Long, User>(validator);
            case FILE:
                return new UserFileRepository(validator, usersFilePath);
            case DATABASE:
                return new UserDbRepository(validator, dbUrl, dbUser, dbPassword);
            default:
                throw new RepositoryException("Unsupported repository type: " + repoType);
        }
    }

    /**
     * Creates and returns a repository instance for managing {@link Friendship} entities.
     * @param validator validator instance for friendship validation
     * @return the concrete {@link Repository} configured for the current persistence type
     * @throws RepositoryException if the persistence type is unsupported
     */
    public Repository<Tuple<Long, Long>, Friendship> createFriendshipRepository(Validator<Friendship> validator) {
        switch (repoType) {
            case MEMORY:
                return new InMemoryRepository<Tuple<Long, Long>, Friendship>(validator);
            case FILE:
                return new FriendshipFileRepository(validator, friendshipsFilePath);
            case DATABASE:
                return new FriendshipDbRepository(validator, dbUrl, dbUser, dbPassword);
            default:
                throw new RepositoryException("Unsupported repository type: " + repoType);
        }
    }
}
