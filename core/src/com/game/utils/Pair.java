package com.game.utils;

public record Pair<T>(T first, T second) {

    public boolean contains(T t) {
        return t.equals(first) || t.equals(second);
    }

}
