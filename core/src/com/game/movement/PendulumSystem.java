package com.game.movement;

import com.game.entities.Entity;
import com.game.System;
import com.game.utils.interfaces.UpdatableConsumer;
import com.game.utils.objects.Pendulum;

public class PendulumSystem extends System {

    public PendulumSystem() {
        super(PendulumComponent.class);
    }

    @Override
    protected void processEntity(Entity entity, float delta) {
        PendulumComponent pendulumComponent = entity.getComponent(PendulumComponent.class);
        Pendulum pendulum = pendulumComponent.getPendulum();
        if (pendulum == null) {
            return;
        }
        pendulum.update(delta);
        UpdatableConsumer<Pendulum> updatableConsumer = pendulumComponent.getUpdatableConsumer();
        if (updatableConsumer == null) {
            return;
        }
        updatableConsumer.consumeAndUpdate(pendulum, delta);
    }

}
