package com.game.world;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.game.core.IEntity;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.util.HashMap;
import java.util.Map;

/**
 * Defines a fixture fixed to a body. Offset is from the center of the body.
 */
@Getter
@Setter
@RequiredArgsConstructor
public class Fixture {

    private final IEntity entity;
    private final FixtureType fixtureType;
    private final Vector2 offset = new Vector2();
    private final Rectangle fixtureBox = new Rectangle();
    private final Map<String, Object> userData = new HashMap<>();

    private boolean active = true;
    private Color debugColor = Color.YELLOW;

    /**
     * Put user data
     *
     * @param key  the key
     * @param data the data
     */
    public void putUserData(String key, Object data) {
        userData.put(key, data);
    }

    /**
     * Get user data.
     *
     * @param key    the key
     * @param tClass the class
     * @param <T>    the data type
     * @return the data
     */
    public <T> T getUserData(String key, Class<T> tClass) {
        return tClass.cast(userData.get(key));
    }

    /**
     * Has user data.
     *
     * @param key    the key
     * @param tClass the class
     * @param <T>    the data type
     * @return if has data
     */
    public <T> boolean hasUserData(String key, Class<T> tClass) {
        return userData.containsKey(key) && tClass.isAssignableFrom(userData.get(key).getClass());
    }

    /**
     * Set.
     *
     * @param bounds the bounds
     */
    public void set(Rectangle bounds) {
        fixtureBox.set(bounds);
    }

    /**
     * Set.
     *
     * @param x      the x
     * @param y      the y
     * @param width  the width
     * @param height the height
     */
    public void set(float x, float y, float width, float height) {
        fixtureBox.set(x, y, width, height);
    }

    /**
     * Set offset.
     *
     * @param x the x
     * @param y the y
     */
    public void setOffset(float x, float y) {
        offset.set(x, y);
    }

    /**
     * Sets size.
     *
     * @param x the x
     * @param y the y
     */
    public void setSize(float x, float y) {
        fixtureBox.setSize(x, y);
    }

    /**
     * Set width.
     *
     * @param x the width
     */
    public void setWidth(float x) {
        fixtureBox.setWidth(x);
    }

    /**
     * Set height.
     *
     * @param y the height
     */
    public void setHeight(float y) {
        fixtureBox.setHeight(y);
    }

    /**
     * Set center.
     *
     * @param center the center
     */
    public void setCenter(Vector2 center) {
        setCenter(center.x, center.y);
    }

    /**
     * Set center.
     *
     * @param x the x
     * @param y the y
     */
    public void setCenter(float x, float y) {
        fixtureBox.setCenter(x, y);
    }

}
