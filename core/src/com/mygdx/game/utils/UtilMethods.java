package com.mygdx.game.utils;

import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

/**
 * Global utility methods.
 */
public class UtilMethods {

    /**
     * Returns String of long dotted line.
     *
     * @return long dotted line.
     */
    public static String longDottedLine() {
        return "---------------------------------------------------------";
    }

    /**
     * Fetches {@link Class#getSimpleName()} of the provided Object.
     *
     * @param o the Object
     * @return the simple name of the Object
     */
    public static String objName(Object o) {
        return o.getClass().getSimpleName();
    }

    /**
     * Returns the interpolations of the x and y values of the two {@link Vector2} instances.
     * See {@link #interpolate(float, float, float)}.
     *
     * @param start the starting coordinates
     * @param target the target coordinates
     * @param delta the delta time
     * @return the interpolation
     */
    public static Vector2 interpolate(Vector2 start, Vector2 target, float delta) {
        Vector2 interPos = new Vector2();
        interPos.x = interpolate(start.x, target.x, delta);
        interPos.y = interpolate(start.y, target.y, delta);
        return interPos;
    }

    /**
     * Returns the interpolated value from start to target.
     *
     * @param start the starting value
     * @param target the target value
     * @param delta the delta time
     * @return the interpolation
     */
    public static float interpolate(float start, float target, float delta) {
        return start - (start - target) * delta;
    }

    /**
     * Returns the direction in which the first {@link Rectangle} should be pushed in order to resolve it overlapping
     * the second Rectangle. If there is no overlap, then return null.
     *
     * @param toBePushed the Rectangle to be pushed
     * @param other the other Rectangle
     * @param overlap the overlap of the two Rectangles
     * @return null if there is no overlap, otherwise the direction in which the first Rectangle should be pushed
     */
    public static Direction getOverlapPushDirection(Rectangle toBePushed, Rectangle other, Rectangle overlap) {
        Intersector.intersectRectangles(toBePushed, other, overlap);
        if (overlap.width == 0 && overlap.height == 0) {
            return null;
        }
        if (overlap.width > overlap.height) {
            return toBePushed.y > other.y ? Direction.UP : Direction.DOWN;
        } else {
            return toBePushed.x > other.x ? Direction.RIGHT : Direction.LEFT;
        }
    }

    /**
     * Positions the first {@link Rectangle} onto the other in relation to the provided {@link Position} value.
     * The other Rectangle, named staticRectangle, is not moved. For example, if position equals
     * {@link Position#BOTTOM_CENTER}, then the Rectangle to be moved will be positioned so that its bottom center
     * point will rest directly on the bottom center point of the static Rectangle. Second example: if position equals
     * {@link Position#CENTER_LEFT}, then the Rectangle to be moved will be positioned so that its center left point
     * will rest directly on the center left point of the static Rectangle. So on and so forth.
     *
     * @param toBeMoved  the Rectangle to be moved onto the other
     * @param staticRect the Rectangle that is not moved and acts as a reference for the first Rectangle
     * @param position   the Position on which to place the first Rectangle
     */
    public static void positionRectOntoOther(Rectangle toBeMoved, Rectangle staticRect, Position position) {
        switch (position) {
            case TOP_LEFT ->
                    setTopLeftToPoint(toBeMoved, topLeftPoint(staticRect));
            case TOP_CENTER ->
                    setTopCenterToPoint(toBeMoved, topCenterPoint(staticRect));
            case TOP_RIGHT ->
                    setTopRightToPoint(toBeMoved, topRightPoint(staticRect));
            case CENTER_LEFT ->
                    setCenterLeftToPoint(toBeMoved, centerLeftPoint(staticRect));
            case CENTER ->
                    toBeMoved.setCenter(centerPoint(staticRect));
            case CENTER_RIGHT ->
                    setCenterRightToPoint(toBeMoved, centerRightPoint(staticRect));
            case BOTTOM_LEFT ->
                    toBeMoved.setPosition(staticRect.x, staticRect.y);
            case BOTTOM_CENTER ->
                    setBottomCenterToPoint(toBeMoved, bottomCenterPoint(staticRect));
            case BOTTOM_RIGHT ->
                    setBottomRightToPoint(toBeMoved, bottomRightPoint(staticRect));
        }
    }

    /**
     * Returns the bottom-right point of the {@link Rectangle} as a {@link Vector2}.
     *
     * @param rectangle the rectangle
     * @return the bottom-right point
     */
    public static Vector2 bottomRightPoint(Rectangle rectangle) {
        return new Vector2(rectangle.x + rectangle.width, rectangle.y);
    }

    /**
     * Sets the {@link Rectangle} such that its bottom-right point rests directly on the {@link Vector2}.
     *
     * @param rectangle        the rectangle
     * @param bottomRightPoint the bottom-right point
     */
    public static void setBottomRightToPoint(Rectangle rectangle, Vector2 bottomRightPoint) {
        rectangle.setPosition(bottomRightPoint.x - rectangle.width, bottomRightPoint.y);
    }

