package com.game.movement;

import com.badlogic.gdx.math.Vector2;
import com.game.Component;
import com.game.utils.objects.KeyValuePair;
import com.game.utils.objects.Timer;

import java.util.ArrayList;
import java.util.List;

import static com.game.ViewVals.PPM;
import static com.game.movement.TrajectoryParser.parse;
import static com.game.utils.UtilMethods.interpolate;

/**
 * {@link Component} implementation for trajectories.
 */
public class TrajectoryComponent extends Component {

    private final List<KeyValuePair<Vector2, Timer>> destinations = new ArrayList<>();

    private int index;

    public TrajectoryComponent(String trajectory, Vector2 startCenterPoint) {
        this(parse(trajectory, PPM), startCenterPoint);
    }

    public TrajectoryComponent(List<KeyValuePair<Vector2, Float>> trajectories, Vector2 startCenterPoint) {
        Vector2 temp = new Vector2(startCenterPoint);
        trajectories.forEach(t -> {
            Vector2 dest = new Vector2(temp).add(t.key());
            destinations.add(KeyValuePair.of(dest, new Timer(t.value())));
            temp.set(dest);
        });
    }

    public Vector2 getPos(float delta) {
        Timer timer = getCurrentTimer();
        timer.update(delta);
        Vector2 pos = interpolate(getPrevDest(), getCurrentDest(), timer.getRatio());
        if (timer.isFinished()) {
            timer.reset();
            index++;
            if (index >= destinations.size()) {
                index = 0;
            }
        }
        return pos;
    }

    private Vector2 getPrevDest() {
        int t = (index == 0 ? destinations.size() : index) - 1;
        return destinations.get(t).key();
    }

    private Vector2 getCurrentDest() {
        return destinations.get(index).key();
    }

    private Timer getCurrentTimer() {
        return destinations.get(index).value();
    }

}
