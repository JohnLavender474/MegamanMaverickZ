package com.game.world;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.game.core.Entity;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.util.HashMap;
import java.util.Map;

import static com.badlogic.gdx.graphics.Color.*;

/**
 * Defines a fixture sensor attached to a body. Offset is from the center of the body.
 */
@Getter
@Setter
@RequiredArgsConstructor
public class Fixture {

    private final Entity entity;
    private final FixtureType fixtureType;

    private final Vector2 offset = new Vector2();
    private final Rectangle fixtureBox = new Rectangle();
    private final Map<String, Object> userData = new HashMap<>();

    private boolean active = true;
    private Color debugColor = YELLOW;

    /**
     * Put user data.
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
     * Sets the bounds of the fixture box
     *
     * @param bounds the bounds
     */
    public void setBounds(Rectangle bounds) {
        fixtureBox.set(bounds);
    }

    /**
     * Sets the bounds of the fixture box to that of the provided fixture. See {@link #setBounds(Rectangle)}.
     *
     * @param fixture the fixture whose bounds are to be used as a reference
     */
    public void setBounds(Fixture fixture) {
        setBounds(fixture.getFixtureBox());
    }

    /**
     * Set.
     *
     * @param x      the x
     * @param y      the y
     * @param width  the width
     * @param height the height
     */
    public void setBounds(float x, float y, float width, float height) {
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
