package com.game.utils;

import com.badlogic.gdx.math.*;
import com.game.utils.objects.Pair;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static com.badlogic.gdx.math.Intersector.*;
import static com.game.utils.UtilMethods.*;

public class ShapeUtils {

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

    public static Pair<Vector2> polylineToPointPair(Polyline polyline) {
        float[] lv = polyline.getTransformedVertices();
        Vector2 lp1 = new Vector2(lv[0], lv[1]);
        Vector2 lp2 = new Vector2(lv[2], lv[3]);
        return Pair.of(lp1, lp2);
    }

    public static boolean lineOverlapsRectangle(Rectangle rect, Pair<Vector2> line) {
        return lineOverlapsRectangle(rect, line.getFirst(), line.getSecond());
    }

    public static boolean lineOverlapsRectangle(Rectangle rect, Vector2 linePoint1, Vector2 linePoint2) {
        return lineOverlapsRectangle(rect, linePoint1, linePoint2, new Vector2());
    }

    public static boolean lineOverlapsRectangle(Rectangle rect, Vector2 linePoint1, Vector2 linePoint2, Vector2 inter) {
        return rectToLines(rect).stream().anyMatch(l -> Intersector.intersectLines(
                l.getFirst(), l.getSecond(), linePoint1, linePoint2, inter));
    }

    public static List<Pair<Vector2>> rectToLines(Rectangle rect) {
        List<Pair<Vector2>> lines = new ArrayList<>();
        lines.add(Pair.of(topLeftPoint(rect), topRightPoint(rect)));
        lines.add(Pair.of(new Vector2(rect.x, rect.y), bottomRightPoint(rect)));
        lines.add(Pair.of(new Vector2(rect.x, rect.y), topLeftPoint(rect)));
        lines.add(Pair.of(bottomRightPoint(rect), topRightPoint(rect)));
        return lines;
    }

    public static boolean intersectLineRect(Polyline polyline, Rectangle rectangle, Collection<Vector2> interPoints) {
        float[] v = polyline.getTransformedVertices();
        Pair<Vector2> line = new Pair<>(new Vector2(v[0], v[1]), new Vector2(v[2], v[3]));
        return intersectLineRect(line, rectangle, interPoints);
    }

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

    public static boolean overlapLines(Polyline line1, Polyline line2) {
        return intersectLines(line1, line2, new Vector2());
    }

    public static boolean intersectLines(Polyline line1, Polyline line2, Vector2 intersection) {
        return intersectLines(polylineToPointPair(line1), polylineToPointPair(line2), intersection);
    }

    /**
     * Returns the result of {@link Intersector#intersectSegments(Vector2, Vector2, Vector2, Vector2, Vector2)}.
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
