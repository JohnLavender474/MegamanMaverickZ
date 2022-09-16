package com.game.shapes.custom;

import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.*;
import com.game.utils.ShapeUtils;
import com.game.utils.UtilMethods;
import com.game.utils.objects.Pair;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

import static com.game.utils.ShapeUtils.*;
import static com.game.utils.UtilMethods.*;

@Getter
@NoArgsConstructor
public class Triangle implements Shape2D {

    private final Polygon polygon = new Polygon();

    public Triangle(Polyline polyline) {
        this(polyline.getTransformedVertices());
    }

    public Triangle(Polygon polygon) {
        this(polygon.getTransformedVertices());
    }

    public Triangle(float[] v) {
        this(v[0], v[1], v[2], v[3], v[4], v[5]);
    }

    public Triangle(float x1, float y1, float x2, float y2, float x3, float y3) {
        this(new Vector2(x1, y1), new Vector2(x2, y2), new Vector2(x3, y3));
    }

    public Triangle(Vector2 p1, Vector2 p2, Vector2 p3) {
        setVertices(p1, p2, p3);
    }

    public void setVertices(Vector2 p1, Vector2 p2, Vector2 p3) {
        polygon.setVertices(pointsToVertices(p1, p2, p3));
        Vector2 center = getBoundsCenter();
        polygon.setOrigin(center.x, center.y);
    }

    public void rotate(float degrees) {
        polygon.rotate(degrees);
    }

    public void setRotation(float rotation) {
        polygon.setRotation(rotation);
    }

    public List<Vector2> getTransformedPoints() {
        float[] v = getTransformedVertices();
        List<Vector2> p = new ArrayList<>();
        for (int i = 0; i < v.length; i += 2) {
            p.add(new Vector2(v[i], v[i + 1]));
        }
        return p;
    }

    public float[] getTransformedVertices() {
        return polygon.getTransformedVertices();
    }

    public Vector2 getOrigin() {
        return new Vector2(polygon.getOriginX(), polygon.getOriginY());
    }

    public void setOrigin(Vector2 origin) {
        setOrigin(origin.x, origin.y);
    }

    public void setOrigin(float x, float y) {
        polygon.setOrigin(x, y);
    }

    public Vector2 getPosition() {
        return new Vector2(polygon.getX(), polygon.getY());
    }

    public void setPosition(Vector2 position) {
        setPosition(position.x, position.y);
    }

    public void setPosition(float x, float y) {
        polygon.setPosition(x, y);
    }

    public void scale(float scale) {
        polygon.scale(scale);
    }

    public void setScale(Vector2 scale) {
        polygon.setScale(scale.x, scale.y);
    }

    public float area() {
        return polygon.area();
    }

    public Rectangle getBoundingRectangle() {
        return polygon.getBoundingRectangle();
    }

    public Vector2 getBoundsCenter() {
        return centerPoint(getBoundingRectangle());
    }

    public Vector2 getCentroid() {
        return polygon.getCentroid(new Vector2());
    }

    public void renderLines(ShapeRenderer shapeRenderer) {
        List<Vector2> p = getTransformedPoints();
        for (int i = 0; i < p.size() - 1; i++) {
            shapeRenderer.line(p.get(i), p.get(i + 1));
        }
        shapeRenderer.line(p.get(p.size() - 1), p.get(0));
    }

    public void renderRectLines(ShapeRenderer shapeRenderer, float thickness) {
        List<Vector2> p = getTransformedPoints();
        for (int i = 0; i < p.size() - 1; i++) {
            shapeRenderer.rectLine(p.get(i), p.get(i + 1), thickness);
        }
        shapeRenderer.rectLine(p.get(p.size() - 1), p.get(0), thickness);
    }

    /**
     * Checks if the polyline overlaps this triangle. Supports only lines with two points.
     *
     * @param polyline the polyline
     * @return if the polyline overlaps this triangle
     */
    public boolean overlaps(Polyline polyline) {
        Pair<Vector2> p = polylineToPointPair(polyline);
        return Intersector.intersectLinePolygon(p.getFirst(), p.getSecond(), getPolygon());
    }

    public boolean overlaps(Polygon polygon) {
        return Intersector.overlapConvexPolygons(getPolygon(), polygon);
    }

    public boolean overlaps(Triangle triangle) {
        return overlaps(triangle.getPolygon());
    }

    public boolean overlaps(Rectangle rectangle) {
        return overlaps(rectToPoly(rectangle));
    }

    public boolean overlaps(Circle circle) {

        Vector2 circleCenter = new Vector2(circle.x, circle.y);

        // Intersector.distanceSegmentPoint()
        return false;
    }

    public boolean intersects(Polygon polygon, Polygon intersection) {
        return Intersector.intersectPolygons(getPolygon(), polygon, intersection);
    }

    @Override
    public boolean contains(Vector2 point) {
        return polygon.contains(point);
    }

    @Override
    public boolean contains(float x, float y) {
        return contains(new Vector2(x, y));
    }

}
