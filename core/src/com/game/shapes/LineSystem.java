package com.game.shapes;

import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.game.Entity;
import com.game.System;
import com.game.utils.objects.Pair;

import static com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType.*;

public class LineSystem extends System {

    private final Camera camera;
    private final ShapeRenderer shapeRenderer;

    public LineSystem(Camera camera, ShapeRenderer shapeRenderer) {
        super(LineComponent.class);
        this.camera = camera;
        this.shapeRenderer = shapeRenderer;
    }

    @Override
    protected void preProcess(float delta) {
        if (shapeRenderer.isDrawing()) {
            shapeRenderer.end();
        }
        shapeRenderer.setProjectionMatrix(camera.combined);
        shapeRenderer.begin(Line);
    }

    @Override
    protected void processEntity(Entity entity, float delta) {
        LineComponent lineComponent = entity.getComponent(LineComponent.class);
        lineComponent.getLineHandles().stream().filter(LineHandle::doRender).forEach(l -> {
            shapeRenderer.setColor(l.getColor());
            shapeRenderer.set(l.getShapeType());
            float thickness = l.getThickness();
            Pair<Vector2> line = l.getLine();
            shapeRenderer.rectLine(line.getFirst(), line.getSecond(), thickness);
        });
    }

    @Override
    protected void postProcess(float delta) {
        shapeRenderer.end();
    }

}
