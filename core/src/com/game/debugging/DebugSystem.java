package com.game.debugging;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.Rectangle;
import com.game.System;
import com.game.core.IEntity;

import java.util.Set;

public class DebugSystem extends System {

    private final ShapeRenderer shapeRenderer;
    private final OrthographicCamera camera;
    private boolean isDrawing;

    public DebugSystem(ShapeRenderer shapeRenderer, OrthographicCamera camera) {
        super(Set.of(DebugComponent.class));
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
    protected void processEntity(IEntity entity, float delta) {
        DebugComponent debugComponent = entity.getComponent(DebugComponent.class);
        debugComponent.getDebugHandles().forEach(debugHandle -> {
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
