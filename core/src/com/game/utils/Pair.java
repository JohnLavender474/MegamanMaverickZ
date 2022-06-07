package com.game.utils;

/**
 * Defines a pair of objects of the same type. Two pair instances are considered equal if they both contain the same
 * objects, regardless of insertion order.
 *
 * @param <T> the type parameter
 */
public record Pair<T>(T first, T second) {

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Pair<?> pair)) {
            return false;
        }
        if (first.equals(pair.first())) {
            return second.equals(pair.second());
        } else if (first.equals(pair.second())) {
            return second.equals(pair.first());
        }
        return false;
    }

    @Override
    public int hashCode() {
        return first.hashCode() + second.hashCode();
    }

}
