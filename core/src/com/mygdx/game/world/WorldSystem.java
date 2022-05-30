package com.mygdx.game.world;

import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.mygdx.game.*;
import com.mygdx.game.System;
import com.mygdx.game.utils.UtilMethods;
import com.mygdx.game.utils.exceptions.InvalidFieldException;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.HashSet;
import java.util.Set;

/**
 * {@link System} implementation that handles the logic of the "game world physics", i.e. gravity, collision handling,
 * and contact-event-handling.
 */
@RequiredArgsConstructor
public class WorldSystem extends System {

    @Getter
    private final Set<Class<? extends Component>> componentMask = Set.of(BodyComponent.class);
    @Getter
    private final Set<GameState> switchOffStates = Set.of(GameState.PAUSED);
    @Getter
    private final Vector2 gravity;

    private final Set<BodyComponent> bodyComponents = new HashSet<>();
    private final Set<Contact> currentContacts = new HashSet<>();
    private final Set<Contact> priorContacts = new HashSet<>();
    private final CollisionHandler collisionHandler;
    private final ContactListener contactListener;
    private final float fixedTimeStep;
    private float accumulator;

    @Override
    public void update(float delta) {
        accumulator += delta;
        while (accumulator >= fixedTimeStep) {
            accumulator -= fixedTimeStep;
            super.update(fixedTimeStep);
        }
    }

    @Override
    protected void preProcess(float delta) {
        getEntities().forEach(
                entity -> bodyComponents.add(entity.getComponent(BodyComponent.class)));
    }

    @Override
    protected void processEntity(Entity entity, float delta) {
        BodyComponent bodyComponent = entity.getComponent(BodyComponent.class);
        // Check for contacts and handle collisions
        Rectangle overlap = new Rectangle();
        for (BodyComponent otherBC : bodyComponents) {
            if (bodyComponent != otherBC) {
                for (Fixture f1 : bodyComponent.getFixtures()) {
                    for (Fixture f2 : otherBC.getFixtures()) {
                        if (Intersector.overlaps(f1.getFixtureBox(), f2.getFixtureBox())) {
                            currentContacts.add(new Contact(f1, f2));
                        }
                    }
                }
                if (Intersector.intersectRectangles(bodyComponent.getCollisionBox(),
                                                    otherBC.getCollisionBox(), overlap)) {
                    collisionHandler.handleCollision(bodyComponent, otherBC, overlap, delta);
                }
            }
        }
        // Remove body from set
        bodyComponents.remove(bodyComponent);
        // Check that friction scalars are correct, throw exception if any are invalid
        if (bodyComponent.getFrictionScalar().x > 1f || bodyComponent.getFrictionScalar().x < 0f) {
            throw new InvalidFieldException(String.valueOf(bodyComponent.getFrictionScalar().x),
                                            "friction scalar x", "body component of " + entity);
        }
        if (bodyComponent.getFrictionScalar().y > 1f || bodyComponent.getFrictionScalar().y < 0f) {
            throw new InvalidFieldException(String.valueOf(bodyComponent.getFrictionScalar().x),
                                            "friction scalar y", "body component of " + entity);
        }
        // Apply friction-scaled velocity to body
        float x = bodyComponent.getVelocity().x * bodyComponent.getFrictionScalar().x;
        float y = bodyComponent.getVelocity().y * bodyComponent.getFrictionScalar().y;
        // Apply friction-scaled impulse to body
        x += bodyComponent.getImpulse().x * bodyComponent.getFrictionScalar().x;
        y += bodyComponent.getImpulse().y * bodyComponent.getFrictionScalar().y;
        // Apply friction-scaled gravity to dynamic or kinematic body, ignore for static or abstract body
        if (bodyComponent.getBodyType() == BodyType.DYNAMIC || bodyComponent.getBodyType() == BodyType.KINEMATIC) {
            x += gravity.x * bodyComponent.getFrictionScalar().x;
            y += gravity.y * bodyComponent.getFrictionScalar().y;
        }
        // Scale x and y to delta and add values to Body Component collision box,
        // important to note that delta == fixedTimeStep
        bodyComponent.getCollisionBox().x += x * delta;
        bodyComponent.getCollisionBox().y += y * delta;
        // The Entity is moved to conform to the Body Component's specified position on the Entity
        UtilMethods.positionRectOntoOther(entity.getBoundingBox(), bodyComponent.getCollisionBox(),
                                          bodyComponent.getPositionOnEntity());
        // Each Fixture is moved to conform to its position offset from the center of the Body Component
        bodyComponent.getFixtures().forEach(fixture -> {
            Vector2 center = new Vector2();
            bodyComponent.getCollisionBox().getCenter(center);
            float fixtureX = center.x + fixture.getOffset().x;
            float fixtureY = center.y + fixture.getOffset().y;
            fixture.getFixtureBox().setCenter(fixtureX, fixtureY);
        });
    }

    @Override
    protected void postProcess(float delta) {
        // handles Contact instances in the current contacts set
        currentContacts.forEach(currentContact -> {
            if (priorContacts.contains(currentContact)) {
                contactListener.beginContact(currentContact, delta);
            } else {
                contactListener.continueContact(currentContact, delta);
            }
        });
        // handles Contact instances in the prior contacts set
        priorContacts.forEach(priorContact -> {
            if (!currentContacts.contains(priorContact)) {
                contactListener.endContact(priorContact, delta);
            }
        });
        // moves current contacts to prior, then clears the set of current contacts
        priorContacts.clear();
        priorContacts.addAll(currentContacts);
        currentContacts.clear();
        // resets the impulse of all bodies
        getEntities().forEach(
                entity -> entity.getComponent(BodyComponent.class).getImpulse().setZero());
    }

}
