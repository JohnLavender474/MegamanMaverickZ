package com.game.cull;

import com.game.System;
import com.game.core.IEntity;
import com.game.updatables.Updatable;
import com.game.utils.enums.ProcessState;
import lombok.Setter;

import java.util.Set;
import java.util.function.Supplier;

@Setter
public class CullOnCamTransSystem extends System {

    private Supplier<ProcessState> transitionStateSupplier;
    private Updatable onContinue;
    private Runnable onBegin;
    private Runnable onEnd;

    public CullOnCamTransSystem() {
        this(null);
    }

    public CullOnCamTransSystem(Supplier<ProcessState> transitionStateSupplier) {
        super(Set.of(CullOnCamTransComponent.class));
        this.transitionStateSupplier = transitionStateSupplier;
    }

    @Override
    public void update(float delta) {
        if (transitionStateSupplier.get() == null) {
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
        switch (transitionStateSupplier.get()) {
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
