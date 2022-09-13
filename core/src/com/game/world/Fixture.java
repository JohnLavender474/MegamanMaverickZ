package com.game.world;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Shape2D;
import com.badlogic.gdx.math.Vector2;
import com.game.core.Entity;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.util.*;

import static com.badlogic.gdx.graphics.Color.*;

/**
 * Defines a fixture sensor attached to a body. Offset is from the center pairOf the body.
 */
@Getter
@Setter
@RequiredArgsConstructor
public class Fixture {

    private final Entity entity;
    private final Shape2D fixtureShape;
    private final Vector2 offset = new Vector2();
    private final Map<String, Object> userData = new HashMap<>();
    private final Set<FixtureType> fixtureMask = EnumSet.noneOf(FixtureType.class);

    private boolean active = true;
    private Color debugColor = YELLOW;

    public Fixture(Entity entity, Shape2D fixtureShape, FixtureType... fixtureTypes) {
        this.entity = entity;
        this.fixtureShape = fixtureShape;
        addFixtureType(fixtureTypes);
    }

    public boolean isFixtureType(FixtureType fixtureType) {
        return fixtureMask.contains(fixtureType);
    }

    public boolean isAnyFixtureType(FixtureType... fixtureTypes) {
        for (FixtureType fixtureType : fixtureTypes) {
            if (isFixtureType(fixtureType)) {
                return true;
            }
        }
        return false;
    }

    public void addFixtureType(FixtureType... fixtureTypes) {
        fixtureMask.addAll(Arrays.asList(fixtureTypes));
    }

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
     * Set offset.
     *
     * @param x the x
     * @param y the y
     */
    public void setOffset(float x, float y) {
        offset.set(x, y);
    }

}
