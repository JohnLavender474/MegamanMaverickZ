package com.game.movement;

import com.badlogic.gdx.math.Vector2;
import com.game.core.Component;
import com.game.utils.objects.KeyValuePair;
import com.game.utils.objects.Timer;
import com.game.world.BodyComponent;

import java.util.ArrayList;
import java.util.List;

/**
 * {@link Component} implementation for trajectories. This component sets the position of the {@link BodyComponent},
 * overriding any
 * settings for friction, velocity, etc.
 */
public class TrajectoryComponent extends Component {

    private final List<KeyValuePair<Vector2, Timer>> trajectories = new ArrayList<>();

    private int index = 0;

    public void addTrajectory(Vector2 trajectory, float duration) {
        trajectories.add(new KeyValuePair<>(trajectory, new Timer(duration)));
    }

    public Vector2 getCurrentTrajectory() {
        return trajectories.get(index).key();
    }

    public Timer getCurrentTimer() {
        return trajectories.get(index).value();
    }

    public void setToNext() {
        index++;
        if (index >= trajectories.size()) {
            index = 0;
        }
    }

}
