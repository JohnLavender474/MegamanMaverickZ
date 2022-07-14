package com.game.utils;

public interface UpdatableConsumer<T> {

    void consumeAndUpdate(T t, float delta);

}
