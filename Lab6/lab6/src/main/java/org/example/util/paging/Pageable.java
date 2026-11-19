package org.example.util.paging;

/**
 * Represents pagination information such as page number and page size.
 */
public class Pageable {
    private final int pageNumber;
    private final int pageSize;

    /**
     * Constructs a Pageable object.
     * @param pageNumber the zero-based page index
     * @param pageSize   the maximum number of elements per page
     */
    public Pageable(int pageNumber, int pageSize) {
        this.pageNumber = pageNumber;
        this.pageSize = pageSize;
    }

    public int getPageNumber() {
        return pageNumber;
    }

    public int getPageSize() {
        return pageSize;
    }
}
