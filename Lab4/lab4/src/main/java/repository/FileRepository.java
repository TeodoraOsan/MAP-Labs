package repository;

import domain.Entity;
import domain.exception.RepositoryException;
import domain.validator.Validator;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

/**
 * Generic file repository implementation using composition with mapper functions.
 * @param <ID> the type of the entity identifier
 * @param <E>  the entity type, extending {@link Entity}
 */
public class FileRepository<ID, E extends Entity<ID>> extends InMemoryRepository<ID, E> {
    private final Path filePath;
    private final Function<String, E> lineToEntity;
    private final Function<E, String> entityToLine;

    /**
     * Constructs a new FileRepository.
     * @param validator    the entity validator
     * @param fileName     the path to the text file where data is persisted
     * @param lineToEntity function deserializing a single line into a domain entity
     * @param entityToLine function serializing a domain entity into a single-line string
     */
    public FileRepository(Validator<E> validator,
                          String fileName,
                          Function<String, E> lineToEntity,
                          Function<E, String> entityToLine) {
        super(validator);
        this.filePath = Paths.get(fileName);
        this.lineToEntity = Objects.requireNonNull(lineToEntity, "lineToEntity function must not be null");
        this.entityToLine = Objects.requireNonNull(entityToLine, "entityToLine function must not be null");
        loadData();
    }

    /**
     * Loads entities from the file into memory. Corrupted lines are skipped and reported.
     */
    private void loadData() {
        if (!Files.exists(filePath)) {
            return;
        }

        try (Stream<String> linesStream = Files.lines(filePath)) {
            List<String> lines = linesStream.collect(Collectors.toList());

            IntStream.range(0, lines.size())
                    .filter(i -> !lines.get(i).trim().isEmpty())
                    .forEach(i -> {
                        String line = lines.get(i).trim();
                        try {
                            E entity = lineToEntity.apply(line);
                            if (entity != null) {
                                super.save(entity);
                            }
                        } catch (Exception e) {
                            System.err.println("Corrupted line skipped at line " + (i + 1) + ": " + e.getMessage());
                        }
                    });
        } catch (IOException e) {
            throw new RepositoryException("Error at reading from file " + filePath, e);
        }
    }

    /**
     * Writes all entities stored in memory to the destination text file.
     */
    private void writeToFile() {
        try {
            if (filePath.getParent() != null && !Files.exists(filePath.getParent())) {
                Files.createDirectories(filePath.getParent());
            }

            List<String> lines = super.findAll().stream()
                    .map(entityToLine)
                    .collect(Collectors.toList());

            Files.write(filePath, lines, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
        } catch (IOException e) {
            throw new RepositoryException("Error at writing to file " + filePath, e);
        }
    }

    @Override
    public E save(E entity) {
        E result = super.save(entity);
        if (result == null) {
            writeToFile();
        }
        return result;
    }

    @Override
    public Optional<E> delete(ID id) {
        Optional<E> result = super.delete(id);
        if (result.isPresent()) {
            writeToFile();
        }
        return result;
    }
}
