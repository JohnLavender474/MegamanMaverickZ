package com.game.utils.objects;

import lombok.*;

import java.util.function.Consumer;

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
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class Pair<T> {

    private T first;
    private T second;
    private boolean swappable;

    public Pair(T first, T second) {
        this(first, second, true);
    }

    public static <U> Pair<U> of(U u1, U u2) {
        return new Pair<>(u1, u2);
    }

    public void set(Pair<T> p) {
        set(p.getFirst(), p.getSecond());
    }

    public void set(T first, T second) {
        setFirst(first);
        setSecond(second);
    }

    public void forEach(Consumer<T> consumer) {
        consumer.accept(first);
        consumer.accept(second);
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
