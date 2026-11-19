package org.example.util.paging;

/**
 * Represents a page of elements along with the total number of elements.
 * @param <E> the type of elements on the page
 */
public class Page<E> {
    private final Iterable<E> elementsOnPage;
    private final int totalNumberOfElements;

    /**
     * Constructs a Page object.
     * @param elementsOnPage        the elements contained in this page
     * @param totalNumberOfElements the total number of elements across all pages
     */
    public Page(Iterable<E> elementsOnPage, int totalNumberOfElements) {
        this.elementsOnPage = elementsOnPage;
        this.totalNumberOfElements = totalNumberOfElements;
    }

    public Iterable<E> getElementsOnPage() {
        return elementsOnPage;
    }

    public int getTotalNumberOfElements() {
        return totalNumberOfElements;
    }
}
