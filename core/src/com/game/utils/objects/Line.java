package com.game.utils.objects;

import com.badlogic.gdx.math.*;
import com.game.utils.UtilMethods;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Based on implementation of {@link com.badlogic.gdx.math.Polyline}.
 * <a href="https://github.com/libgdx/libgdx/blob/master/gdx/src/com/badlogic/gdx/math/Polyline.java">Polyline</a>
 */
@Getter
@Setter
@NoArgsConstructor
public class Line implements Shape2D {

    @Getter(AccessLevel.NONE)
    private final Pair<Vector2> localPoints = new Pair<>(Vector2.Zero, Vector2.Zero);
    @Getter(AccessLevel.NONE)
    private final Pair<Vector2> worldPoints = new Pair<>(Vector2.Zero, Vector2.Zero);

    private final Vector2 pos = new Vector2();
    private final Vector2 origin = new Vector2();

    private float rotation;

    public Line(float x1, float y1, float x2, float y2) {
        this(new Vector2(x1, y1), new Vector2(x2, y2));
    }

    public Line(Vector2 p1, Vector2 p2) {
        setPoints(p1, p2);
    }

    public void setPoints(Vector2 p1, Vector2 p2) {
        localPoints.set(p1, p2);
        worldPoints.set(p1, p2);
    }

    public Pair<Vector2> getWorldPoints() {
        float cos = MathUtils.cosDeg(rotation);
        float sin = MathUtils.sinDeg(rotation);
        setNewWorldPoint(localPoints.getFirst().cpy(), worldPoints.getFirst(), cos, sin);
        setNewWorldPoint(localPoints.getSecond().cpy(), worldPoints.getSecond(), cos, sin);
        return worldPoints;
    }

    private void setNewWorldPoint(Vector2 localPoint, Vector2 worldPoint, float cos, float sin) {
        float x = localPoint.x - origin.x;
        float y = localPoint.y - origin.y;
        if (rotation != 0) {
            float oldX = x;
            x = cos * x - sin * y;
            y = sin * oldX + cos * y;
        }
        worldPoint.x = pos.x + x + origin.x;
        worldPoint.y = pos.y + y + origin.y;
    }

    public float getLength() {
        Vector2 p1 = worldPoints.getFirst();
        Vector2 p2 = worldPoints.getSecond();
        return Vector2.dst(p1.x, p1.y, p2.x, p2.y);
    }

    public Vector2 getCenter() {
        Vector2 p1 = worldPoints.getFirst();
        Vector2 p2 = worldPoints.getSecond();
        float x = (p1.x + p2.x) / 2f;
        float y = (p1.y + p2.y) / 2f;
        return new Vector2(x, y);
    }

    public void setCenter(Vector2 center) {

        // TODO: Set center

    }

    public void setPosition(float x, float y) {
        pos.set(x, y);
    }

    public void setOrigin(float x, float y) {
        origin.set(x, y);
    }

    public void rotate(float degrees) {
        rotation += degrees;
    }

    public void translate(float x, float y) {
        pos.add(x, y);
    }

    public boolean overlaps(Line line) {
        return intersects(line, new Vector2());
    }

    public boolean intersects(Line line, Vector2 intersection) {
        return Intersector.intersectLines(worldPoints.getFirst(), worldPoints.getSecond(),
                line.getWorldPoints().getFirst(), line.getWorldPoints().getSecond(), intersection);
    }

    public boolean overlaps(Rectangle rect) {
        return Intersector.intersectLinePolygon(worldPoints.getFirst(), worldPoints.getSecond(),
                UtilMethods.rectToPoly(rect));
    }

    public boolean overlaps(Circle circle) {
        return Intersector.intersectSegmentCircle(worldPoints.getFirst(), worldPoints.getSecond(), circle, null);
    }

    @Override
    public boolean contains(Vector2 point) {
        return Intersector.pointLineSide(worldPoints.getFirst(), worldPoints.getSecond(), point) == 0;
    }

    @Override
    public boolean contains(float x, float y) {
        return contains(new Vector2(x, y));
    }

}
