package com.game.trajectories;

import com.game.core.Component;
import com.game.entities.Entity;
import com.game.core.System;
import com.game.utils.Trajectory;
import com.game.world.BodyComponent;

import java.util.Set;

public class TrajectorySystem extends System {

    @Override
    public Set<Class<? extends Component>> getComponentMask() {
        return Set.of(TrajectoryComponent.class, BodyComponent.class);
    }

    @Override
    protected void processEntity(Entity entity, float delta) {
        TrajectoryComponent trajectoryComponent = entity.getComponent(TrajectoryComponent.class);
        BodyComponent bodyComponent = entity.getComponent(BodyComponent.class);
        Trajectory trajectory = trajectoryComponent.getCurrentTrajectory();
        if (trajectory.isAtBeginning()) {
            trajectory.reset();
            trajectory.init(bodyComponent.getPosition());
        }
        trajectory.update(delta);
        bodyComponent.setPosition(trajectory.getPosition());
        if (trajectory.isFinished()) {
            trajectoryComponent.setToNext();
        }
    }

}
