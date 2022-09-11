package com.game.utils;

import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.*;
import com.badlogic.gdx.math.collision.BoundingBox;
import com.game.utils.enums.Direction;
import com.game.utils.enums.Position;
import com.game.utils.interfaces.Positional;
import com.game.utils.objects.Pair;

import java.util.EnumMap;
import java.util.Map;

import static com.badlogic.gdx.graphics.Texture.TextureFilter.*;
import static java.lang.Math.*;

/**
 * Global utility methods.
 */
public class UtilMethods {

    public static boolean lineOverlapsRectangle(Rectangle rect, Pair<Vector2> line) {
        return lineOverlapsRectangle(rect, line.getFirst(), line.getSecond());
    }

    public static boolean lineOverlapsRectangle(Rectangle rect, Vector2 linePoint1, Vector2 linePoint2) {
        return lineOverlapsRectangle(rect, linePoint1, linePoint2, new Vector2());
    }

    public static boolean lineOverlapsRectangle(Rectangle rect, Vector2 linePoint1, Vector2 linePoint2, Vector2 inter) {
        return rectToLines(rect).values().stream().anyMatch(l -> Intersector.intersectLines(
                l.getFirst(), l.getSecond(), linePoint1, linePoint2, inter));
    }

    public static Map<Direction, Pair<Vector2>> rectToLines(Rectangle rect) {
        Map<Direction, Pair<Vector2>> rectToLinesMap = new EnumMap<>(Direction.class);
        rectToLinesMap.put(Direction.DIR_UP, Pair.of(topLeftPoint(rect), topRightPoint(rect)));
        rectToLinesMap.put(Direction.DIR_DOWN, Pair.of(new Vector2(rect.x, rect.y), bottomRightPoint(rect)));
        rectToLinesMap.put(Direction.DIR_LEFT, Pair.of(new Vector2(rect.x, rect.y), topLeftPoint(rect)));
        rectToLinesMap.put(Direction.DIR_RIGHT, Pair.of(bottomRightPoint(rect), topRightPoint(rect)));
        return rectToLinesMap;
    }

    /**
     * Draws the sprite with its texture filtered.
     *
     * @param sprite the sprite
     * @param spriteBatch the sprite batch
     */
    public static void drawFiltered(Sprite sprite, SpriteBatch spriteBatch) {
        Texture texture = sprite.getTexture();
        if (texture == null) {
            return;
        }
        texture.setFilter(Nearest, Nearest);
        sprite.draw(spriteBatch);
    }

    /**
     * Returns if the provided rectangle is within the camera bounds.
     *
     * @param camera the camera*
     * @param rectangle the rectangle
     * @return if the provided rectangle is within the camera bounds
     */
    public static boolean isInCamBounds(Camera camera, Rectangle rectangle) {
        return camera.frustum.boundsInFrustum(rectToBBox(rectangle));
    }

    /**
     * Returns the number bounded between min and max.
     *
     * @param number the number
     * @param min the min
     * @param max the max
     * @return the bounded number
     * @param <T> the number type
     */
    public static <T extends Number & Comparable<T>> T clampNumber(T number, T min, T max) {
        if (number.compareTo(min) < 0) {
            number = min;
        } else if (number.compareTo(max) > 0) {
            number = max;
        }
        return number;
    }

