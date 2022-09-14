package com.game.utils;

import com.badlogic.gdx.math.*;
import com.game.shapes.custom.Triangle;
import com.game.utils.objects.Pair;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import static com.badlogic.gdx.math.Intersector.*;
import static com.game.utils.UtilMethods.*;
import static com.game.utils.objects.Pair.pairOf;

/** Util methods for shapes. */
public class ShapeUtils {

    /**
     * Converts the points to an array of vertices.
     *
     * @param points the points
     * @return the array of vertices
     */
    public static float[] pointsToVertices(Vector2... points) {
        float[] v = new float[points.length * 2];
        for (int i = 0; i < points.length; i++) {
            v[i * 2] = points[i].x;
            v[(i * 2) + 1] = points[i].y;
        }
        return v;
    }

    /**
     * Supports overlap detection for {@link Rectangle}, {@link Circle}, and {@link Polyline}.
     *
     * @param s1 the first shape
     * @param s2 the second shape
     * @return if the two shapes overlap
     */
    public static boolean overlap(Shape2D s1, Shape2D s2) {
        Pair<Shape2D> p = new Pair<>();
        if (mask(s1, s2, Rectangle.class)) {
            return Intersector.overlaps((Rectangle) s1, (Rectangle) s2);
        } else if (mask(s1, s2, Circle.class)) {
            return overlaps((Circle) s1, (Circle) s2);
        } else if (mask(s1, s2, Polyline.class)) {
            return overlapLines((Polyline) s1, (Polyline) s2);
        } else if (mask(s1, s2, Rectangle.class, Circle.class, p)) {
            return overlaps((Circle) p.getSecond(), (Rectangle) p.getFirst());
        } else if (mask(s1, s2, Rectangle.class, Polyline.class, p)) {
            Polyline polyline = (Polyline) p.getSecond();
            float[] v = polyline.getTransformedVertices();
            return intersectSegmentRectangle(new Vector2(v[0], v[1]), new Vector2(v[2], v[3]),
                    (Rectangle) p.getFirst());
        } else if (mask(s1, s2, Circle.class, Polyline.class, p)) {
            Pair<Vector2> line = polylineToPointPair((Polyline) p.getSecond());
            return intersectSegmentCircle(line.getFirst(), line.getSecond(), (Circle) p.getFirst(), null);
        }
        return false;
    }

    /**
     * Converts the rectangle into a polygon
     *
     * @param rect the rectangle
     * @return the polygon
     */
    public static Polygon rectToPoly(Rectangle rect) {
        return new Polygon(new float[]{
                rect.x, rect.y,
                rect.x + rect.width, rect.y,
                rect.x + rect.width, rect.y + rect.height,
                rect.x, rect.y + rect.height
        });
    }

    /**
     * See {@link #polylineToPointPair(float[])}.
     *
     * @param polyline the polyline
     * @return the pair of points
     */
    public static Pair<Vector2> polylineToPointPair(Polyline polyline) {
        float[] v = polyline.getTransformedVertices();
        return polylineToPointPair(v);
    }

    /**
     * Converts the polyline to a pair of points. Meant only for lines with two points.
     *
     * @param v the vertex array
     * @return the pair of points
     */
    public static Pair<Vector2> polylineToPointPair(float[] v) {
        Vector2 p1 = new Vector2(v[0], v[1]);
        Vector2 p2 = new Vector2(v[2], v[3]);
        return pairOf(p1, p2);
    }

    /**
     * Converts the polyline to a list of point pairs. Uses the transformed vertices of the polyline.
     * Meant for polylines with more than two points. Use {@link #polylineToPointPair(Polyline)}
     * instead if the line has only two points.
     *
     * @param polyline the polyline
     * @return the list of point pairs
     */
    public static List<Pair<Vector2>> polylineToPointPairs(Polyline polyline) {
        return polylineToPointPairs(polyline.getTransformedVertices());
    }

    /**
     * Converts the vertex array to a list of point pairs.
     *
     * @param v the vertex array
     * @return list of point pairs
     */
    public static List<Pair<Vector2>> polylineToPointPairs(float[] v) {
        List<Pair<Vector2>> pairs = new ArrayList<>();
        int n = v.length;
        for (int i = 0; i <= n - 4; i += 2) {
            float[] c = Arrays.copyOfRange(v, i, i + 4);
            pairs.add(polylineToPointPair(c));
        }
        if (v.length > 4) {
            pairs.add(pairOf(new Vector2(v[0], v[1]), new Vector2(v[n - 2], v[n - 1])));
        }
        return pairs;
    }

