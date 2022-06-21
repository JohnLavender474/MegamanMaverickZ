package com.game.core;

public interface IBlackboard {

    /**
     * Put blackboard object.
     *
     * @param key    the key
     * @param object the object
     */
    void putBlackboardObject(String key, Object object);

    /**
     * Get blackboard object.
     *
     * @param <T>    the type parameter of the object
     * @param key    the key
     * @param tClass the class to cast the object to
     * @return the blackboard object
     */
    <T> T getBlackboardObject(String key, Class<T> tClass);

}
