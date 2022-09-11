package com.game.screens.menus.utils;

import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.math.Vector3;
import com.game.utils.interfaces.Updatable;
import com.game.utils.interfaces.Initializable;
import com.game.utils.objects.Timer;
import lombok.Setter;

@Setter
public class ScreenSlide implements Initializable, Updatable {

    private final Timer timer;
    private final Camera camera;

    private Vector3 trajectory;
    private Vector3 startPoint;
    private Vector3 endPoint;

    public ScreenSlide(Camera camera, Vector3 trajectory, Vector3 startPoint, Vector3 endPoint, float duration,
                       boolean setToEnd) {
        this(camera, trajectory, startPoint, endPoint, duration);
        if (setToEnd) {
            setToEnd();
        }
    }

    public ScreenSlide(Camera camera, Vector3 trajectory, Vector3 startPoint, Vector3 endPoint, float duration) {
        this.camera = camera;
        this.timer = new Timer(duration);
        this.trajectory = trajectory;
        this.startPoint = startPoint;
        this.endPoint = endPoint;
    }

    public boolean isFinished() {
        return timer.isFinished();
    }

    public boolean isJustFinished() {
        return timer.isJustFinished();
    }

    public void setToEnd() {
        timer.setToEnd();
    }

    public void reverse() {
        Vector3 temp = endPoint;
        endPoint = startPoint;
        startPoint = temp;
        trajectory.scl(-1f);
    }
    
    @Override
    public void init() {
        camera.position.x = startPoint.x;
        camera.position.y = startPoint.y;
        timer.reset();
    }

    @Override
    public void update(float delta) {
        timer.update(delta);
        if (timer.isFinished()) {
            return;
        }
        camera.position.x += trajectory.x * delta * (1f / timer.getDuration());
        camera.position.y += trajectory.y * delta * (1f / timer.getDuration());
        if (timer.isFinished()) {
            camera.position.x = endPoint.x;
            camera.position.y = endPoint.y;
        }
    }

}
