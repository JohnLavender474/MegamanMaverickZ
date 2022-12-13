package com.game.world;

import com.badlogic.gdx.math.*;
import com.game.entities.Entity;
import com.game.System;
import com.game.graph.Graph;
import com.game.graph.Node;
import com.game.levels.LevelTiledMap;
import com.game.shapes.custom.Triangle;
import com.game.utils.interfaces.Updatable;
import com.game.utils.objects.Pair;

import java.util.*;

import static com.badlogic.gdx.math.Intersector.intersectRectangles;
import static com.game.ViewVals.PPM;
import static com.game.utils.ShapeUtils.overlap;
import static java.lang.Math.*;

/**
 * {@link System} implementation that handles the logic pairOf the "game world physics", i.e. gravity, collision
 * handling, and contact-event-handling.
 */
public class WorldSystem extends System {

    private static final float MIN_VEL = .01f;

    private final Set<Contact> priorContacts = new HashSet<>();
    private final Set<Contact> currentContacts = new HashSet<>();
    private final List<BodyComponent> bodies = new ArrayList<>();
    private final List<Updatable> postProcess = new ArrayList<>();
    private final WorldContactListener worldContactListener;
    private final float fixedTimeStep;

    private Graph graph;
    private Vector2 airResistance;
    private float accumulator;

    public WorldSystem(WorldContactListener worldContactListener, Vector2 airResistance, float fixedTimeStep) {
        super(BodyComponent.class);
        this.airResistance = airResistance;
        this.fixedTimeStep = fixedTimeStep;
        this.worldContactListener = worldContactListener;
    }

    public void setWorldGraph(LevelTiledMap levelMap) {
        graph = new Graph(new Vector2(PPM, PPM), levelMap.getWidthInTiles(), levelMap.getHeightInTiles());
    }