    /**
     * Returns a list of lines representing each side of the rectangle.
     *
     * @param rect the rectangle
     * @return the list of lines
     */
    public static List<Pair<Vector2>> rectToLines(Rectangle rect) {
        List<Pair<Vector2>> lines = new ArrayList<>();
        lines.add(pairOf(topLeftPoint(rect), topRightPoint(rect)));
        lines.add(pairOf(new Vector2(rect.x, rect.y), bottomRightPoint(rect)));
        lines.add(pairOf(new Vector2(rect.x, rect.y), topLeftPoint(rect)));
        lines.add(pairOf(bottomRightPoint(rect), topRightPoint(rect)));
        return lines;
    }

    /**
     * See {@link #intersectLineRect(Pair, Rectangle, Collection)}.
     *
     * @param polyline the polyline
     * @param rectangle the rectangle
     * @param interPoints the collection of intersection points
     * @return if the polyline and rectangle intersect
     */
    public static boolean intersectLineRect(Polyline polyline, Rectangle rectangle, Collection<Vector2> interPoints) {
        float[] v = polyline.getTransformedVertices();
        return intersectLineRect(v, rectangle, interPoints);
    }

    /**
     * See {@link #intersectLineRect(Pair, Rectangle, Collection)}. Should be called only when the vertex array
     * describes a line with only two points.
     *
     * @param v the vertex array
     * @param rectangle the rectangle
     * @param interPoints the intersection points
     * @return if the polyline and rectangle intersect
     */
    public static boolean intersectLineRect(float[] v, Rectangle rectangle, Collection<Vector2> interPoints) {
        Pair<Vector2> line = pairOf(new Vector2(v[0], v[1]), new Vector2(v[2], v[3]));
        return intersectLineRect(line, rectangle, interPoints);
    }

    /**
     * Returns if the line and rectangle intersect, and adds each intersection point to the collection.
     *
     * @param line the line
     * @param rectangle the rectangle
     * @param interPoints the collection of intersection points
     * @return if the line and rectangle intersect
     */
    public static boolean intersectLineRect(Pair<Vector2> line, Rectangle rectangle, Collection<Vector2> interPoints) {
        List<Pair<Vector2>> rectToLines = rectToLines(rectangle);
        boolean isIntersection = false;
        for (Pair<Vector2> l : rectToLines) {
            Vector2 intersection = new Vector2();
            if (intersectLines(l, line, intersection)) {
                isIntersection = true;
                interPoints.add(intersection);
            }
        }
        return isIntersection;
    }

    /**
     * Returns if the two lines overlap.
     *
     * @param line1 the first line
     * @param line2 the second line
     * @return if the two lines overlap
     */
    public static boolean overlapLines(Polyline line1, Polyline line2) {
        return intersectLines(line1, line2, new Vector2());
    }

    /**
     * Returns if the two lines intersect, and sets the intersection if so.
     *
     * @param line1 the first line
     * @param line2 the second line
     * @param intersection the intersection point, setVertices if the method returns true
     * @return if the two lines intersect
     */
    public static boolean intersectLines(Polyline line1, Polyline line2, Vector2 intersection) {
        return intersectLines(polylineToPointPair(line1), polylineToPointPair(line2), intersection);
    }

    /**
     * Returns the result pairOf {@link Intersector#intersectSegments(Vector2, Vector2, Vector2, Vector2, Vector2)}.
     *
     * @param l1 the first point
     * @param l2 the second point
     * @param intersection the intersection point
     * @return if the two lines intersect
     */
    public static boolean intersectLines(Pair<Vector2> l1, Pair<Vector2> l2, Vector2 intersection) {
        return intersectSegments(l1.getFirst(), l1.getSecond(), l2.getFirst(), l2.getSecond(), intersection);
    }

    /**
     * Masks the two shapes.
     *
     * @param s1 the first shape
     * @param s2 the second shape
     * @param c1 the first mask
     * @param c2 the second mask
     * @param p the pair
     * @return if the mask is successful
     */
    public static boolean mask(Shape2D s1, Shape2D s2, Class<? extends Shape2D> c1, Class<? extends Shape2D> c2,
                               Pair<Shape2D> p) {
        if (c1.isAssignableFrom(s1.getClass()) && c2.isAssignableFrom(s2.getClass())) {
            p.set(s1, s2);
            return true;
        } else if (c1.isAssignableFrom(s2.getClass()) && c2.isAssignableFrom(s1.getClass())) {
            p.set(s2, s1);
            return true;
        }
        return false;
    }

    /**
     * Masks the two shapes.
     *
     * @param s1 the first shape
     * @param s2 the second shape
     * @param c the mask
     * @return if the mask is successful
     */
    public static boolean mask(Shape2D s1, Shape2D s2, Class<? extends Shape2D> c) {
        return c.isAssignableFrom(s1.getClass()) && c.isAssignableFrom(s2.getClass());
    }

}
