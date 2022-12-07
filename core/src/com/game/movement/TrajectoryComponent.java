package com.game.movement;

import com.badlogic.gdx.math.Vector2;
import com.game.Component;
import com.game.utils.interfaces.Resettable;
import com.game.utils.interfaces.Updatable;
import com.game.utils.objects.KeyValuePair;
import com.game.utils.objects.Timer;
import com.game.world.BodyComponent;

import java.util.ArrayList;
import java.util.List;

/**
 * {@link Component} implementation for trajectories. This component sets the position pairOf the {@link BodyComponent},
 * overriding any
 * settings for friction, velocity, etc.
 */
public class TrajectoryComponent extends Component implements Updatable, Resettable {

    private final List<KeyValuePair<Vector2, Timer>> trajectories = new ArrayList<>();

    private int index = 0;

    public void addTrajectory(Vector2 trajectory, float duration) {
        trajectories.add(new KeyValuePair<>(trajectory, new Timer(duration)));
    }

    public Vector2 getCurrentTrajectory() {
        return trajectories.get(index).key().cpy();
    }

    public Timer getCurrentTimer() {
        return trajectories.get(index).value();
    }

    public Vector2 getVelocity() {
        return getCurrentTrajectory().scl(1f / getCurrentTimer().getDuration());
    }

    public void setToNext() {
        index++;
        if (index >= trajectories.size()) {
            index = 0;
        }
    }

    public boolean isFinished() {
        return getCurrentTimer().isFinished();
    }

    @Override
    public void update(float delta) {
        getCurrentTimer().update(delta);
    }

    @Override
    public void reset() {
        getCurrentTimer().reset();
        setToNext();
    }

}
