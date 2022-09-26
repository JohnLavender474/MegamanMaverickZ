package com.game.utils.objects;

/**
 * Key-value pair.
 *
 * @param <K> the key parameter
 * @param <V> the value parameter
 */
public record KeyValuePair<K, V>(K key, V value) {

    public static <T, U> KeyValuePair<T, U> of(T t, U u) {
        return new KeyValuePair<>(t, u);
    }

}
