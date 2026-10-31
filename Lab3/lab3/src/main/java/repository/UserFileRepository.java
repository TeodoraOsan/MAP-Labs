package repository;

import domain.User;
import domain.exception.RepositoryException;
import domain.validator.Validator;

import java.util.List;

public class UserFileRepository extends AbstractFileRepository<Long, User> {
    public UserFileRepository(Validator<User> validator, String filename) {
        super(validator, filename);
    }

    @Override
    protected User extractEntity(List<String> attributes) {
        if (attributes.size() < 3) {
            throw new RepositoryException("Insufficient number of fields");
        }
        Long id = Long.parseLong(attributes.get(0).trim());
        String firstName = attributes.get(1).trim();
        String lastName = attributes.get(2).trim();

        User u = new User(firstName, lastName);
        u.setId(id);
        return u;
    }

    @Override
    protected String createEntityAsString(User entity) {
        return entity.getId() + ";" + entity.getFirstName() + ";" + entity.getLastName();
    }
}
