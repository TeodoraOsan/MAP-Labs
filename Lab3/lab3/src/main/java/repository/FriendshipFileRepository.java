package repository;

import domain.Friendship;
import domain.Tuple;
import domain.exception.RepositoryException;
import domain.validator.Validator;

import java.time.LocalDateTime;
import java.util.List;

public class FriendshipFileRepository extends AbstractFileRepository<Tuple<Long, Long>, Friendship> {
    public FriendshipFileRepository(Validator<Friendship> validator, String fileName) {
        super(validator, fileName);
    }

    @Override
    protected Friendship extractEntity(List<String> attributes) {
        if (attributes.size() < 3) {
            throw new RepositoryException("Insufficient number of fields");
        }
        Long u1 = Long.parseLong(attributes.get(0).trim());
        Long u2 = Long.parseLong(attributes.get(1).trim());
        LocalDateTime date = LocalDateTime.parse(attributes.get(2).trim());
        Friendship friendship = new Friendship(u1, u2, date);
        return friendship;
    }

    @Override
    protected String createEntityAsString(Friendship entity) {
        return entity.getFirstUserId() + ";" + entity.getSecondUserId() + ";" + entity.getDate().toString();
    }
}
