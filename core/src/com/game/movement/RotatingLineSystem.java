package com.game.movement;

import com.game.entities.Entity;
import com.game.System;
import com.game.utils.interfaces.UpdatableConsumer;
import com.game.utils.objects.RotatingLine;

public class RotatingLineSystem extends System {

    public RotatingLineSystem() {
        super(RotatingLineComponent.class);
    }

    @Override
    protected void processEntity(Entity entity, float delta) {
        RotatingLineComponent rotatingLineComponent = entity.getComponent(RotatingLineComponent.class);
        RotatingLine rotatingLine = rotatingLineComponent.getRotatingLine();
        if (rotatingLine == null) {
            return;
        }
        rotatingLine.update(delta);
        UpdatableConsumer<RotatingLine> updatableConsumer = rotatingLineComponent.getUpdatableConsumer();
        if (updatableConsumer == null) {
            return;
        }
        updatableConsumer.consumeAndUpdate(rotatingLine, delta);
    }

}
