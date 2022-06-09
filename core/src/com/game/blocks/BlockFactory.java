package com.game.blocks;

import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.math.Vector2;
import com.game.Entity;
import com.game.world.BodyComponent;
import com.game.world.BodyType;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Factory for instantiating new block entities.
 */
public abstract class BlockFactory {

    /**
     * Create list of block entities.
     *
     * @param rectangleMapObjects the rectangle map objects
     * @return the list
     */
    public final List<Entity> create(List<RectangleMapObject> rectangleMapObjects) {
        return rectangleMapObjects.stream().map(this::create).collect(Collectors.toList());
    }

    /**
     * Create block entity.
     *
     * @param rectangleMapObject the rectangle map object
     * @return the entity
     */
    public final Entity create(RectangleMapObject rectangleMapObject) {
        Entity entity = new Entity();
        define(entity, rectangleMapObject);
        return entity;
    }

    /**
     * The body type of the block's body.
     *
     * @return the body type
     */
    protected abstract BodyType bodyType();

    /**
     * Define the {@link Entity} using the provided {@link RectangleMapObject}.
     *
     * @param entity             the entity
     * @param rectangleMapObject the rectangle map object
     */
    protected void define(Entity entity, RectangleMapObject rectangleMapObject) {
        BodyComponent bodyComponent = new BodyComponent(bodyType());
        defineBodyComponent(bodyComponent, rectangleMapObject);
        entity.addComponent(bodyComponent);
    }

    /**
     * Define body component.
     *
     * @param bodyComponent      the body component
     * @param rectangleMapObject the rectangle map object
     */
    protected void defineBodyComponent(
            BodyComponent bodyComponent, RectangleMapObject rectangleMapObject) {
        bodyComponent.getCollisionBox().set(rectangleMapObject.getRectangle());
        bodyComponent.getGravity().set(gravity(rectangleMapObject));
        bodyComponent.getFrictionScalar().set(friction(rectangleMapObject));
        bodyComponent.getGravityScalar().set(gravityScalar(rectangleMapObject));
    }

    /**
     * Gravity vector 2.
     *
     * @param rectangleMapObject the rectangle map object
     * @return the vector 2
     */
    protected Vector2 gravity(RectangleMapObject rectangleMapObject) {
        Vector2 gravity = new Vector2();
        Float gravityX = rectangleMapObject.getProperties().get("GravityX", Float.class);
        gravity.x = gravityX == null ? 0f : gravityX;
        Float gravityY = rectangleMapObject.getProperties().get("GravityY", Float.class);
        gravity.y = gravityY == null ? 0f : gravityY;
        return gravity;
    }

    /**
     * Friction vector 2.
     *
     * @param rectangleMapObject the rectangle map object
     * @return the vector 2
     */
    protected Vector2 friction(RectangleMapObject rectangleMapObject) {
        Vector2 friction = new Vector2();
        Float frictionX = rectangleMapObject.getProperties().get("FrictionX", Float.class);
        friction.x = frictionX == null ? 1f : frictionX;
        Float frictionY = rectangleMapObject.getProperties().get("FrictionY", Float.class);
        friction.y = frictionY == null ? 1f : frictionY;
        return friction;
    }

    /**
     * Gravity scalar vector 2.
     *
     * @param rectangleMapObject the rectangle map object
     * @return the vector 2
     */
    protected Vector2 gravityScalar(RectangleMapObject rectangleMapObject) {
        Vector2 gravityScalar = new Vector2();
        Float gravityScalarX = rectangleMapObject.getProperties().get("GravityX", Float.class);
        gravityScalar.x = gravityScalarX == null ? 0f : gravityScalarX;
        Float gravityScalarY = rectangleMapObject.getProperties().get("GravityY", Float.class);
        gravityScalar.y = gravityScalarY == null ? 0f : gravityScalarY;
        return gravityScalar;
    }

}
