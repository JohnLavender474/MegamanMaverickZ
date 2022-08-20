package com.game.debugging;

import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.Rectangle;
import com.game.core.Entity;
import com.game.core.System;

import java.util.Set;

public class DebugRectSystem extends System {

    private final ShapeRenderer shapeRenderer;
    private final Camera camera;
    private boolean isDrawing;

    public DebugRectSystem(Camera camera, ShapeRenderer shapeRenderer) {
        super(DebugRectComponent.class);
        this.shapeRenderer = shapeRenderer;
        this.camera = camera;
    }

    @Override
    protected void preProcess(float delta) {
        shapeRenderer.setProjectionMatrix(camera.combined);
        isDrawing = shapeRenderer.isDrawing();
        if (!isDrawing) {
            shapeRenderer.begin(ShapeType.Line);
        }
    }

    @Override
    protected void processEntity(Entity entity, float delta) {
        DebugRectComponent debugRectComponent = entity.getComponent(DebugRectComponent.class);
        debugRectComponent.getDebugHandles().forEach(debugHandle -> {
            Rectangle rectangle = debugHandle.key().get();
            Color color = debugHandle.value().get();
            shapeRenderer.setColor(color);
            shapeRenderer.rect(rectangle.x, rectangle.y, rectangle.width, rectangle.height);
        });
    }

    @Override
    protected void postProcess(float delta) {
        if (!isDrawing) {
            shapeRenderer.end();
        }
    }

}
