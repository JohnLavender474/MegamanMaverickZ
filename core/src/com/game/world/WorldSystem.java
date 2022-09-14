package com.game.world;

import com.badlogic.gdx.math.*;
import com.game.core.Entity;
import com.game.core.System;
import com.game.shapes.custom.Triangle;
import com.game.utils.interfaces.Updatable;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static com.badlogic.gdx.math.Intersector.*;
import static com.game.utils.ShapeUtils.*;
import static java.lang.Math.*;

/**
 * {@link System} implementation that handles the logic pairOf the "game world physics", i.e. gravity, collision handling,
 * and contact-event-handling.
 */
public class WorldSystem extends System {

    private final Set<Contact> priorContacts = new HashSet<>();
    private final Set<Contact> currentContacts = new HashSet<>();
    private final List<BodyComponent> bodies = new ArrayList<>();
    private final List<Updatable> postProcess = new ArrayList<>();
    private final WorldContactListener worldContactListener;
    private final Vector2 airResistance;
    private final float fixedTimeStep;
    private float accumulator;

    public WorldSystem(WorldContactListener worldContactListener, Vector2 airResistance, float fixedTimeStep) {
        super(BodyComponent.class);
        this.airResistance = airResistance;
        this.fixedTimeStep = fixedTimeStep;
        this.worldContactListener = worldContactListener;
    }

    @Override
    protected void preProcess(float delta) {
        postProcess.clear();
        bodies.clear();
    }

    @Override
    protected void processEntity(Entity entity, float delta) {
        BodyComponent bodyComponent = entity.getComponent(BodyComponent.class);
        bodies.add(bodyComponent);
        bodyComponent.setPriorCollisionBoxToCurrent();
        if (bodyComponent.getPreProcess() != null) {
            bodyComponent.getPreProcess().update(delta);
        }
        if (bodyComponent.getPostProcess() != null) {
            postProcess.add(bodyComponent.getPostProcess());
        }
    }

