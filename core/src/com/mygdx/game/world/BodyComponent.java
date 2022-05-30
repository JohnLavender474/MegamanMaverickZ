package com.mygdx.game.world;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.mygdx.game.Component;
import com.mygdx.game.Entity;
import com.mygdx.game.utils.Position;
import lombok.Getter;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;

/**
 * Defines the body and world rules of the {@link Entity}
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
 *     // assert bc.getFrictionScalar() x and y values are between 0f and 1f,
 *     // throw exception if not true
 *     float x = bc.getVelocity().x * bc.getFrictionScalar().x;
 *     float y = bc.getVelocity().y * bc.getFrictionScalar().y;
 *     x += bc.getImpulse().x * bc.getFrictionScalar().x;
 *     y += bc.getImpulse().y * bc.getFrictionScalar().y;
 *     // other logic performed on x and y
 *     // before adding x and y to body component collision box
 *
 * }*</pre>
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
public class BodyComponent implements Component {
    private Color debugColor = Color.GREEN;
    private BodyType bodyType = BodyType.ABSTRACT;
    private final Vector2 impulse = new Vector2();
    private final Vector2 velocity = new Vector2();
    private Position positionOnEntity = Position.CENTER;
    private final Vector2 frictionScalar = new Vector2();
    private final Set<Fixture> fixtures = new HashSet<>();
    private final Rectangle collisionBox = new Rectangle();
}
