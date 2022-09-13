package com.game.debugging;

import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.*;
import com.game.core.Entity;
import com.game.core.System;
import com.game.utils.ShapeUtils;
import com.game.utils.interfaces.UpdatableConsumer;
import com.game.utils.objects.Pair;

public class DebugShapesSystem extends System {

    private final ShapeRenderer shapeRenderer;
    private final Camera camera;

    public DebugShapesSystem(Camera camera, ShapeRenderer shapeRenderer) {
        super(DebugShapesComponent.class);
        this.shapeRenderer = shapeRenderer;
        this.camera = camera;
    }

    @Override
    protected void preProcess(float delta) {
        shapeRenderer.setProjectionMatrix(camera.combined);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
    }

    @Override
    protected void processEntity(Entity entity, float delta) {
        DebugShapesComponent debugShapesComponent = entity.getComponent(DebugShapesComponent.class);
        debugShapesComponent.getDebugShapesHandles().forEach(debugShapeHandle -> {
            Shape2D shape = debugShapeHandle.getShape().get();
            if (shape == null) {
                return;
            }
            shapeRenderer.set(debugShapeHandle.getShapeType());
            shapeRenderer.setColor(debugShapeHandle.getColor());
            UpdatableConsumer<Shape2D> updatableConsumer = debugShapeHandle.getUpdatableConsumer();
            updatableConsumer.consumeAndUpdate(shape, delta);
            if (shape instanceof Rectangle rectangle) {
                shapeRenderer.rect(rectangle.x, rectangle.y, rectangle.width, rectangle.height);
            } else if (shape instanceof Circle circle) {
                shapeRenderer.circle(circle.x, circle.y, circle.radius);
            } else if (shape instanceof Polyline line) {
                Pair<Vector2> l = ShapeUtils.polylineToPointPair(line);
                shapeRenderer.line(l.getFirst(), l.getSecond());
            }
        });
    }

    @Override
    protected void postProcess(float delta) {
        shapeRenderer.end();
    }

}
