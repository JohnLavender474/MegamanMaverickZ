package com.mygdx.game.world;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.mygdx.game.core.Component;
import com.mygdx.game.core.Entity;
import com.mygdx.game.utils.Position;
import com.mygdx.game.utils.exceptions.InvalidArgumentException;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.HashSet;
import java.util.Set;

/**
 * Defines the body and world rules of the {@link Entity}.
 * <p>
 * {@link #debugColor} defines the color outline of the {@link #collisionBox} when world debugging is turned on.
 * <p>
 * {@link #positionOnEntity} defines the position of {@link #collisionBox} relative to {@link Entity#getBoundingBox()},
 * e.g., {@link Position#CENTER} means the Body is centered on the Entity and {@link Position#BOTTOM_CENTER}
 * means the bottom center of the Body is at the bottom center of the Entity.
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
 * }****</pre>
 * Of course, this means that, contrary to intuition, the smaller the values of {@link #frictionScalar} are, the
 * greater the "friction" resistance. This means that, for example, 0.1f results in greater "friction" than 0.9f.
 * <p>
 * {@link #impulse} defines the movement of the body per frame. Impulse is reset to zero after every frame.
 * <p>
 * {@link #velocity} defines the movement of the body per frame. This is in addition to {@link #impulse}.
 * The difference between the two values is that impulse is reset to zero after every frame but the value
 * of velocity remains the same each frame until it is changed by a caller.
 * <p>
 * {@link #fixtures} defines the {@link Fixture} instances attached to this body.
 */
@Getter
@Setter
@ToString
public class BodyComponent implements Component {

    private Color debugColor = Color.GREEN;
    private Vector2 gravity = new Vector2();
    private Vector2 impulse = new Vector2();
    private Vector2 velocity = new Vector2();
    private BodyType bodyType = BodyType.ABSTRACT;
    private Set<Fixture> fixtures = new HashSet<>();
    private Rectangle collisionBox = new Rectangle();
    private Position positionOnEntity = Position.CENTER;

    @Setter(AccessLevel.NONE)
    @Getter(AccessLevel.NONE)
    private Vector2 frictionScalar = new Vector2(1f, 1f);

    /**
     * Sets friction scalar values. Must be less than or equal to 1 and greater than 0, otherwise will throw exception.
     *
     * @param x the x friction scalar value
     * @param y the y friction scalar value
     * @throws InvalidArgumentException thrown if the value of x or y is greater than 1 or less than or equal to 0
     */
    public void setFrictionScalar(float x, float y)
            throws InvalidArgumentException {
        if (x > 1f || x <= 0f) {
            throw new InvalidArgumentException(String.valueOf(x), "friction scalar x");
        }
        if (y > 1f || y <= 0f) {
            throw new InvalidArgumentException(String.valueOf(y), "friction scalar y");
        }
        frictionScalar.set(x, y);
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
