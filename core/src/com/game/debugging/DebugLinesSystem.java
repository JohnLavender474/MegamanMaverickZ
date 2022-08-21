package com.game.debugging;

import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.game.core.Entity;
import com.game.core.System;

import static com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType.*;

public class DebugLinesSystem extends System {

    private final Camera camera;
    private final ShapeRenderer shapeRenderer;

    private boolean isDrawing;

    public DebugLinesSystem(Camera camera, ShapeRenderer shapeRenderer) {
        super(DebugLinesComponent.class);
        this.camera = camera;
        this.shapeRenderer = shapeRenderer;
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
        DebugLinesComponent debugLinesComponent = entity.getComponent(DebugLinesComponent.class);
        debugLinesComponent.getDebugLinesSupplierMap().forEach((points, colorSupplier) -> {
            shapeRenderer.setColor(colorSupplier.get());
            float thickness = debugLinesComponent.getThickness();
            shapeRenderer.set(debugLinesComponent.getShapeType());
            for (int i = 0; i < points.size() - 1; i++) {
                Vector2 p1 = points.get(i);
                Vector2 p2 = points.get(i + 1);
                shapeRenderer.rectLine(p1, p2, thickness);
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
