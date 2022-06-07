package com.game.world;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.game.utils.ProcessState;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.EnumMap;
import java.util.Map;

/**
 * The type Fixture.
 */
@Getter
@Setter
@ToString
public class Fixture {

    private final FixtureType fixtureType;
    private final Vector2 offset = new Vector2();
    private final Rectangle fixtureBox = new Rectangle();
    private final Map<ProcessState, Map<FixtureType, Runnable>> listeners = new EnumMap<>(ProcessState.class);

    /**
     * Instantiates a new Fixture.
     *
     * @param fixtureType the fixture type
     */
    public Fixture(FixtureType fixtureType) {
        this.fixtureType = fixtureType;
        for (ProcessState processState : ProcessState.values()) {
            listeners.put(processState, new EnumMap<>(FixtureType.class));
        }
    }

    /**
     * Is listening for contact boolean.
     *
     * @param processState the process state
     * @param fixtureType  the fixture type
     * @return the boolean
     */
    public boolean isListeningForContact(ProcessState processState, FixtureType fixtureType) {
        return listeners.get(processState).containsKey(fixtureType);
    }

    /**
     * Run contact listener.
     *
     * @param processState the process state
     * @param fixtureType  the fixture type
     */
    public void runContactListener(ProcessState processState, FixtureType fixtureType) {
        listeners.get(processState).get(fixtureType).run();
    }

    /**
     * Put contact listener.
     *
     * @param processState the process state
     * @param fixtureType  the fixture type
     * @param runnable     the runnable
     */
    public void putContactListener(ProcessState processState, FixtureType fixtureType, Runnable runnable) {
        listeners.get(processState).put(fixtureType, runnable);
    }

}
