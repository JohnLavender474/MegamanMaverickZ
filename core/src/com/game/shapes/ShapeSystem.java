package com.game.shapes;

import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.*;
import com.game.Entity;
import com.game.System;
import com.game.utils.interfaces.Updatable;
import com.game.utils.objects.Pair;

import java.util.EnumMap;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Queue;

import static com.badlogic.gdx.graphics.glutils.ShapeRenderer.*;
import static com.game.utils.ShapeUtils.*;

/** System implementation for rendering shapes. */
public class ShapeSystem extends System {

    private final Map<ShapeType, Queue<ShapeHandle>> shapeHandles = new EnumMap<>(ShapeType.class);
    private final ShapeRenderer shapeRenderer;
    private final Camera camera;

    /**
     * Instantiate with camera and shape renderer.
     *
     * @param camera the camera
     * @param shapeRenderer the shape renderer
     */
    public ShapeSystem(Camera camera, ShapeRenderer shapeRenderer) {
        super(ShapeComponent.class);
        this.shapeRenderer = shapeRenderer;
        this.camera = camera;
        for (ShapeType shapeType : ShapeType.values()) {
            shapeHandles.put(shapeType, new PriorityQueue<>());
        }
    }

    @Override
    protected void processEntity(Entity entity, float delta) {
        ShapeComponent shapeComponent = entity.getComponent(ShapeComponent.class);
        shapeComponent.getShapeHandles().stream().filter(ShapeHandle::doRender).forEach(s ->
            shapeHandles.get(s.getShapeType()).add(s));
    }

    @Override
    protected void postProcess(float delta) {
        shapeRenderer.setProjectionMatrix(camera.combined);
        if (shapeRenderer.isDrawing()) {
            shapeRenderer.end();
        }
        shapeHandles.forEach(((shapeType, shapeHandleQ) -> {
            shapeRenderer.begin(shapeType);
            while (!shapeHandleQ.isEmpty()) {
                ShapeHandle s = shapeHandleQ.poll();
                Updatable updatable = s.getUpdatable();
                if (updatable != null) {
                    updatable.update(delta);
                }
                Shape2D shape = s.getShape();
                if (shape == null) {
                    return;
                }
                shapeRenderer.set(s.getShapeType());
                shapeRenderer.setColor(s.getColor());
                if (shape instanceof Rectangle rectangle) {
                    shapeRenderer.rect(rectangle.x, rectangle.y, rectangle.width, rectangle.height);
                } else if (shape instanceof Circle circle) {
                    shapeRenderer.circle(circle.x, circle.y, circle.radius);
                } else if (shape instanceof Polyline line) {
                    Pair<Vector2> l = polylineToPointPair(line);
                    shapeRenderer.line(l.getFirst(), l.getSecond());
                }
            }
            shapeRenderer.end();
        }));
    }

}
