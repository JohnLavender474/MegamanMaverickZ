package com.game.utils;

/**
 * Defines a pair of objects of the same type. Two pair instances are considered equal if they both contain the same
 * objects, regardless of insertion order. For Pair p1 and Pair p2, if p1.first() == p2.first() and p1.second() ==
 * p2.second(), then the two pairs are equal. Also, for Pair p1 and Pair p2, if p1.first() == p2.second() and
 * p1.second() == p1.first(), then the two pairs are equal.
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
        int hash = 7;
        hash += 49 * first.hashCode();
        hash += 49 * second.hashCode();
        return hash;
    }

}
