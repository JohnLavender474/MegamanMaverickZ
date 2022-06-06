package com.game.world;

import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.game.Component;
import com.game.System;
import com.game.entities.Entity;
import com.game.utils.exceptions.InvalidFieldException;
import lombok.RequiredArgsConstructor;

import java.util.*;

/**
 * {@link System} implementation that handles the logic of the "game world physics", i.e. gravity, collision handling,
 * and contact-event-handling.
 */
@RequiredArgsConstructor
public class WorldSystem extends System {

    private final List<BodyComponent> bodies = new ArrayList<>();
    private final Set<Contact> currentContacts = new HashSet<>();
    private final Set<Contact> priorContacts = new HashSet<>();
    private final WorldContactListener worldContactListener;
    private final float fixedTimeStep;
    private float accumulator;

    @Override
    public Set<Class<? extends Component>> getComponentMask() {
        return Set.of(BodyComponent.class);
    }

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
        bodies.clear();
    }

    @Override
    protected void processEntity(Entity entity, float delta) {
        BodyComponent bodyComponent = entity.getComponent(BodyComponent.class);
        bodies.add(bodyComponent);
        // Check that friction scalars are correct, throw exception if any are invalid
        Vector2 frictionScalar = bodyComponent.getFrictionScalarCopy();
        if (frictionScalar.x > 1f || frictionScalar.x < 0f) {
            throw new InvalidFieldException(String.valueOf(frictionScalar.x),
                                            "friction scalar x", "body component of " + entity);
        }
        if (frictionScalar.y > 1f || frictionScalar.y < 0f) {
            throw new InvalidFieldException(String.valueOf(frictionScalar.x),
                                            "friction scalar y", "body component of " + entity);
        }
        // Apply friction-scaled velocity to body
        float x = bodyComponent.getVelocity().x * frictionScalar.x;
        float y = bodyComponent.getVelocity().y * frictionScalar.y;
        // Apply friction-scaled impulse to body
        x += bodyComponent.getImpulse().x * frictionScalar.x;
        y += bodyComponent.getImpulse().y * frictionScalar.y;
        // Apply friction-scaled gravity to body
        x += bodyComponent.getGravity().x * frictionScalar.x;
        y += bodyComponent.getGravity().y * frictionScalar.y;
        // Scale x and y to delta and add values to Body Component collision box,
        // important to note that delta == fixedTimeStep
        bodyComponent.getCollisionBox().x += x * delta;
        bodyComponent.getCollisionBox().y += y * delta;
        // Each Fixture is moved to conform to its position center from the center of the Body Component
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
        // Reset impulse to zero
        bodies.forEach(bodyComponent -> bodyComponent.getImpulse().setZero());
        // Handle contacts
        Set<BodyComponent> staticBodies = new HashSet<>();
        Set<BodyComponent> dynamicBodies = new HashSet<>();
        for (int i = 0; i < bodies.size(); i++) {
            BodyComponent bc1 = bodies.get(i);
            if (bc1.getBodyType() == BodyType.STATIC) {
                staticBodies.add(bc1);
            } else if (bc1.getBodyType() == BodyType.DYNAMIC) {
                dynamicBodies.add(bc1);
            }
            for (int j = i + 1; j < bodies.size(); j++) {
                BodyComponent bc2 = bodies.get(j);
                for (Fixture f1 : bc1.getFixtures()) {
                    for (Fixture f2 : bc2.getFixtures()) {
                        if (Intersector.overlaps(f1.getFixtureBox(), f2.getFixtureBox())) {
                            currentContacts.add(new Contact(f1, f2));
                        }
                    }
                }
            }
        }
        // Handle collisions
        for (BodyComponent staticBC : staticBodies) {
            for (BodyComponent dynamicBC : dynamicBodies) {
                Rectangle overlap = new Rectangle();
                if (Intersector.intersectRectangles(dynamicBC.getCollisionBox(),
                                                    staticBC.getCollisionBox(), overlap)) {
                    handleCollision(dynamicBC, staticBC, overlap);
                }
            }
        }
        // Handles Contact instances in the current contacts set
        currentContacts.forEach(currentContact -> {
            if (!priorContacts.contains(currentContact)) {
                worldContactListener.beginContact(currentContact, delta);
            }
        });
        // Handles Contact instances in the prior contacts set
        priorContacts.forEach(priorContact -> {
            if (!currentContacts.contains(priorContact)) {
                worldContactListener.endContact(priorContact, delta);
            }
        });
        // Moves current contacts to prior, then clears the set of current contacts
        priorContacts.clear();
        priorContacts.addAll(currentContacts);
        currentContacts.clear();
    }

    private void handleCollision(BodyComponent bc1, BodyComponent bc2, Rectangle overlap) {
        if (overlap.getWidth() > overlap.getHeight()) {
            if (bc1.getCollisionBox().getY() > bc2.getCollisionBox().getY()) {
                bc1.getCollisionBox().y += overlap.getHeight();
            } else {
                bc1.getCollisionBox().y -= overlap.getHeight();
            }
        } else {
            if (bc1.getCollisionBox().getX() > bc2.getCollisionBox().getX()) {
                bc1.getCollisionBox().x += overlap.getWidth();
            } else {
                bc1.getCollisionBox().x -= overlap.getWidth();
            }
        }
    }

}
