package com.game.movement;

import com.badlogic.gdx.math.Polyline;
import com.badlogic.gdx.math.Vector2;
import com.game.updatables.Updatable;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RotatingLine implements Updatable {

    private final Polyline polyline = new Polyline();
    private final float[] vertices = new float[4];
    private final float[] debugColorVals = new float[4];
    private final float radius;

    public float speed;
    public float degrees;

    public RotatingLine(Vector2 origin, float radius, float speed) {
        this.speed = speed;
        this.radius = radius;
        vertices[0] = origin.x;
        vertices[1] = origin.y;
        vertices[2] = origin.x;
        vertices[3] = origin.y + radius;
        polyline.setVertices(vertices);
        polyline.setOrigin(origin.x, origin.y);
    }

    @Override
    public void update(float delta) {
        degrees += speed * delta;
        polyline.setRotation(degrees);
    }

    public Vector2 getPosOnLine(float scalar) {
        float x = polyline.getOriginX() + ((getEndPoint().x - polyline.getOriginX()) * scalar);
        float y = polyline.getOriginY() + ((getEndPoint().y - polyline.getOriginY()) * scalar);
        return new Vector2(x, y);
    }

    public void setDebugColorVals(float r, float g, float b, float a) {
        debugColorVals[0] = r;
        debugColorVals[1] = g;
        debugColorVals[2] = b;
        debugColorVals[3] = a;
    }

    public Vector2 getEndPoint() {
        return new Vector2(vertices[2], vertices[3]);
    }

    public void translate(float x, float y) {
        polyline.setOrigin(polyline.getOriginX() + x, polyline.getOriginY() + y);
    }

    public Vector2 getPos() {
        return new Vector2(polyline.getOriginX(), polyline.getOriginY());
    }

    public void setPos(Vector2 pos) {
        polyline.setOrigin(pos.x, pos.y);
    }

}
