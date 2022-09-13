package com.game.screens;

import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.game.core.GameContext2d;
import com.game.core.constants.RenderingGround;
import com.game.core.constants.ViewVals;
import com.game.utils.objects.RotatingLine;

import static com.game.core.constants.ViewVals.PPM;

public class TestScreen extends ScreenAdapter {

    private final GameContext2d gameContext;

    private RotatingLine rotatingLine;

    public TestScreen(GameContext2d gameContext) {
        this.gameContext = gameContext;
    }

    @Override
    public void show() {
        Vector2 origin = new Vector2(ViewVals.VIEW_WIDTH * PPM / 2f, ViewVals.VIEW_HEIGHT * PPM / 2f);
        rotatingLine = new RotatingLine(origin, 3f * PPM, 20f);
    }

    @Override
    public void render(float delta) {
        rotatingLine.update(delta);
        gameContext.setShapeRendererProjectionMatrix(RenderingGround.UI);
        ShapeRenderer shapeRenderer = gameContext.getShapeRenderer();
        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        shapeRenderer.line(rotatingLine.getPos(), rotatingLine.getEndPoint());
        shapeRenderer.end();
    }

}
