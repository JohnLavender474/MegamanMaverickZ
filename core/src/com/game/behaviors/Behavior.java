package com.game.behaviors;

import com.game.utils.Updatable;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

/**
 * Defines a behavior that can be executed.
 */
@Getter
@RequiredArgsConstructor
public abstract class Behavior implements Updatable {

    private boolean runningNow = false;
    private boolean runningPrior = false;
    private final List<Supplier<Boolean>> overrides = new ArrayList<>();

    /**
     * Returns if the behavior is accepted to run
     *
     * @param delta the delta time
     * @return if the behavior is accepted to run
     */
    protected abstract boolean evaluate(float delta);

    /**
     * Init.
     */
    protected abstract void init();

    /**
     * Act.
     *
     * @param delta the delta time
     */
    protected abstract void act(float delta);

    /**
     * End.
     */
    protected abstract void end();

    /**
     * Add override.
     *
     * @param override the override
     */
    public void addOverride(Supplier<Boolean> override) {
        overrides.add(override);
    }

    @Override
    public final void update(float delta) {
        runningPrior = runningNow;
        runningNow = overrides.stream().noneMatch(Supplier::get) && evaluate(delta);
        if (runningNow && !runningPrior) {
            init();
        }
        if (runningNow) {
            act(delta);
        }
        if (!runningNow && runningPrior) {
            end();
        }
    }

}
