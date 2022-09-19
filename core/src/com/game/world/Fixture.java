package com.game.world;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Shape2D;
import com.badlogic.gdx.math.Vector2;
import com.game.core.Entity;
import com.game.utils.enums.ProcessState;
import com.game.utils.objects.Process;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.util.*;

import static com.badlogic.gdx.graphics.Color.*;
import static java.util.Collections.*;

/**
 * Defines a fixture sensor attached to a body. Offset is from the center pairOf the body.
 */
@Getter
@Setter
@RequiredArgsConstructor
public class Fixture {

    private final Entity entity;
    private final Shape2D fixtureShape;
    private final FixtureType fixtureType;
    private final Vector2 offset = new Vector2();
    private final Map<String, Object> userData = new HashMap<>();
    private final Map<FixtureType, Set<ProcessState>> scanner = new EnumMap<>(FixtureType.class) {{
        for (FixtureType f : FixtureType.values()) {
            put(f, EnumSet.noneOf(ProcessState.class));
        }
    }};

    private boolean active = true;
    private Color debugColor = YELLOW;

    public boolean isFixtureType(FixtureType fixtureType) {
        return this.fixtureType.equals(fixtureType);
    }

    public boolean isAnyFixtureType(FixtureType... fixtureTypes) {
        for (FixtureType fixtureType : fixtureTypes) {
            if (isFixtureType(fixtureType)) {
                return true;
            }
        }
        return false;
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
     * @param key the key
     * @return the data
     */
    public Object getUserData(String key) {
        return userData.get(key);
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
        return tClass.cast(getUserData(key));
    }

    /**
     * Returns if there is user data mapped to the key.
     *
     * @param key the key
     * @return if there is user data mapped to the key
     */
    public boolean containsUserDataKey(String key) {
        return userData.containsKey(key);
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
     * Adds the contact process state.
     *
     * @param f the fixture type
     * @param p the process state
     */
    public void addContactProcessState(FixtureType f, ProcessState p) {
        scanner.get(f).add(p);
    }

    /**
     * Gets the set of contact process states for the fixture type.
     *
     * @param f the fixture type
     * @return the process state
     */
    public Set<ProcessState> getContactProcessStates(FixtureType f) {
        return unmodifiableSet(scanner.get(f));
    }

    /** Clears the contact process states for each fixture type. */
    public void resetContactProcessStates() {
        scanner.forEach((f, p) -> p.clear());
    }

    /**
     * Returns if there is a contact in process with the fixture type.
     *
     * @param f the fixture type
     * @return if a contact is in process
     */
    public boolean isContactInProcess(FixtureType f) {
        return !scanner.get(f).isEmpty();
    }

    /**
     * Returns if there is a contact in the specified process state with the fixture type.
     *
     * @param f the fixture type
     * @param p the process state
     * @return if there is a contact in the specified process state with the fixture type
     */
    public boolean isContactInProcess(FixtureType f, ProcessState p) {
        return scanner.get(f).contains(p);
    }

}
