package com.mygdx.game.utils;

/**
 * Represents a key-value pair of two object instances each of a different type. Can also be treated merely as a pair
 * with neither one "dominant" nor "mapping to" the other.
 *
 * @param <K> the key parameter
 * @param <V> the value parameter
 */
public record KeyValuePair<K, V>(K key, V value) {}
