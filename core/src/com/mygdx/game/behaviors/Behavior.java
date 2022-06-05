package com.mygdx.game.behaviors;

import com.mygdx.game.utils.Updatable;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

/**
 * Defines the update cycle of a behavior. Optional {@link #userData} field can be used to store a reference to
 * the "container" of this class that doesn't extend {@link Behavior}, or any other object desired.
 */
@RequiredArgsConstructor
public class Behavior implements Updatable {

    private final List<Supplier<Boolean>> overrides = new ArrayList<>();
    @Getter private Boolean runningPrior = false;
    @Getter private Boolean runningNow = false;
    private final Supplier<Boolean> evaluator;
    @Getter @Setter private Object userData;
    private final Runnable initializer;
    private final Updatable actuator;
    private final Runnable ender;

    /**
     * Add override.
     *
     * @param override the override
     */
    public void addOverride(Supplier<Boolean> override) {
        overrides.add(override);
    }

    @Override
    public void update(float delta) {
        runningPrior = runningNow;
        runningNow = overrides.stream().noneMatch(Supplier::get) && evaluator.get();
        if (runningNow && !runningPrior) {
            initializer.run();
        }
        if (runningNow) {
            actuator.update(delta);
        }
        if (!runningNow && runningPrior) {
            ender.run();
        }
    }

}
