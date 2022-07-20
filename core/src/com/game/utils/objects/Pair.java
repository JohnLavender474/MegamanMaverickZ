package com.game.utils.objects;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * A homosexual pair of T's. T stands for tests of course. But in all seriousness...
 *
 * Defines a pair of objects of the same type. Two pair instances are equal depending on the value of the boolean
 * swappable. If swappable is true, then two Pair objects are equal if they contain the same objects regardless of
 * which ones specifically are first or second. Otherwise, the two Pair objects are equal only if the first parameter
 * of the first Pair is equal to the first parameter of the second Pair, and the second parameter of the first Pair is
 * equal to the second parameter of the second Pair.
 *
 * @param <T> the type parameter
 */
@Getter
@RequiredArgsConstructor
public class Pair<T> {

    private final T first;
    private final T second;
    private final boolean swappable;

    public Pair(T first, T second) {
        this(first, second, true);
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Pair<?> pair)) {
            return false;
        }
        if (first.equals(pair.getFirst())) {
            return second.equals(pair.getSecond());
        }
        if (isSwappable() && first.equals(pair.getSecond())) {
            return second.equals(pair.getFirst());
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
