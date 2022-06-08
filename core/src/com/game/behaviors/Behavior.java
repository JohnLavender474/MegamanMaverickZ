package com.game.behaviors;

import com.game.utils.Updatable;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

@Getter
@RequiredArgsConstructor
public abstract class Behavior implements Updatable {

    private boolean runningNow = false;
    private boolean runningPrior = false;
    private final List<Supplier<Boolean>> overrides = new ArrayList<>();

    protected abstract boolean evaluate(float delta);

    protected abstract void init();

    protected abstract void act(float delta);

    protected abstract void end();

    public void addOverride(Supplier<Boolean> override) {
        overrides.add(override);
    }

    @Override
    public void update(float delta) {
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
