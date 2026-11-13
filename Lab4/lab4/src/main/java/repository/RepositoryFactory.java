package repository;

import domain.Friendship;
import domain.Tuple;
import domain.User;
import domain.exception.PropertyNotFoundException;
import domain.exception.RepositoryException;
import domain.validator.Validator;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.util.EnumMap;
import java.util.Map;
import java.util.Properties;
import java.util.function.Function;

/**
 * Factory class responsible for creating repository instances according to the configured persistence type.
 * Dispatches repository creation using functional maps.
 */
public class RepositoryFactory {
    private RepositoryType repoType;
    private String usersFilePath;
    private String friendshipsFilePath;
    private String dbUrl;
    private String dbUser;
    private String dbPassword;

    private final Map<RepositoryType, Function<Validator<User>, Repository<Long, User>>> userRepoProviders = new EnumMap<>(RepositoryType.class);
    private final Map<RepositoryType, Function<Validator<Friendship>, Repository<Tuple<Long, Long>, Friendship>>> friendshipRepoProviders = new EnumMap<>(RepositoryType.class);

    /**
     * Constructs a new {@code RepositoryFactory} instance.
     * Reads the application persistence configuration from {@code config.properties} and sets up the functional provider mappings for each supported {@link RepositoryType}.
     * @throws RepositoryException if the configuration file is unreadable, corrupted, missing mandatory properties, or specifies an invalid repository type
     */
    public RepositoryFactory() {
        loadConfig();
        initProviders();
    }

    private void initProviders() {
        userRepoProviders.put(RepositoryType.MEMORY, InMemoryRepository::new);
        userRepoProviders.put(RepositoryType.FILE, validator -> new FileRepository<>(
                validator,
                usersFilePath,
                this::extractUser,
                this::createUserLine
        ));
        userRepoProviders.put(RepositoryType.DATABASE, validator -> new UserDbRepository(validator, dbUrl, dbUser, dbPassword));

        friendshipRepoProviders.put(RepositoryType.MEMORY, InMemoryRepository::new);
        friendshipRepoProviders.put(RepositoryType.FILE, validator -> new FileRepository<>(
                validator,
                friendshipsFilePath,
                this::extractFriendship,
                this::createFriendshipLine
        ));
        friendshipRepoProviders.put(RepositoryType.DATABASE, validator -> new FriendshipDbRepository(validator, dbUrl, dbUser, dbPassword));
    }

    private User extractUser(String line) {
        String[] tokens = line.split(";", -1);
        if (tokens.length < 3) {
            throw new RepositoryException("Insufficient number of fields");
        }
        Long id = Long.parseLong(tokens[0].trim());
        User u = new User(tokens[1].trim(), tokens[2].trim());
        u.setId(id);
        return u;
    }

    private String createUserLine(User u) {
        return u.getId() + ";" + u.getFirstName() + ";" + u.getLastName();
    }

    private Friendship extractFriendship(String line) {
        String[] tokens = line.split(";", -1);
        if (tokens.length < 3) {
            throw new RepositoryException("Insufficient number of fields");
        }
        Long u1 = Long.parseLong(tokens[0].trim());
        Long u2 = Long.parseLong(tokens[1].trim());
        LocalDateTime date = LocalDateTime.parse(tokens[2].trim());
        return new Friendship(u1, u2, date);
    }

    private String createFriendshipLine(Friendship f) {
        return f.getFirstUserId() + ";" + f.getSecondUserId() + ";" + f.getDate();
    }

    private void validateProperty(Properties p, String key) {
        if (p.getProperty(key) == null) {
            throw new PropertyNotFoundException("Missing property " + key);
        }
    }

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
            validateProperty(properties, "repo.file.users");
            validateProperty(properties, "repo.file.friendships");
            validateProperty(properties, "db.url");
            validateProperty(properties, "db.user");
            validateProperty(properties, "db.password");

            this.usersFilePath = properties.getProperty("repo.file.users");
            this.friendshipsFilePath = properties.getProperty("repo.file.friendships");
            this.dbUrl = properties.getProperty("db.url");
            this.dbUser = properties.getProperty("db.user");
            this.dbPassword = properties.getProperty("db.password");
        } catch (IllegalArgumentException e) {
            throw new RepositoryException("Unknown repository type specified: " + typeStr, e);
        }
    }

    /**
     * Creates and returns a repository instance configured for managing {@link User} entities.
     * The concrete implementation is selected dynamically based on the configured {@link RepositoryType} by querying the internal functional provider mapping.
     * @param validator the entity validator to be used by the created repository
     * @return a concrete {@link Repository} instance for {@link User} entities
     * @throws RepositoryException if no provider is mapped for the current repository type
     */
    public Repository<Long, User> createUserRepository(Validator<User> validator) {
        Function<Validator<User>, Repository<Long, User>> provider = userRepoProviders.get(repoType);
        if (provider == null) {
            throw new RepositoryException("Unsupported repository type: " + repoType);
        }
        return provider.apply(validator);
    }

    /**
     * Creates and returns a repository instance configured for managing {@link Friendship} entities.
     * The concrete implementation is selected dynamically based on the configured {@link RepositoryType} by querying the internal functional provider mapping.
     * @param validator the entity validator to be used by the created repository
     * @return a concrete {@link Repository} instance for {@link Friendship} entities
     * @throws RepositoryException if no provider is mapped for the current repository type
     */
    public Repository<Tuple<Long, Long>, Friendship> createFriendshipRepository(Validator<Friendship> validator) {
        Function<Validator<Friendship>, Repository<Tuple<Long, Long>, Friendship>> provider = friendshipRepoProviders.get(repoType);
        if (provider == null) {
            throw new RepositoryException("Unsupported repository type: " + repoType);
        }
        return provider.apply(validator);
    }
}