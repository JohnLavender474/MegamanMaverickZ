package com.game.world;

import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.game.Component;
import com.game.System;
import com.game.Entity;
import com.game.utils.Pair;
import com.game.utils.ProcessState;
import com.game.utils.Updatable;
import lombok.RequiredArgsConstructor;

import java.util.*;

import static com.game.utils.ProcessState.*;

/**
 * {@link System} implementation that handles the logic of the "game world physics", i.e. gravity, collision handling,
 * and contact-event-handling.
 */
@RequiredArgsConstructor
public class WorldSystem extends System {

    private final Set<Pair<Fixture>> currentContacts = new HashSet<>();
    private final Set<Pair<Fixture>> priorContacts = new HashSet<>();
    private final List<BodyComponent> bodies = new ArrayList<>();
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
        // Clear collision flags for each body
        bodies.forEach(bodyComponent -> {
            bodyComponent.clearCollisionFlags();
            Updatable preProcess = bodyComponent.getPreProcess();
            if (preProcess != null) {
                preProcess.update(delta);
            }
        });
        // ImpulseMovement and collision handling is time-stepped
        accumulator += delta;
        while (accumulator >= fixedTimeStep) {
            accumulator -= fixedTimeStep;
            // Apply forces
            bodies.forEach(bodyComponent -> {
                // Apply velocity, impulse, and gravity
                float x = bodyComponent.getImpulse().x + bodyComponent.getGravity().x;
                float y = bodyComponent.getImpulse().y + bodyComponent.getGravity().y;
                // Scale to fixed time step and friction scalar
                bodyComponent.getCollisionBox().x += x * fixedTimeStep * bodyComponent.getFrictionScalarCopy().x;
                bodyComponent.getCollisionBox().y += y * fixedTimeStep * bodyComponent.getFrictionScalarCopy().y;
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
                                currentContacts.add(new Pair<>(f1, f2));
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
        }
        // Handle contacts in the current contacts set
        currentContacts.forEach(contact -> {
            if (priorContacts.contains(contact)) {
                runContact(CONTINUE, contact, delta);
            } else {
                runContact(BEGIN, contact, delta);
            }
        });
        // Handle contacts in the prior contacts set
        priorContacts.forEach(contact -> {
            if (!currentContacts.contains(contact)) {
                runContact(END, contact, delta);
            }
        });
        // Move current contacts to prior contacts set, then clear the current contacts set
        priorContacts.clear();
        priorContacts.addAll(currentContacts);
        currentContacts.clear();
        bodies.forEach(bodyComponent -> {
            bodyComponent.getImpulse().setZero();
            bodyComponent.setFrictionScalar(1f, 1f);
            Updatable postProcess = bodyComponent.getPostProcess();
            if (postProcess != null) {
                postProcess.update(delta);
            }
        });
    }

    private void runContact(ProcessState processState, Pair<Fixture> contact, float delta) {
        // Fixture 1 listen to contact with Fixture 2
        ContactListener contactListener1 = contact.first().getContactListeners().get(processState);
        if (contactListener1 != null) {
            contactListener1.listen(contact.second().getFixtureType(), delta);
        }
        // Fixture 2 listen to contact with Fixture 1
        ContactListener contactListener2 = contact.second().getContactListeners().get(processState);
        if (contactListener2 != null) {
            contactListener2.listen(contact.first().getFixtureType(), delta);
        }
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
