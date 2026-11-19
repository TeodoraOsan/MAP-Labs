package org.example.repository;

import org.example.domain.Entity;
import org.example.util.paging.Page;
import org.example.util.paging.Pageable;

/**
 * Repository interface that supports pagination.
 * @param <ID> the type of the entity identifier
 * @param <E>  the type of the entity
 */
public interface PagingRepository<ID, E extends Entity<ID>> extends Repository<ID, E> {
    /**
     * Retrieves a page of entities based on the provided pagination parameters.
     * @param pageable the pagination parameters (page number and page size)
     * @return a Page containing the elements on the requested page and the total element count
     * @throws IllegalArgumentException if pageNumber < 0 or pageSize <= 0
     */
    Page<E> findAllOnPage(Pageable pageable);
}