    /**
     * Returns true if the test object is equal to any of the following supplied objects.
     *
     * @param test the test object
     * @param objs the var args of supplied objects to test against
     * @return if the test object is equal to any of the following supplied objects
     */
    public static boolean equalsAny(Object test, Object... objs) {
        for (Object obj : objs) {
            if (test.equals(obj)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Returns if the two rectangles overlap.
     *
     * @param r1 the first rectangle
     * @param r2 the second rectangle
     * @return if the two rectangles overlap
     */
    public static boolean overlaps(Rectangle r1, Rectangle r2) {
        return r1.x <= r2.x + r2.width &&
                r1.x + r1.width >= r2.x &&
                r1.y <= r2.y + r2.height &&
                r1.y + r1.height >= r2.y;
    }

    /**
     * Returns the normalized trajectory.
     *
     * @param start the starting point
     * @param end   the ending point
     * @param speed the speed
     * @return the normalized trajectory
     */
    public static Vector2 normalizedTrajectory(Vector2 start, Vector2 end, float speed) {
        float x = end.x - start.x;
        float y = end.y - start.y;
        float length = (float) sqrt(x * x + y * y);
        x /= length;
        y /= length;
        return new Vector2(x * speed, y * speed);
    }

    /**
     * Rounds the provided float to the number of decimal places specified.
     *
     * @param num the provided float
     * @param decimals the number of decimal places
     * @return the rounded float
     */
    public static float roundedFloat(float num, int decimals) {
        float scale = (float) pow(10, decimals);
        return round(num * scale) / scale;
    }

    /**
     * Rounds the provided {@link Vector2} to the number of decimal places specified.
     *
     * @param vector2 the vector2
     * @param decimals the number of decimal places
     */
    public static void roundedVector2(Vector2 vector2, int decimals) {
        vector2.x = roundedFloat(vector2.x, decimals);
        vector2.y = roundedFloat(vector2.y, decimals);
    }

    /**
     * Returns {@link BoundingBox} representation of {@link Rectangle}.
     *
     * @param rectangle the rectangle
     * @return the bounding box representation
     */
    public static BoundingBox rectToBBox(Rectangle rectangle) {
        return new BoundingBox(new Vector3(rectangle.getX(), rectangle.getY(), 0.0f),
                new Vector3(rectangle.getX() + rectangle.getWidth(), rectangle.getY() + rectangle.getHeight(), 0.0f));
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
     * Get scaled rect centered on provided rect.
     *
     * @param rect  the rect
     * @param scale the scale
     * @return the scaled rect
     */
    public static Rectangle getScaledRect(Rectangle rect, float scale) {
        Rectangle scaledRect = new Rectangle(rect);
        scaledRect.width *= scale;
        scaledRect.height *= scale;
        scaledRect.setCenter(centerPoint(rect));
        return scaledRect;
    }

    /**
     * Converts {@link Vector2} to {@link Vector3} with {@link Vector3#z} equal to 0.
     *
     * @param vector2 the vector2
     * @return vector3 vector 3
     */
    public static Vector3 toVec3(Vector2 vector2) {
        return new Vector3(vector2.x, vector2.y, 0f);
    }

    /**
     * Converts {@link Vector3} to {@link Vector2}. {@link Vector3#z} is ignored.
     *
     * @param vector3 the vector3
     * @return vector2 vector 2
     */
    public static Vector2 toVec2(Vector3 vector3) {
        return new Vector2(vector3.x, vector3.y);
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
     * @param start  the starting coordinates
     * @param target the target coordinates
     * @param delta  the delta time
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
     * @param start  the starting value
     * @param target the target value
     * @param delta  the delta time
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
     * @param other      the other Rectangle
     * @param overlap    the overlap of the two Rectangles
     * @return null if there is no overlap, otherwise the direction in which the first Rectangle should be pushed
     */
    public static Direction getOverlapPushDirection(Rectangle toBePushed, Rectangle other, Rectangle overlap) {
        Intersector.intersectRectangles(toBePushed, other, overlap);
        if (overlap.width == 0f && overlap.height == 0f) {
            return null;
        }
        if (overlap.width > overlap.height) {
            return toBePushed.y > other.y ? Direction.DIR_UP : Direction.DIR_DOWN;
        } else {
            return toBePushed.x > other.x ? Direction.DIR_RIGHT : Direction.DIR_LEFT;
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
    public static void positionRectOnOther(Rectangle toBeMoved, Rectangle staticRect, Position position) {
        switch (position) {
            case TOP_LEFT -> setTopLeftToPoint(toBeMoved, topLeftPoint(staticRect));
            case TOP_CENTER -> setTopCenterToPoint(toBeMoved, topCenterPoint(staticRect));
            case TOP_RIGHT -> setTopRightToPoint(toBeMoved, topRightPoint(staticRect));
            case CENTER_LEFT -> setCenterLeftToPoint(toBeMoved, centerLeftPoint(staticRect));
            case CENTER -> toBeMoved.setCenter(centerPoint(staticRect));
            case CENTER_RIGHT -> setCenterRightToPoint(toBeMoved, centerRightPoint(staticRect));
            case BOTTOM_LEFT -> toBeMoved.setPosition(staticRect.x, staticRect.y);
            case BOTTOM_CENTER -> setBottomCenterToPoint(toBeMoved, bottomCenterPoint(staticRect));
            case BOTTOM_RIGHT -> setBottomRightToPoint(toBeMoved, bottomRightPoint(staticRect));
        }
    }

    /**
     * Max x float.
     *
     * @param rectangle the rectangle
     * @return the float
     */
    public static float maxX(Rectangle rectangle) {
        return rectangle.x + rectangle.width;
    }

    /**
     * Max y float.
     *
     * @param rectangle the rectangle
     * @return the float
     */
    public static float maxY(Rectangle rectangle) {
        return rectangle.y + rectangle.height;
    }

    /**
     * Gets the point corresponding to the position on the supplied rectangle.
     *
     * @param rectangle the rectangle
     * @param position  the position
     * @return the point
     */
    public static Vector2 getPoint(Rectangle rectangle, Position position) {
        switch (position) {
            case BOTTOM_LEFT:
                Vector2 point = new Vector2();
                rectangle.getPosition(point);
                return point;
            case BOTTOM_CENTER:
                return bottomCenterPoint(rectangle);
            case BOTTOM_RIGHT:
                return bottomRightPoint(rectangle);
            case CENTER_LEFT:
                return centerLeftPoint(rectangle);
            case CENTER:
                return centerPoint(rectangle);
            case CENTER_RIGHT:
                return centerRightPoint(rectangle);
            case TOP_LEFT:
                return topLeftPoint(rectangle);
            case TOP_CENTER:
                return topCenterPoint(rectangle);
            case TOP_RIGHT:
                return topRightPoint(rectangle);
            default:
                throw new IllegalStateException("Position value is not valid");
        }
    }

    /**
     * See {@link #setToPoint(Rectangle, Vector2, Position, Positional)}.
     *
     * @param rectangle the rectangle
     * @param point     the point
     * @param position  the position
     */
    public static void setToPoint(Rectangle rectangle, Vector2 point, Position position) {
        setToPoint(rectangle, point, position, rectangle::setPosition);
    }

    /**
     * Sets the {@link Positional} to the point based on the supplied position value.
     *
     * @param rectangle the rectangle
     * @param point     the point
     * @param position  the position
     */
    public static void setToPoint(Rectangle rectangle, Vector2 point, Position position, Positional positional) {
        switch (position) {
            case BOTTOM_LEFT -> positional.setPosition(point);
            case BOTTOM_CENTER -> setBottomCenterToPoint(rectangle, point, positional);
            case BOTTOM_RIGHT -> setBottomRightToPoint(rectangle, point, positional);
            case CENTER_LEFT -> setCenterLeftToPoint(rectangle, point, positional);
            case CENTER -> positional.setPosition(point.x - rectangle.width / 2f, point.y - rectangle.height / 2f);
            case CENTER_RIGHT -> setCenterRightToPoint(rectangle, point, positional);
            case TOP_LEFT -> setTopLeftToPoint(rectangle, point, positional);
            case TOP_CENTER -> setTopCenterToPoint(rectangle, point, positional);
            case TOP_RIGHT -> setTopRightToPoint(rectangle, point, positional);
            default -> throw new IllegalStateException("Position value is not valid");
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
     * See {@link #setBottomRightToPoint(Rectangle, Vector2, Positional)}.
     *
     * @param rectangle        the rectangle
     * @param bottomRightPoint the bottom-right point
     */
    public static void setBottomRightToPoint(Rectangle rectangle, Vector2 bottomRightPoint) {
        setBottomRightToPoint(rectangle, bottomRightPoint, rectangle::setPosition);
    }

    /**
     * Sets the {@link Positional} such that its bottom-right point rests directly on the {@link Vector2}.
     *
     * @param rectangle        the rectangle
     * @param bottomRightPoint the bottom-right point
     */
    public static void setBottomRightToPoint(Rectangle rectangle, Vector2 bottomRightPoint, Positional positional) {
        positional.setPosition(bottomRightPoint.x - rectangle.width, bottomRightPoint.y);
    }

    /**
     * Returns the bottom-center point of the {@link Rectangle} as a {@link Vector2}.
     *
     * @param rectangle the rectangle
     * @return the bottom-center point
     */
    public static Vector2 bottomCenterPoint(Rectangle rectangle) {
        return new Vector2(rectangle.x + rectangle.width / 2f, rectangle.y);
    }

    /**
     * See {@link #setBottomCenterToPoint(Rectangle, Vector2, Positional)}.
     *
     * @param rectangle         the rectangle
     * @param bottomCenterPoint the bottom-center point
     */
    public static void setBottomCenterToPoint(Rectangle rectangle, Vector2 bottomCenterPoint) {
        setBottomCenterToPoint(rectangle, bottomCenterPoint, rectangle::setPosition);
    }

    /**
     * Sets the {@link Positional} such that its bottom-center point rests directly on the {@link Vector2}.
     *
     * @param rectangle         the rectangle
     * @param bottomCenterPoint the bottom-center point
     */
    public static void setBottomCenterToPoint(Rectangle rectangle, Vector2 bottomCenterPoint, Positional positional) {
        positional.setPosition(bottomCenterPoint.x - rectangle.width / 2f, bottomCenterPoint.y);
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
     * See {@link #setCenterRightToPoint(Rectangle, Vector2, Positional)}.
     *
     * @param rectangle        the rectangle
     * @param centerRightPoint the center-right point
     */
    public static void setCenterRightToPoint(Rectangle rectangle, Vector2 centerRightPoint) {
        setCenterRightToPoint(rectangle, centerRightPoint, rectangle::setPosition);
    }

    /**
     * Sets the {@link Positional} such that its center-right point resets directly on the {@link Vector2}.
     *
     * @param rectangle        the rectangle
     * @param centerRightPoint the center-right point
     */
    public static void setCenterRightToPoint(Rectangle rectangle, Vector2 centerRightPoint, Positional positional) {
        positional.setPosition(centerRightPoint.x - rectangle.width, centerRightPoint.y - (rectangle.height / 2f));
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
     * See {@link #setCenterLeftToPoint(Rectangle, Vector2, Positional)}.
     *
     * @param rectangle       the rectangle
     * @param centerLeftPoint the center-left point
     */
    public static void setCenterLeftToPoint(Rectangle rectangle, Vector2 centerLeftPoint) {
        setCenterLeftToPoint(rectangle, centerLeftPoint, rectangle::setPosition);
    }

    /**
     * Sets the {@link Positional} such that its center-left point rests directly on the {@link Vector2}.
     *
     * @param rectangle       the rectangle
     * @param centerLeftPoint the center-left point
     */
    public static void setCenterLeftToPoint(Rectangle rectangle, Vector2 centerLeftPoint, Positional positional) {
        positional.setPosition(centerLeftPoint.x, centerLeftPoint.y - (rectangle.height / 2f));
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
     * See {@link #setTopRightToPoint(Rectangle, Vector2, Positional)}.
     *
     * @param rectangle     the rectangle
     * @param topRightPoint the top-right point
     */
    public static void setTopRightToPoint(Rectangle rectangle, Vector2 topRightPoint) {
        setTopRightToPoint(rectangle, topRightPoint, rectangle::setPosition);
    }

    /**
     * Sets the {@link Positional} such that its top-right point rests directly on the {@link Vector2}.
     *
     * @param rectangle     the rectangle
     * @param topRightPoint the top-right point
     */
    public static void setTopRightToPoint(Rectangle rectangle, Vector2 topRightPoint, Positional positional) {
        positional.setPosition(topRightPoint.x - rectangle.width, topRightPoint.y - rectangle.height);
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
     * See {@link #setTopCenterToPoint(Rectangle, Vector2, Positional)}.
     *
     * @param rectangle      the rectangle
     * @param topCenterPoint the top-center point
     */
    public static void setTopCenterToPoint(Rectangle rectangle, Vector2 topCenterPoint) {
        setTopCenterToPoint(rectangle, topCenterPoint, rectangle::setPosition);
    }

    /**
     * Sets the {@link Positional} such that its top-center point rests directly on the {@link Vector2}.
     *
     * @param rectangle      the rectangle
     * @param topCenterPoint the top-center point
     */
    public static void setTopCenterToPoint(Rectangle rectangle, Vector2 topCenterPoint, Positional positional) {
        positional.setPosition(topCenterPoint.x - (rectangle.width / 2f), topCenterPoint.y - rectangle.height);
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
     * See {@link #setTopLeftToPoint(Rectangle, Vector2, Positional)}.
     *
     * @param rectangle    the rectangle
     * @param topLeftPoint the top-left point
     */
    public static void setTopLeftToPoint(Rectangle rectangle, Vector2 topLeftPoint) {
        setTopLeftToPoint(rectangle, topLeftPoint, rectangle::setPosition);
    }

    /**
     * Sets the {@link Positional} such that its top-left point rests directly on the {@link Vector2}.
     *
     * @param rectangle    the rectangle
     * @param topLeftPoint the top-left point
     */
    public static void setTopLeftToPoint(Rectangle rectangle, Vector2 topLeftPoint, Positional positional) {
        positional.setPosition(topLeftPoint.x, topLeftPoint.y - rectangle.height);
    }

}
