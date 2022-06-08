package com.game.world;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.game.Component;
import com.game.Entity;
import com.game.utils.Direction;
import com.game.utils.Position;
import com.game.utils.Updatable;
import com.game.utils.exceptions.InvalidArgumentException;
import lombok.*;

import java.util.*;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * Defines the body and world rules of the {@link Entity}.
 * <p>
 * {@link #bodyType} defines the "worldly rules" and "physicality" of the body. See {@link Position} for details.
 * <p>
 * {@link #frictionScalar} defines the friction scalar applied to this body. The value must be between 0f and 1f.
 * Each frame, the movement of {@link #collisionBox} is calculated via
 * <pre>{@code
 *
 *     BodyComponent bc = // fetch BodyComponent from Entity instance
 *     // assert bc.getFrictionScalarCopy() x and y values are between 0f and 1f,
 *     // throw exception if not true
 *     float x = bc.getVelocity().x * bc.getFrictionScalarCopy().x;
 *     float y = bc.getVelocity().y * bc.getFrictionScalarCopy().y;
 *     x += bc.getImpulse().x * bc.getFrictionScalarCopy().x;
 *     y += bc.getImpulse().y * bc.getFrictionScalarCopy().y;
 *     // other logic performed on x and y
 *     // before adding x and y to body component collision box
 *
 * }********</pre>
 * Of course, this means that, contrary to intuition, the smaller the values of {@link #frictionScalar} are, the
 * greater the "friction" resistance. This means that, for example, 0.1f results in greater "friction" than 0.9f.
 * <p>
 * {@link #impulse} defines the movement of the body for one fram      e. Impulse is reset to zero after every frame.
 * <p>
 * {@link #fixtures} defines the {@link Fixture} instances attached to this body.
 */
@Getter
@Setter
@ToString
@RequiredArgsConstructor
public class BodyComponent implements Component {

    private final BodyType bodyType;
    private final Vector2 impulse = new Vector2();
    private final Vector2 gravity = new Vector2();
    private final Rectangle collisionBox = new Rectangle();
    private final List<Fixture> fixtures = new ArrayList<>();
    private final Vector2 gravityScalar = new Vector2(1f, 1f);
    private final Vector2 frictionScalar = new Vector2(1f, 1f);
    private final Map<Direction, Boolean> collisionFlags = new EnumMap<>(Direction.class) {{
        for (Direction direction : Direction.values()) {
            put(direction, false);
        }
    }};
    // Called once before contacts and collisions are detected and forces are applied
    private Updatable preProcess;
    // Called once after contacts and collisions are detected and forces are applied
    private Updatable postProcess;

    /**
     * Clear collision flags.
     */
    public void clearCollisionFlags() {
        getCollisionFlags().replaceAll((direction, aBoolean) -> false);
    }

    /**
     * Is colliding in the provided direction.
     *
     * @param direction the direction
     * @return is colliding in the provided direction
     */
    public boolean isColliding(Direction direction) {
        return getCollisionFlags().get(direction);
    }

    /**
     * Set colliding left.
     */
    public void setCollidingLeft() {
        collisionFlags.replace(Direction.LEFT, true);
    }

    /**
     * Set colliding right.
     */
    public void setCollidingRight() {
        collisionFlags.replace(Direction.RIGHT, true);
    }

    /**
     * Set colliding up.
     */
    public void setCollidingUp() {
        collisionFlags.replace(Direction.UP, true);
    }

    /**
     * Set colliding down.
     */
    public void setCollidingDown() {
        collisionFlags.replace(Direction.DOWN, true);
    }

    /**
     * See {@link #setFrictionScalar(float, float)}.
     *
     * @param x the x value of the friction scalar
     * @throws InvalidArgumentException thrown if the value of x is greater than 1 or less than or equal to 0
     */
    public void setFrictionScalarX(float x)
            throws InvalidArgumentException {
        if (x > 1f || x <= 0f) {
            throw new InvalidArgumentException(String.valueOf(x), "friction scalar x");
        }
        frictionScalar.x = x;
    }

    /**
     * See {@link #setFrictionScalar(float, float)}.
     *
     * @param y the y value of the friction scalar
     * @throws InvalidArgumentException thrown if the value of y is greater than 1 or less than or equal to 0
     */
    public void setFrictionScalarY(float y)
            throws InvalidArgumentException {
        if (y > 1f || y <= 0f) {
            throw new InvalidArgumentException(String.valueOf(y), "friction scalar y");
        }
        frictionScalar.y = y;
    }

    /**
     * Sets friction scalar values. Must be less than or equal to 1 and greater than 0, otherwise will throw exception.
     * Friction scalar values are reset back to 1 after every frame.
     *
     * @param x the x friction scalar value
     * @param y the y friction scalar value
     * @throws InvalidArgumentException thrown if the value of x or y is greater than 1 or less than or equal to 0
     */
    public void setFrictionScalar(float x, float y)
            throws InvalidArgumentException {
        setFrictionScalarX(x);
        setFrictionScalarY(y);
    }

    /**
     * Gets copy of friction scalar {@link Vector2}.
     *
     * @return the friction scalar values
     */
    public Vector2 getFrictionScalarCopy() {
        return new Vector2(frictionScalar);
    }

}
