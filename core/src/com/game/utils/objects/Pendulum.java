package com.game.utils.objects;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.game.updatables.Updatable;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import static com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType.*;
import static com.game.core.ConstVals.ViewVals.PPM;
import static java.lang.Math.*;

/**
 * Resource: <a href="https://www.javacodex.com/More-Examples/2/13">...</a>
 */
@Getter
@RequiredArgsConstructor
public class Pendulum implements Updatable {

    private final float length;
    private final float gravity;
    private final Vector2 anchor;
    private final Vector2 end = new Vector2();

    private float angle = (float) PI / 2f;
    private float angleAccel;
    private float angleVel;

    @Setter
    private float scalar = 1f;

    public Pendulum(float length, float gravity, float anchorX, float anchorY, float scalar) {
        this(length, gravity, new Vector2(anchorX, anchorY), scalar);
    }

    public Pendulum(float length, float gravity, Vector2 anchor, float scalar) {
        this(length, gravity, anchor);
        this.scalar = scalar;
    }

    public Pendulum(float length, float gravity, float anchorX, float anchorY) {
        this(length, gravity, new Vector2(anchorX, anchorY));
    }

    @Override
    public void update(float delta) {
        angleAccel = (float) (gravity / length * sin(angle));
        angleVel += angleAccel * delta * scalar;
        angle += angleVel * delta * scalar;
        end.x = (float) (anchor.x + (sin(angle) * length));
        end.y = (float) (anchor.y + (cos(angle) * length));
    }

    public void debug(ShapeRenderer shapeRenderer, Color color) {
        debug(shapeRenderer, color, color);
    }

    public void debug(ShapeRenderer shapeRenderer, Color lineColor, Color ballColor) {
        boolean isDrawing = shapeRenderer.isDrawing();
        if (!isDrawing) {
            shapeRenderer.begin(Line);
        }
        shapeRenderer.setColor(lineColor);
        shapeRenderer.line(anchor, end);
        shapeRenderer.setColor(ballColor);
        shapeRenderer.circle(end.x, end.y, PPM / 2f);
        if (!isDrawing) {
            shapeRenderer.end();
        }
    }

}
