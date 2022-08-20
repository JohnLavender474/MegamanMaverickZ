package com.game.movement;

import com.game.core.Entity;
import com.game.core.System;
import com.game.world.BodyComponent;

public class TrajectorySystem extends System {

    public TrajectorySystem() {
        super(TrajectoryComponent.class, BodyComponent.class);
    }

    @Override
    protected void processEntity(Entity entity, float delta) {
        TrajectoryComponent trajectoryComponent = entity.getComponent(TrajectoryComponent.class);
        BodyComponent bodyComponent = entity.getComponent(BodyComponent.class);
        bodyComponent.setVelocity(trajectoryComponent.getCurrentTrajectory());
        trajectoryComponent.getCurrentTimer().update(delta);
        if (trajectoryComponent.getCurrentTimer().isFinished()) {
            trajectoryComponent.getCurrentTimer().reset();
            trajectoryComponent.setToNext();
        }
    }

}
