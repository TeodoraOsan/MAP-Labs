package repository;

import domain.Entity;
import domain.exception.RepositoryException;
import domain.validator.Validator;

import java.io.*;
import java.util.Arrays;
import java.util.List;

/**
 * Abstract repository that persists data to a plain text file.
 * Implements the Template Method pattern for entity reading and writing.
 * @param <ID> the type of entity identifier
 * @param <E>  the entity type, extending {@link Entity}
 */
public abstract class AbstractFileRepository<ID, E extends Entity<ID>> extends InMemoryRepository<ID, E> {
    private final String fileName;

    /**
     * Constructs an AbstractFileRepository.
     * @param validator the entity validator
     * @param fileName  the path to the text file where data is persisted
     */
    public AbstractFileRepository(Validator<E> validator, String fileName) {
        super(validator);
        this.fileName = fileName;
        loadData();
    }

    /**
     * Converts a list of text tokens read from a file line into a domain entity.
     * @param attributes list of string values representing entity attributes
     * @return the reconstructed entity instance
     */
    protected abstract E extractEntity(List<String> attributes);

    /**
     * Converts a domain entity into a single-line string representation for file storage.
     * @param entity the entity to format
     * @return line string with semicolon-delimited values
     */
    protected abstract String createEntityAsString(E entity);

    /**
     * Loads existing entities from the file into memory.
     * Corrupted lines are logged and skipped without crashing the application.
     * @throws domain.exception.RepositoryException if an I/O error occurs while reading
     */
    protected void loadData() {
        File file = new File(fileName);
        if (!file.exists()) {
            return;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            int lineNumber = 0;
            while ((line = reader.readLine()) != null) {
                lineNumber++;
                line = line.trim();
                if (line.isEmpty()) {
                    continue;
                }
                try {
                    // split(";", -1) pastreaza campurile goale de la final
                    String[] tokens = line.split(";", -1);
                    List<String> attributes = Arrays.asList(tokens);
                    E entity = extractEntity(attributes);
                    if (entity != null) {
                        super.save(entity);
                    }
                } catch (Exception e) {
                    System.err.println("Linie corupta ignorata la linia " + lineNumber + ": " + e.getMessage());
                }
            }
        } catch (IOException e) {
            throw new RepositoryException("Eroare la citirea fisierului " + fileName, e);
        }
    }

    private void writeToFile() {
        File file = new File(fileName);
        File parent = file.getParentFile();
        if (parent != null && !parent.exists()) {
            parent.mkdirs();
        }

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
            List<E> allEntities = super.findAll();
            for (E entity : allEntities) {
                writer.write(createEntityAsString(entity));
                writer.newLine();
            }
        } catch (IOException e) {
            throw new RepositoryException("Eroare la scrierea in fisierul " + fileName, e);
        }
    }

    /**
     * Saves an entity in memory and synchronizes the change to the text file if successful.
     * @param entity the entity to be saved
     * @return {@code null} if the entity was successfully saved, or the existing entity if an ID conflict occurs
     */
    @Override
    public E save(E entity) {
        E result = super.save(entity);
        if (result == null) {
            writeToFile();
        }
        return result;
    }

    /**
     * Deletes an entity from memory and updates the file if an entity was removed.
     * @param id the identifier of the entity to remove
     * @return the deleted entity, or {@code null} if not found
     */
    @Override
    public E delete(ID id) {
        E result = super.delete(id);
        if (result != null) {
            writeToFile();
        }
        return result;
    }
}