package com.game.debugging;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.Rectangle;
import com.game.Component;
import com.game.Entity;
import com.game.System;
import com.game.utils.KeyValuePair;
import lombok.RequiredArgsConstructor;

import java.util.Queue;
import java.util.Set;

@RequiredArgsConstructor
public class DebugSystem extends System {

    private final ShapeRenderer shapeRenderer;
    private final OrthographicCamera camera;
    private boolean isDrawing;

    @Override
    public Set<Class<? extends Component>> getComponentMask() {
        return Set.of(DebugComponent.class);
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
        DebugComponent debugComponent = entity.getComponent(DebugComponent.class);
        debugComponent.getDebugHandles().forEach(debugHandle -> {
            Rectangle rectangle = debugHandle.rectangleSupplier().get();
            Color color = debugHandle.color();
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
