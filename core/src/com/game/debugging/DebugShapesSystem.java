package com.game.debugging;

import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Shape2D;
import com.game.core.Entity;
import com.game.core.System;
import com.game.utils.interfaces.UpdatableConsumer;

import static com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType.*;

public class DebugShapesSystem extends System {

    private final ShapeRenderer shapeRenderer;
    private final Camera camera;
    private boolean isDrawing;

    public DebugShapesSystem(Camera camera, ShapeRenderer shapeRenderer) {
        super(DebugShapesComponent.class);
        this.shapeRenderer = shapeRenderer;
        this.camera = camera;
    }

    @Override
    protected void preProcess(float delta) {
        shapeRenderer.setProjectionMatrix(camera.combined);
        isDrawing = shapeRenderer.isDrawing();
        if (!isDrawing) {
            shapeRenderer.begin(Line);
        }
    }

    @Override
    protected void processEntity(Entity entity, float delta) {
        DebugShapesComponent debugShapesComponent = entity.getComponent(DebugShapesComponent.class);
        debugShapesComponent.getDebugShapesHandles().forEach(debugShapeHandle -> {
            shapeRenderer.set(debugShapeHandle.getShapeType());
            shapeRenderer.setColor(debugShapeHandle.getColor());
            Shape2D shape = debugShapeHandle.getShape();
            UpdatableConsumer<Shape2D> updatableConsumer = debugShapeHandle.getUpdatableConsumer();
            updatableConsumer.consumeAndUpdate(shape, delta);
            if (shape instanceof Rectangle rectangle) {
                shapeRenderer.rect(rectangle.x, rectangle.y, rectangle.width, rectangle.height);
            } else if (shape instanceof Circle circle) {
                shapeRenderer.circle(circle.x, circle.y, circle.radius);
            }
        });
    }

    @Override
    protected void postProcess(float delta) {
        if (!isDrawing) {
            shapeRenderer.end();
        }
    }

}
