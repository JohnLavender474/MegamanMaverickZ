package com.game.utils;

import com.badlogic.gdx.math.Vector2;
import com.game.updatables.Updatable;
import lombok.Getter;

@Getter
public class Trajectory implements Initializable<Vector2>, Updatable, Resettable {

    private final Timer timer;
    private final Vector2 trajectory;
    private final Vector2 initPos = new Vector2();

    public Trajectory(float x, float y, float duration) {
        this(new Vector2(x, y), duration);
    }

    public Trajectory(Vector2 trajectory, float duration) {
        this.trajectory = trajectory;
        this.timer = new Timer(duration);
    }

    @Override
    public void init(Vector2 initPos) {
        this.initPos.set(initPos);
    }

    @Override
    public void update(float delta) {
        timer.update(delta);
    }

    public Vector2 getPosition() {
        return UtilMethods.interpolate(initPos, initPos.cpy().add(trajectory), timer.getRatio());
    }

    public boolean isAtBeginning() {
        return timer.isAtBeginning();
    }

    public boolean isFinished() {
        return timer.isFinished();
    }

    @Override
    public void reset() {
        timer.reset();
    }

}