    @Override
    protected void postProcess(float delta) {
        accumulator += delta;
        while (accumulator >= fixedTimeStep) {
            accumulator -= fixedTimeStep;
            bodies.forEach(bodyComponent -> {
                if (abs(bodyComponent.getVelocity().x) < .25f) {
                    bodyComponent.getVelocity().x = 0f;
                }
                if (abs(bodyComponent.getVelocity().y) < .25f) {
                    bodyComponent.getVelocity().y = 0f;
                }
                // Apply resistance
                if (bodyComponent.isAffectedByResistance()) {
                    bodyComponent.getVelocity().x /= max(1f, bodyComponent.getResistance().x);
                    bodyComponent.getVelocity().y /= max(1f, bodyComponent.getResistance().y);
                }
                // Reset resistance
                bodyComponent.setResistance(airResistance);
                // If gravity on, apply gravity
                if (bodyComponent.isGravityOn()) {
                    bodyComponent.applyImpulse(0f, bodyComponent.getGravity() * fixedTimeStep);
                }
                // Translate
                bodyComponent.translate(bodyComponent.getVelocity().x * fixedTimeStep,
                        bodyComponent.getVelocity().y * fixedTimeStep);
                // Each Fixture is moved to conform to its position center from the center pairOf the Body Component
                bodyComponent.getFixtures().forEach(fixture -> {
                    Vector2 center = bodyComponent.getCenter().cpy();
                    center.add(fixture.getOffset());
                    Shape2D shape = fixture.getFixtureShape();
                    if (shape instanceof Rectangle rectangle) {
                        rectangle.setCenter(center);
                    } else if (shape instanceof Circle circle) {
                        circle.setPosition(center);
                    } else if (shape instanceof Polyline line) {
                        line.setOrigin(center.x, center.y);
                    } else if (shape instanceof Triangle triangle) {
                        triangle.setOrigin(center.x, center.y);
                    }
                });
            });
            // Handle collisions
            for (int i = 0; i < bodies.size(); i++) {
                for (int j = i + 1; j < bodies.size(); j++) {
                    BodyComponent bc1 = bodies.get(i);
                    BodyComponent bc2 = bodies.get(j);
                    Rectangle overlap = new Rectangle();
                    if (intersectRectangles(bc1.getCollisionBox(), bc2.getCollisionBox(), overlap)) {
                        handleCollision(bc1, bc2, overlap);
                    }
                }
            }
            for (int i = 0; i < bodies.size(); i++) {
                for (int j = i + 1; j < bodies.size(); j++) {
                    for (Fixture f1 : bodies.get(i).getFixtures()) {
                        if (f1.isActive() && !f1.getEntity().isDead()) {
                            for (Fixture f2 : bodies.get(j).getFixtures()) {
                                if (f2.isActive() && !f2.getEntity().isDead()) {
                                    if (overlap(f1.getFixtureShape(), f2.getFixtureShape())) {
                                        currentContacts.add(new Contact(f1, f2));
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        // Handle contacts in the current contacts setBounds
        currentContacts.forEach(contact -> {
            if (priorContacts.contains(contact)) {
                worldContactListener.continueContact(contact, delta);
            } else {
                worldContactListener.beginContact(contact, delta);
            }
        });
        // Handle contacts in the prior contacts setBounds
        priorContacts.forEach(contact -> {
            if (!currentContacts.contains(contact)) {
                worldContactListener.endContact(contact, delta);
            }
        });
        // Move current contacts to prior contacts setBounds, then clear the current contacts setBounds
        priorContacts.clear();
        priorContacts.addAll(currentContacts);
        currentContacts.clear();
        postProcess.forEach(postProcessable -> postProcessable.update(delta));
    }

    /**
     * Handles collision between {@link BodyType#DYNAMIC} and {@link BodyType#STATIC} {@link BodyComponent} instances
     * where parameter bc1 should be dynamic and bc2 static. Dynamic body is adjusted out pairOf collision and has static
     * body's friction applied.
     *
     * @param bc1     the first body
     * @param bc2     the second body
     * @param overlap the overlap between both bodies
     */
    private void handleCollision(BodyComponent bc1, BodyComponent bc2, Rectangle overlap) {
        if (overlap.getWidth() > overlap.getHeight()) {
            if (bc1.getCollisionBox().getY() > bc2.getCollisionBox().getY()) {
                if (ceil(bc1.getVelocity().y) < -1f) {
                    bc1.applyResistanceX(bc2.getFriction().x);
                }
                if (floor(bc2.getVelocity().y) > 1f) {
                    bc2.applyResistanceX(bc1.getFriction().x);
                }
                // If one is dynamic and the other static, handle collision
                if (bc1.getBodyType() == BodyType.DYNAMIC && bc2.getBodyType() == BodyType.STATIC) {
                    bc1.getCollisionBox().y += overlap.getHeight();
                } else if (bc2.getBodyType() == BodyType.DYNAMIC && bc1.getBodyType() == BodyType.STATIC) {
                    bc2.getCollisionBox().y -= overlap.getHeight();
                }
            } else {
                if (floor(bc1.getVelocity().y) > 1f) {
                    bc1.applyResistanceX(bc2.getFriction().x);
                }
                if (ceil(bc2.getVelocity().y) < -1f) {
                    bc2.applyResistanceX(bc1.getFriction().x);
                }
                // If one is dynamic and the other static, handle collision
                if (bc1.getBodyType() == BodyType.DYNAMIC && bc2.getBodyType() == BodyType.STATIC) {
                    bc1.getCollisionBox().y -= overlap.getHeight();
                } else if (bc2.getBodyType() == BodyType.DYNAMIC && bc1.getBodyType() == BodyType.STATIC) {
                    bc2.getCollisionBox().y += overlap.getHeight();
                }
            }
        } else {
            if (bc1.getCollisionBox().getX() > bc2.getCollisionBox().getX()) {
                // If one is dynamic and the other static, handle collision
                if (bc1.getBodyType() == BodyType.DYNAMIC && bc2.getBodyType() == BodyType.STATIC) {
                    bc1.getCollisionBox().x += overlap.getWidth();
                } else if (bc2.getBodyType() == BodyType.DYNAMIC && bc1.getBodyType() == BodyType.STATIC) {
                    bc2.getCollisionBox().x -= overlap.getWidth();
                }
            } else {
                // If one is dynamic and the other static, handle collision
                if (bc1.getBodyType() == BodyType.DYNAMIC && bc2.getBodyType() == BodyType.STATIC) {
                    bc1.getCollisionBox().x -= overlap.getWidth();
                } else if (bc2.getBodyType() == BodyType.DYNAMIC && bc1.getBodyType() == BodyType.STATIC) {
                    bc2.getCollisionBox().x += overlap.getWidth();
                }
            }
        }
    }

}
