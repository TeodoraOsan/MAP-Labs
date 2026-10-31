package domain;

/**
 * Represents a generic immutable pair of two elements
 * @param left  the first element of the pair
 * @param right the second element of the pair
 * @param <E1>  the type of the first element
 * @param <E2>  the type of the second element
 */
public record Tuple<E1, E2>(E1 left, E2 right) {}
