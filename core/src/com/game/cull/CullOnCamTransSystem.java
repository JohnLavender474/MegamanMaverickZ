package com.game.cull;

import com.game.Entity;
import com.game.System;
import com.game.utils.enums.ProcessState;
import lombok.Setter;

import java.util.function.Supplier;

@Setter
public class CullOnCamTransSystem extends System {

    private Supplier<ProcessState> transitionStateSupplier;

    public CullOnCamTransSystem() {
        super(CullOnCamTransComponent.class);
    }

    @Override
    public void update(float delta) {
        if (transitionStateSupplier.get() == null) {
            return;
        }
        super.update(delta);
    }

    @Override
    protected void processEntity(Entity entity, float delta) {
        entity.setDead(true);
    }

}
