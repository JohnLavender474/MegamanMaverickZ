package com.game.world;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.game.entities.Entity;
import com.game.utils.ProcessState;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.util.EnumMap;
import java.util.Map;

/**
 * Defines a fixture fixed to a body. Offset is from the center of the body.
 */
@Getter
@Setter
@ToString
@RequiredArgsConstructor
public class Fixture {

    private Object userData;
    private final Entity entity;
    private final FixtureType fixtureType;
    private final Vector2 offset = new Vector2();
    private final Rectangle fixtureBox = new Rectangle();

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

    /**
     * Get user data.
     *
     * @param <T>    the type parameter
     * @param tClass the t class
     * @return the user data
     */
    public <T> T getUserData(Class<T> tClass) {
        return tClass.cast(userData);
    }

    @Override
    public boolean equals(Object o) {
        return o instanceof Fixture fixture && fixture.getFixtureType().equals(fixtureType);
    }

    @Override
    public int hashCode() {
        return 49 + 7 * fixtureType.hashCode();
    }

}
