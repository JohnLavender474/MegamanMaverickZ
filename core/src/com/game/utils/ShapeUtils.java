package com.game.utils;

import com.badlogic.gdx.math.*;
import com.game.utils.objects.Line;
import com.game.utils.objects.Pair;

public class ShapeUtils {

    /**
     * Supports centering for {@link Rectangle}, {@link Circle}, and {@link Line}.
     *
     * @param shape2D the shape
     * @param center the center
     */
    public static void setCenter(Shape2D shape2D, Vector2 center) {
        if (shape2D instanceof Rectangle rectangle) {
            rectangle.setCenter(center);
        } else if (shape2D instanceof Circle circle) {
            circle.setPosition(center);
        } else if (shape2D instanceof Line line) {

            // TODO: Center line

        }
    }

    /**
     * Supports overlap detection for {@link Rectangle}, {@link Circle}, and {@link Line}.
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
            return Intersector.overlaps((Circle) s1, (Circle) s2);
        } else if (mask(s1, s2, Line.class)) {
            return ((Line) s1).overlaps((Line) s2);
        } else if (mask(s1, s2, Rectangle.class, Circle.class, p)) {
            return Intersector.overlaps((Circle) p.getSecond(), (Rectangle) p.getFirst());
        } else if (mask(s1, s2, Rectangle.class, Line.class, p)) {
            return ((Line) p.getSecond()).overlaps((Rectangle) p.getFirst());
        } else if (mask(s1, s2, Circle.class, Line.class, p)) {
            return ((Line) p.getSecond()).overlaps((Circle) p.getFirst());
        }
        return false;
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
