package com.game.world;

import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.game.Component;
import com.game.System;
import com.game.entities.Entity;
import com.game.utils.ProcessState;
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
    private final float fixedTimeStep;
    private float accumulator;

    @Override
    public Set<Class<? extends Component>> getComponentMask() {
        return Set.of(BodyComponent.class);
    }

    @Override
    protected void preProcess(float delta) {
        bodies.clear();
    }

    @Override
    protected void processEntity(Entity entity, float delta) {
        BodyComponent bodyComponent = entity.getComponent(BodyComponent.class);
        bodies.add(bodyComponent);
    }

    @Override
    protected void postProcess(float delta) {
        bodies.forEach(Collidable::resetCollisionFlags);
        accumulator += delta;
        while (accumulator >= fixedTimeStep) {
            accumulator -= fixedTimeStep;
            // Apply forces
            bodies.forEach(bodyComponent -> {
                Vector2 frictionScalar = bodyComponent.getFrictionScalarCopy();
                if (frictionScalar.x > 1f || frictionScalar.x < 0f) {
                    throw new InvalidFieldException(String.valueOf(frictionScalar.x),
                                                    "friction scalar x", "body component " + bodyComponent);
                }
                if (frictionScalar.y > 1f || frictionScalar.y < 0f) {
                    throw new InvalidFieldException(String.valueOf(frictionScalar.x),
                                                    "friction scalar y", "body component " + bodyComponent);
                }
                // Apply velocity, impulse, and gravity
                float x = bodyComponent.getVelocity().x + bodyComponent.getImpulse().x + bodyComponent.getGravity().x;
                float y = bodyComponent.getVelocity().y + bodyComponent.getImpulse().y + bodyComponent.getGravity().y;
                // Scale to fixed time step and friction scalar
                bodyComponent.getCollisionBox().x += x * fixedTimeStep * frictionScalar.x;
                bodyComponent.getCollisionBox().y += y * fixedTimeStep * frictionScalar.y;
                // Each Fixture is moved to conform to its position center from the center of the Body Component
                bodyComponent.getFixtures().forEach(fixture -> {
                    Vector2 center = new Vector2();
                    bodyComponent.getCollisionBox().getCenter(center);
                    center.add(fixture.getOffset());
                    fixture.getFixtureBox().setCenter(center);
                });
            });
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
            currentContacts.forEach(contact -> {
                if (priorContacts.contains(contact)) {
                    contact.run(ProcessState.CONTINUE);
                } else {
                    contact.run(ProcessState.BEGIN);
                }
            });
            // Handles Contact instances in the prior contacts set
            priorContacts.forEach(contact -> {
                if (!currentContacts.contains(contact)) {
                    contact.run(ProcessState.END);
                }
            });
            // Moves current contacts to prior, then clears the set of current contacts
            priorContacts.clear();
            priorContacts.addAll(currentContacts);
            currentContacts.clear();
        }
        bodies.forEach(bodyComponent -> {
            bodyComponent.getImpulse().setZero();
            bodyComponent.setFrictionScalar(1f, 1f);
        });
    }

    private void handleCollision(BodyComponent bc1, BodyComponent bc2, Rectangle overlap) {
        if (overlap.getWidth() > overlap.getHeight()) {
            if (bc1.getCollisionBox().getY() > bc2.getCollisionBox().getY()) {
                bc1.getCollisionBox().y += overlap.getHeight();
                bc1.setCollidingDown();
                bc2.setCollidingUp();
            } else {
                bc1.getCollisionBox().y -= overlap.getHeight();
                bc1.setCollidingUp();
                bc2.setCollidingDown();
            }
        } else {
            if (bc1.getCollisionBox().getX() > bc2.getCollisionBox().getX()) {
                bc1.getCollisionBox().x += overlap.getWidth();
                bc1.setCollidingLeft();
                bc2.setCollidingRight();
            } else {
                bc1.getCollisionBox().x -= overlap.getWidth();
                bc1.setCollidingRight();
                bc2.setCollidingLeft();
            }
        }
    }

}
