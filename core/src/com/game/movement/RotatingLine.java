package com.game.movement;

import com.badlogic.gdx.math.Polyline;
import com.badlogic.gdx.math.Vector2;
import com.game.utils.interfaces.Updatable;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RotatingLine implements Updatable {

    private final float radius;
    private final float[] vertices = new float[4];
    private final Polyline polyline = new Polyline();

    public float speed;
    public float degrees;

    public RotatingLine(Vector2 origin, float radius, float speed) {
        this(origin, radius, speed, 0f);
    }

    public RotatingLine(Vector2 origin, float radius, float speed, float degrees) {
        this.speed = speed;
        this.radius = radius;
        this.degrees = degrees;
        vertices[0] = origin.x;
        vertices[1] = origin.y;
        vertices[2] = origin.x + radius;
        vertices[3] = origin.y;
        polyline.setVertices(vertices);
        polyline.setOrigin(origin.x, origin.y);
    }

    @Override
    public void update(float delta) {
        degrees += speed * delta;
        if (degrees >= 360f) {
            degrees -= 360f;
        }
        polyline.setRotation(degrees);
    }

    public Vector2 getPosOnLine(float scalar) {
        float x = polyline.getOriginX() + ((getEndPoint().x - polyline.getOriginX()) * scalar);
        float y = polyline.getOriginY() + ((getEndPoint().y - polyline.getOriginY()) * scalar);
        return new Vector2(x, y);
    }

    public Vector2 getEndPoint() {
        float[] tv = polyline.getTransformedVertices();
        return new Vector2(tv[2], tv[3]);
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