    /**
     * Returns the bottom-center point of the {@link Rectangle} as a {@link Vector2}.
     *
     * @param rectangle the rectangle
     * @return the bottom-center point
     */
    public static Vector2 bottomCenterPoint(Rectangle rectangle) {
        return new Vector2(rectangle.x + (rectangle.width / 2f), rectangle.y);
    }

    /**
     * Sets the {@link Rectangle} such that its bottom-center point rests directly on the {@link Vector2}.
     *
     * @param rectangle         the rectangle
     * @param bottomCenterPoint the bottom-center point
     */
    public static void setBottomCenterToPoint(Rectangle rectangle, Vector2 bottomCenterPoint) {
        rectangle.setPosition(bottomCenterPoint.x - (rectangle.width / 2f), bottomCenterPoint.y);
    }

    /**
     * Returns the center-right point of the {@link Rectangle} as a {@link Vector2}.
     *
     * @param rectangle the rectangle
     * @return the center-right point
     */
    public static Vector2 centerRightPoint(Rectangle rectangle) {
        return new Vector2(rectangle.x + rectangle.width, rectangle.y + (rectangle.height / 2f));
    }

    /**
     * Sets the {@link Rectangle} such that its center-right point resets directly on the {@link Vector2}.
     *
     * @param rectangle        the rectangle
     * @param centerRightPoint the center-right point
     */
    public static void setCenterRightToPoint(Rectangle rectangle, Vector2 centerRightPoint) {
        rectangle.setPosition(centerRightPoint.x - rectangle.width, centerRightPoint.y - (rectangle.height / 2f));
    }

    /**
     * Returns the center point of the {@link Rectangle}.
     *
     * @param rectangle the rectangle
     * @return the center point
     */
    public static Vector2 centerPoint(Rectangle rectangle) {
        Vector2 center = new Vector2();
        rectangle.getCenter(center);
        return center;
    }

    /**
     * Returns the center-left point of the {@link Rectangle} as a {@link Vector2}.
     *
     * @param rectangle the rectangle
     * @return the center-left point
     */
    public static Vector2 centerLeftPoint(Rectangle rectangle) {
        return new Vector2(rectangle.x, rectangle.y + (rectangle.height / 2f));
    }

    /**
     * Sets the {@link Rectangle} such that its center-left point rests directly on the {@link Vector2}.
     *
     * @param rectangle       the rectangle
     * @param centerLeftPoint the center-left point
     */
    public static void setCenterLeftToPoint(Rectangle rectangle, Vector2 centerLeftPoint) {
        rectangle.setPosition(centerLeftPoint.x, centerLeftPoint.y - (rectangle.height / 2f));
    }

    /**
     * Returns the top-right point of the {@link Rectangle} as a {@link Vector2}.
     *
     * @param rectangle the rectangle
     * @return the top-right point
     */
    public static Vector2 topRightPoint(Rectangle rectangle) {
        return new Vector2(rectangle.x + rectangle.width, rectangle.y + rectangle.height);
    }

    /**
     * Sets the {@link Rectangle} such that its top-right point rests directly on the {@link Vector2}.
     *
     * @param rectangle     the rectangle
     * @param topRightPoint the top-right point
     */
    public static void setTopRightToPoint(Rectangle rectangle, Vector2 topRightPoint) {
        rectangle.setPosition(topRightPoint.x - rectangle.width, topRightPoint.y - rectangle.height);
    }

    /**
     * Returns the top-center point of the {@link Rectangle} as a {@link Vector2}.
     *
     * @param rectangle the rectangle
     * @return the top-center point
     */
    public static Vector2 topCenterPoint(Rectangle rectangle) {
        return new Vector2(rectangle.x + (rectangle.width / 2f), rectangle.y + rectangle.height);
    }

    /**
     * Sets the {@link Rectangle} such that its top-center point rests directly on the {@link Vector2}.
     *
     * @param rectangle      the rectangle
     * @param topCenterPoint the top-center point
     */
    public static void setTopCenterToPoint(Rectangle rectangle, Vector2 topCenterPoint) {
        rectangle.setPosition(topCenterPoint.x - (rectangle.width / 2f), topCenterPoint.y - rectangle.height);
    }

    /**
     * Returns the top-left point of the {@link Rectangle}.
     *
     * @param rectangle the rectangle
     * @return the top-left point
     */
    public static Vector2 topLeftPoint(Rectangle rectangle) {
        return new Vector2(rectangle.x, rectangle.y + rectangle.height);
    }

    /**
     * Sets the {@link Rectangle} such that its top-left point rests directly on the {@link Vector2}.
     *
     * @param rectangle    the rectangle
     * @param topLeftPoint the top-left point
     */
    public static void setTopLeftToPoint(Rectangle rectangle, Vector2 topLeftPoint) {
        rectangle.setPosition(topLeftPoint.x, topLeftPoint.y - rectangle.height);
    }

}
