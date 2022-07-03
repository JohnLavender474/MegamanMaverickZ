package com.game.trajectories;

import com.game.Component;
import com.game.core.IEntity;
import com.game.System;
import com.game.world.BodyComponent;

import java.util.Set;

public class TrajectorySystem extends System {

    @Override
    public Set<Class<? extends Component>> getComponentMask() {
        return Set.of(TrajectoryComponent.class, BodyComponent.class);
    }

    @Override
    protected void processEntity(IEntity entity, float delta) {
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
