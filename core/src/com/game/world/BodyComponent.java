package com.game.world;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.game.Component;
import com.game.Entity;
import com.game.utils.Direction;
import com.game.utils.Position;
import com.game.utils.Updatable;
import lombok.*;

import java.util.*;

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
 * }*********</pre>
 * Of course, this means that, contrary to intuition, the smaller the values of {@link #frictionScalar} are, the
 * greater the "friction" resistance. This means that, for example, 0.1f results in greater "friction" than 0.9f.
 * <p>
 * {@link #impulse} defines the movement of the body for one fram      e. Impulse is reset to zero after every frame.
 * <p>
 * {@link #fixtures} defines the {@link Fixture} instances attached to this body.
 */
@Getter
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
    @Getter(AccessLevel.NONE) private final Set<BodySense> bodySenses =
            EnumSet.noneOf(BodySense.class);
    @Getter(AccessLevel.NONE) private final Map<Direction, Boolean> collisionFlags =
            new EnumMap<>(Direction.class) {{
                for (Direction direction : Direction.values()) {
                    put(direction, false);
                }
            }};
    // Called once before contacts and collisions are detected and forces are applied
    @Setter private Updatable preProcess;
    // Called once after contacts and collisions are detected and forces are applied
    @Setter private Updatable postProcess;

    /**
     * Is body sense true.
     *
     * @param bodySense the body sense
     * @return is body sense true
     */
    public boolean is(BodySense bodySense) {
        return bodySenses.contains(bodySense);
    }

    /**
     * Set is sensing body sense.
     *
     * @param bodySense the body sense
     */
    public void setIs(BodySense bodySense) {
        bodySenses.add(bodySense);
    }

    /**
     * Set body sense not true.
     *
     * @param bodySense the body sense
     */
    public void setIsNot(BodySense bodySense) {
        bodySenses.remove(bodySense);
    }

    /**
     * Clear collision flags.
     */
    public void clearCollisionFlags() {
        collisionFlags.replaceAll((direction, aBoolean) -> false);
    }

    /**
     * Is colliding in the provided direction.
     *
     * @param direction the direction
     * @return is colliding in the provided direction
     */
    public boolean isColliding(Direction direction) {
        return collisionFlags.get(direction);
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

}
