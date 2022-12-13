package com.game.world;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.game.Component;
import com.game.utils.interfaces.Updatable;
import com.game.utils.enums.Direction;
import com.game.utils.UtilMethods;
import lombok.Getter;
import lombok.Setter;

import java.util.*;

import static java.lang.Float.*;
import static java.util.stream.Collectors.toList;

/**
 * {@link Component} implementation for world bodies.
 */
@Getter
@Setter
public class BodyComponent extends Component {

    private final Vector2 velocity = new Vector2();
    private final Vector2 friction = new Vector2();
    private final Vector2 resistance = new Vector2(1f, 1f);
    private final Rectangle collisionBox = new Rectangle();
    private final List<Fixture> fixtures = new ArrayList<>();
    private final Rectangle priorCollisionBox = new Rectangle();
    private final Vector2 clamp = new Vector2(MAX_VALUE, MAX_VALUE);
    private final Set<String> customCollisionMask = new HashSet<>();
    private final Set<BodySense> bodySenses = EnumSet.noneOf(BodySense.class);
    private final Map<Direction, Boolean> collisionFlags = new EnumMap<>(Direction.class) {{
        for (Direction direction : Direction.values()) {
            put(direction, false);
        }
    }};

    private String customCollisionBit = null;

    private float gravity;
    private BodyType bodyType;
    private Updatable preProcess;
    private Updatable postProcess;
    private boolean gravityOn = true;
    private boolean affectedByResistance = true;

    /**
     * Instantiates a new Body Component with the given {@link BodyType} value.
     *
     * @param bodyType the body type
     */
    public BodyComponent(BodyType bodyType) {
        this.bodyType = bodyType;
    }

    /**
     * See {@link #BodyComponent(BodyType)}. Bounds pairOf supplied collision box are copied.
     *
     * @param bodyType the body type
     * @param collisionBox the collision box bounds to be copied
     */
    public BodyComponent(BodyType bodyType, Rectangle collisionBox) {
        this(bodyType);
        this.collisionBox.set(collisionBox);
    }

    /**
     * Get min x.
     *
     * @return min x
     */
    public float getMinX() {
        return collisionBox.x;
    }

    /**
     * Get max x.
     *
     * @return max x
     */
    public float getMaxX() {
        return collisionBox.x + collisionBox.width;
    }

    /**
     * Get min y.
     *
     * @return min y
     */
    public float getMinY() {
        return collisionBox.y;
    }

    /**
     * Get max y.
     *
     * @return max y
     */
    public float getMaxY() {
        return collisionBox.y + collisionBox.height;
    }

    /**
     * Return if this body overlaps the other body.
     *
     * @param o the other body
     * @return if this body overlaps the other body
     */
    public boolean overlaps(BodyComponent o) {
        return overlaps(o.getCollisionBox());
    }

    /**
     * If this body overlaps the bounds.
     *
     * @param o the bounds
     * @return if this body overlaps the bounds
     */
    public boolean overlaps(Rectangle o) {
        return collisionBox.overlaps(o);
    }

    /**
     * If this body is above the other.
     *
     * @param bodyComponent the other body component
     * @return if this body is above the other
     */
    public boolean isAbove(BodyComponent bodyComponent) {
        return getCenter().y > bodyComponent.getCenter().y;
    }

    /**
     * If this body is below the other.
     *
     * @param bodyComponent the other body component
     * @return if this body is below the other
     */
    public boolean isBelow(BodyComponent bodyComponent) {
        return !isAbove(bodyComponent);
    }

    /**
     * If this body is right pairOf the other.
     *
     * @param bodyComponent the other body component
     * @return if this body is right pairOf the other
     */
    public boolean isRightOf(BodyComponent bodyComponent) {
        return getCenter().x > bodyComponent.getCenter().x;
    }

    /**
     * If this body is left pairOf the other.
     *
     * @param bodyComponent the other body component
     * @return if this body is left pairOf the other
     */
    public boolean isLeftOf(BodyComponent bodyComponent) {
        return !isRightOf(bodyComponent);
    }

    /**
     * Translate gravity.
     *
     * @param delta the delta
     */
    public void translateGravity(float delta) {
        gravity += delta;
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
     * Gets the max vals of the bounds.
     *
     * @return the max vals of the bounds
     */
    public Vector2 getBoundsMax() {
        return new Vector2(collisionBox.x + collisionBox.width, collisionBox.y + collisionBox.height);
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
     * Sets the clamp of the velocity.
     *
     * @param clamp the clamp
     */
    public void setClamp(Vector2 clamp) {
        setClamp(clamp.x, clamp.y);
    }

    /**
     * Sets the clamp of the velocity.
     *
     * @param x the x clamp
     * @param y the y clamp
     */
    public void setClamp(float x, float y) {
        clamp.set(x, y);
    }

    /**
     * Translate velocity.
     *
     * @param x the x vel delta
     * @param y the y vel delta
     */
    public void translateVelocity(float x, float y) {
        velocity.add(x, y);
    }

    /**
     * Translate velocity.
     *
     * @param delta the vel delta
     */
    public void translateVelocity(Vector2 delta) {
        translateVelocity(delta.x, delta.y);
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
     * Returns if any of the body senses true.
     *
     * @param bodySenses the body senses
     * @return is any of the body senses true
     */
    public boolean is(BodySense... bodySenses) {
        for (BodySense bodySense : bodySenses) {
            if (this.bodySenses.contains(bodySense)) {
                return true;
            }
        }
        return false;
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
     * Returns first fixture that has matching fixture type.
     *
     * @param fixtureType the fixture type
     * @return the optional fixture
     */
    public Optional<Fixture> getFirstMatchingFixture(FixtureType fixtureType) {
        return fixtures.stream().filter(fixture -> fixture.isFixtureType(fixtureType)).findFirst();
    }

    /**
     * Returns the collection of fixtures matching the specified fixture type
     *
     * @param fixtureType the fixture type
     * @return the matching fixtures
     */
    public Collection<Fixture> getFixturesOfType(FixtureType fixtureType) {
        return fixtures.stream().filter(fixture -> fixture.getFixtureType().equals(fixtureType)).collect(toList());
    }


    /**
     * Returns only the fixtures that are active.
     *
     * @return only the fixtures that are active
     */
    public List<Fixture> getActiveFixtures() {
        return fixtures.stream().filter(Fixture::isActive).toList();
    }

    /**
     * Set prior collision box to current.
     */
    public void setPriorCollisionBoxToCurrent() {
        priorCollisionBox.set(collisionBox);
    }


    /**
     * Adds each custom collision bit mask to this body. If a body component has {@link #getCustomCollisionBit()} equal
     * to any of the provided bits, then this body component will be offset.
     *
     * @param bits the custom collision bits to mask
     */
    public void maskForCustomCollisions(String... bits) {
        customCollisionMask.addAll(Arrays.asList(bits));
    }

    /**
     * Returns if this body component is set to be offset when colliding with the provided body.
     *
     * @param bc the other body
     * @return if set to offset when colliding with other body
     */
    public boolean isMaskedForCollisionWith(BodyComponent bc) {
        String bit = bc.getCustomCollisionBit();
        return bit != null && isCustomCollisionMask(bc.getCustomCollisionBit());
    }

    /**
     * Returns if this is masking for the custom collision bit.
     *
     * @param bit the custom collision mask bit
     * @return if this is masking for the custom collision bit
     */
    public boolean isCustomCollisionMask(String bit) {
        return customCollisionMask.contains(bit);
    }

}