    public void setAirResistance(Vector2 airResistance) {
        this.airResistance = airResistance;
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
        // bodyComponent.setPriorCollisionBoxToCurrent();
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
                Vector2 velocity = bodyComponent.getVelocity();
                // set velocity to zero if below threshold
                if (abs(velocity.x) < PPM * MIN_VEL) {
                    velocity.x = 0f;
                }
                if (abs(velocity.y) < PPM * MIN_VEL) {
                    velocity.y = 0f;
                }
                // apply resistance
                if (bodyComponent.isAffectedByResistance()) {
                    if (bodyComponent.getResistance().x != 0) {
                        velocity.x /= bodyComponent.getResistance().x;
                    }
                    if (bodyComponent.getResistance().y != 0) {
                        velocity.y /= bodyComponent.getResistance().y;
                    }
                }
                // reset resistance
                bodyComponent.setResistance(airResistance);
                // if gravity on, apply gravity
                if (bodyComponent.isGravityOn()) {
                    velocity.add(0f, bodyComponent.getGravity());
                }
                // clamp velocity
                Vector2 clamp = bodyComponent.getClamp();
                if (velocity.x > 0f && velocity.x > abs(clamp.x)) {
                    velocity.x = abs(clamp.x);
                } else if (velocity.x < 0f && velocity.x < -abs(clamp.x)) {
                    velocity.x = -abs(clamp.x);
                }
                if (velocity.y > 0f && velocity.y > abs(clamp.y)) {
                    velocity.y = abs(clamp.y);
                } else if (velocity.y < 0f && velocity.y < -abs(clamp.y)) {
                    velocity.y = -abs(clamp.y);
                }
                // translate
                bodyComponent.translate(velocity.x * fixedTimeStep, velocity.y * fixedTimeStep);
                // add body to graph
                graph.addObjToNodes(bodyComponent, bodyComponent.getCollisionBox());
                // set fixtures
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
                    // add fixture to graph if active
                    if (fixture.isActive()) {
                        graph.addObjToNodes(fixture, fixture.getFixtureShape());
                    }
                });
            });
            // handle collisions
            bodies.forEach(body -> getBodiesOverlapping(body).forEach((b, r) -> {
                if (intersectRectangles(body.getCollisionBox(), b.getCollisionBox(), r)) {
                    handleCollision(body, b, r);
                }
            }));
            // handle fixture contacts
            bodies.forEach(body -> body.getActiveFixtures().forEach(f1 ->
                    getFixturesOverlapping(f1).forEach(f2 -> currentContacts.add(new Contact(f1, f2)))));
            // clear graph node objs
            graph.clearNodeObjs();
        }
        // handle contacts in the current contacts setBounds
        currentContacts.forEach(contact -> {
            if (priorContacts.contains(contact)) {
                worldContactListener.continueContact(contact, delta);
            } else {
                worldContactListener.beginContact(contact, delta);
            }
        });
        // handle contacts in the prior contacts setBounds
        priorContacts.forEach(contact -> {
            if (!currentContacts.contains(contact)) {
                worldContactListener.endContact(contact, delta);
            }
        });
        // move current contacts to prior contacts setBounds, then clear the current contacts setBounds
        priorContacts.clear();
        priorContacts.addAll(currentContacts);
        currentContacts.clear();
        postProcess.forEach(postProcessable -> postProcessable.update(delta));
        bodies.forEach(BodyComponent::setPriorCollisionBoxToCurrent);
    }


    private Map<BodyComponent, Rectangle> getBodiesOverlapping(BodyComponent bodyComponent) {
        Pair<Pair<Integer>> indexes = graph.getNodeIndexes(bodyComponent.getCollisionBox());
        Pair<Integer> min = indexes.getFirst();
        Pair<Integer> max = indexes.getSecond();
        Map<BodyComponent, Rectangle> map = new HashMap<>();
        for (int i = min.getFirst(); i <= max.getFirst(); i++) {
            for (int j = min.getSecond(); j <= max.getSecond(); j++) {
                Node node = graph.getNode(i, j);
                node.getObjects().stream().filter(o -> o instanceof BodyComponent).forEach(o -> {
                    BodyComponent c = (BodyComponent) o;
                    Rectangle overlap = new Rectangle();
                    if (intersectRectangles(bodyComponent.getCollisionBox(), c.getCollisionBox(), overlap)) {
                        map.put(c, overlap);
                    }
                });
            }
        }
        return map;
    }

    private List<Fixture> getFixturesOverlapping(Fixture fixture) {
        Pair<Pair<Integer>> indexes = graph.getNodeIndexes(fixture.getFixtureShape());
        Pair<Integer> min = indexes.getFirst();
        Pair<Integer> max = indexes.getSecond();
        List<Fixture> fixtures = new ArrayList<>();
        for (int i = min.getFirst(); i <= max.getFirst(); i++) {
            for (int j = min.getSecond(); j <= max.getSecond(); j++) {
                Node node = graph.getNode(i, j);
                node.getObjects().stream().filter(o -> o instanceof Fixture).forEach(o -> {
                    Fixture f = (Fixture) o;
                    if (overlap(fixture.getFixtureShape(), f.getFixtureShape())) {
                        fixtures.add(f);
                    }
                });
            }
        }
        return fixtures;
    }

    /**
     * Handles collision between {@link BodyType#DYNAMIC} and {@link BodyType#STATIC} {@link BodyComponent}
     * instances
     * where parameter bc1 should be dynamic and bc2 static. Dynamic body is adjusted out pairOf collision and has
     * static
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
                if ((bc1.getBodyType() == BodyType.DYNAMIC && bc2.getBodyType() == BodyType.STATIC) ||
                        bc1.isMaskedForCollisionWith(bc2)) {
                    bc1.getCollisionBox().y += overlap.getHeight();
                } else if ((bc2.getBodyType() == BodyType.DYNAMIC && bc1.getBodyType() == BodyType.STATIC) ||
                        bc2.isMaskedForCollisionWith(bc1)) {
                    bc2.getCollisionBox().y -= overlap.getHeight();
                }
            } else {
                if (floor(bc1.getVelocity().y) > 1f) {
                    bc1.applyResistanceX(bc2.getFriction().x);
                }
                if (ceil(bc2.getVelocity().y) < -1f) {
                    bc2.applyResistanceX(bc1.getFriction().x);
                }
                if ((bc1.getBodyType() == BodyType.DYNAMIC && bc2.getBodyType() == BodyType.STATIC) ||
                        bc1.isMaskedForCollisionWith(bc2)) {
                    bc1.getCollisionBox().y -= overlap.getHeight();
                } else if ((bc2.getBodyType() == BodyType.DYNAMIC && bc1.getBodyType() == BodyType.STATIC) ||
                        bc2.isMaskedForCollisionWith(bc1)) {
                    bc2.getCollisionBox().y += overlap.getHeight();
                }
            }
        } else {
            if (bc1.getCollisionBox().getX() > bc2.getCollisionBox().getX()) {
                if ((bc1.getBodyType() == BodyType.DYNAMIC && bc2.getBodyType() == BodyType.STATIC) ||
                        bc1.isMaskedForCollisionWith(bc2)) {
                    bc1.getCollisionBox().x += overlap.getWidth();
                } else if ((bc2.getBodyType() == BodyType.DYNAMIC && bc1.getBodyType() == BodyType.STATIC) ||
                        bc2.isMaskedForCollisionWith(bc1)) {
                    bc2.getCollisionBox().x -= overlap.getWidth();
                }
            } else {
                if ((bc1.getBodyType() == BodyType.DYNAMIC && bc2.getBodyType() == BodyType.STATIC) ||
                        bc1.isMaskedForCollisionWith(bc2)) {
                    bc1.getCollisionBox().x -= overlap.getWidth();
                } else if ((bc2.getBodyType() == BodyType.DYNAMIC && bc1.getBodyType() == BodyType.STATIC) ||
                        bc2.isMaskedForCollisionWith(bc1)) {
                    bc2.getCollisionBox().x += overlap.getWidth();
                }
            }
        }
    }

}
