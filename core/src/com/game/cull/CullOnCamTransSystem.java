package com.game.cull;

import com.game.System;
import com.game.core.IEntity;
import com.game.updatables.Updatable;
import com.game.utils.ProcessState;
import lombok.Setter;

import java.util.Set;
import java.util.function.Supplier;

@Setter
public class CullOnCamTransSystem extends System {

    private final Supplier<ProcessState> transitionState;

    private Updatable onContinue;
    private Runnable onBegin;
    private Runnable onEnd;

    public CullOnCamTransSystem(Supplier<ProcessState> transitionState) {
        super(Set.of(CullOnCamTransComponent.class));
        this.transitionState = transitionState;
    }

    @Override
    public void update(float delta) {
        if (transitionState.get() == null) {
            return;
        }
        super.update(delta);
    }

    @Override
    protected void processEntity(IEntity entity, float delta) {
        entity.setDead(true);
    }

    @Override
    public void postProcess(float delta) {
        switch (transitionState.get()) {
            case CONTINUE -> {
                if (onContinue != null) {
                    onContinue.update(delta);
                }
            }
            case BEGIN -> {
                if (onBegin != null) {
                    onBegin.run();
                }
            }
            case END -> {
                if (onEnd != null) {
                    onEnd.run();
                }
            }
        }
    }

}
