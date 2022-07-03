package com.game.world;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.game.Component;
import com.game.updatables.Updatable;
import com.game.utils.Direction;
import com.game.utils.UtilMethods;
import lombok.Getter;
import lombok.Setter;

import java.util.*;

/**
 * {@link Component} implementation for world bodies.
 */
@Getter
@Setter
public class BodyComponent implements Component {

    private final Vector2 friction = new Vector2();
    private final Vector2 velocity = new Vector2();
    private final Vector2 resistance = new Vector2(1f, 1f);
    private final List<Fixture> fixtures = new ArrayList<>();
    private final Rectangle collisionBox = new Rectangle();
    private final Rectangle priorCollisionBox = new Rectangle();
    private final Set<BodySense> bodySenses = EnumSet.noneOf(BodySense.class);
    private final Map<Direction, Boolean> collisionFlags = new EnumMap<>(Direction.class) {{
        for (Direction direction : Direction.values()) {
            put(direction, false);
        }
    }};

    private float gravity;
    private BodyType bodyType;
    private Updatable preProcess;
    private Updatable postProcess;
    private boolean gravityOn = true;
    private boolean affectedByResistance = true;

    /**
     * Instantiates a new Body Component.
     *
     * @param bodyType the body type
     */
    public BodyComponent(BodyType bodyType) {
        this.bodyType = bodyType;
    }

    /**
     * Set.
     *
     * @param bounds the bounds
     */
    public void set(Rectangle bounds) {
        collisionBox.set(bounds);
    }

    /**
     * Set bounds.
     *
     * @param x      the x
     * @param y      the y
     * @param width  the width
     * @param height the height
     */
    public void set(float x, float y, float width, float height) {
        collisionBox.set(x, y, width, height);
    }

    /**
     * Set size.
     *
     * @param width  the width
     * @param height the height
     */
    public void setSize(float width, float height) {
        collisionBox.setSize(width, height);
    }

    /**
     * Set width.
     *
     * @param width the width
     */
    public void setWidth(float width) {
        collisionBox.setWidth(width);
    }

    /**
     * Set height.
     *
     * @param height the height
     */
    public void setHeight(float height) {
        collisionBox.setHeight(height);
    }

    /**
     * Get size.
     *
     * @return the size
     */
    public Vector2 getSize() {
        Vector2 size = new Vector2();
        collisionBox.getSize(size);
        return size;
    }

    /**
     * Set position.
     *
     * @param x the x
     * @param y the y
     */
    public void setPosition(float x, float y) {
        collisionBox.setPosition(x, y);
    }

    /**
     * Get position.
     *
     * @return the position
     */
    public Vector2 getPosition() {
        Vector2 position = new Vector2();
        collisionBox.getPosition(position);
        return position;
    }

    /**
     * Sets position.
     *
     * @param position the position
     */
    public void setPosition(Vector2 position) {
        setPosition(position.x, position.y);
    }

    /**
     * Set center.
     *
     * @param x the x
     * @param y the y
     */
    public void setCenter(float x, float y) {
        collisionBox.setCenter(x, y);
    }

    /**
     * Get center.
     *
     * @return the center
     */
    public Vector2 getCenter() {
        Vector2 center = new Vector2();
        collisionBox.getCenter(center);
        return center;
    }

    /**
     * Sets center.
     *
     * @param center the center
     */
    public void setCenter(Vector2 center) {
        setCenter(center.x, center.y);
    }

    /**
     * Translate.
     *
     * @param delta the x and y
     */
    public void translate(Vector2 delta) {
        translate(delta.x, delta.y);
    }

    /**
     * Translate.
     *
     * @param x the x
     * @param y the y
     */
    public void translate(float x, float y) {
        collisionBox.x += x;
        collisionBox.y += y;
    }

    /**
     * Returns the pos delta between this frame and the prior frame.
     *
     * @return the pos delta
     */
    public Vector2 getPosDelta() {
        Vector2 priorPos = UtilMethods.centerPoint(priorCollisionBox);
        Vector2 currentPos = UtilMethods.centerPoint(collisionBox);
        return currentPos.sub(priorPos);
    }

    /**
     * Apply impulse.
     *
     * @param x the x
     * @param y the y
     */
    public void applyImpulse(float x, float y) {
        velocity.add(x, y);
    }

    /**
     * Sets velocity.
     *
     * @param velocity the velocity
     */
    public void setVelocity(Vector2 velocity) {
        setVelocity(velocity.x, velocity.y);
    }

    /**
     * Set velocity.
     *
     * @param x the x
     * @param y the y
     */
    public void setVelocity(float x, float y) {
        setVelocityX(x);
        setVelocityY(y);
    }

    /**
     * Set velocity x.
     *
     * @param x the x
     */
    public void setVelocityX(float x) {
        velocity.x = x;
    }

    /**
     * Set velocity y.
     *
     * @param y the y
     */
    public void setVelocityY(float y) {
        velocity.y = y;
    }

    /**
     * Set friction.
     *
     * @param friction the friction
     */
    public void setFriction(Vector2 friction) {
        setFriction(friction.x, friction.y);
    }

    /**
     * Sets friction.
     *
     * @param x the x
     * @param y the y
     */
    public void setFriction(float x, float y) {
        setFrictionX(x);
        setFrictionY(y);
    }

    /**
     * Set friction x.
     *
     * @param x the x
     */
    public void setFrictionX(float x) {
        friction.x = x;
    }

    /**
     * Set friction y.
     *
     * @param y the y
     */
    public void setFrictionY(float y) {
        friction.y = y;
    }

    /**
     * Set resistance.
     *
     * @param resistance the resistance
     */
    public void setResistance(Vector2 resistance) {
        setResistance(resistance.x, resistance.y);
    }

    /**
     * Set resistance.
     *
     * @param x the x
     * @param y the y
     */
    public void setResistance(float x, float y) {
        resistance.set(x, y);
    }

    /**
     * Add friction x.
     *
     * @param x the x
     */
    public void applyResistanceX(float x) {
        resistance.x += x;
    }

    /**
     * Add friction y.
     *
     * @param y the y
     */
    public void applyResistanceY(float y) {
        resistance.y += y;
    }

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
     * Add fixture.
     *
     * @param fixture the fixture
     */
    public void addFixture(Fixture fixture) {
        fixtures.add(fixture);
    }

    /**
     * Set prior collision box to current.
     */
    public void setPriorCollisionBoxToCurrent() {
        priorCollisionBox.set(collisionBox);
    }

}
