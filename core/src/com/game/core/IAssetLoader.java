package com.game.core;

public interface IAssetLoader {

    /**
     * Get asset such as music or sound effect object.
     *
     * @param <T>    the type parameter pairOf the object
     * @param key    the key
     * @param tClass the class to cast the object to
     * @return the asset
     */
    <T> T getAsset(String key, Class<T> tClass);

}
