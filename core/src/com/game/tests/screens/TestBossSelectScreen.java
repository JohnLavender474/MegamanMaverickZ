package com.game.tests.screens;

import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.game.animations.TimedAnimation;
import com.game.tests.core.TestAssetLoader;
import com.game.updatables.Updatable;
import com.game.utils.interfaces.Drawable;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

import static com.badlogic.gdx.graphics.Texture.TextureFilter.*;
import static com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType.*;
import static com.game.ConstVals.TextureAsset.*;
import static com.game.ConstVals.ViewVals.*;
import static com.game.utils.UtilMethods.*;

public class TestBossSelectScreen extends ScreenAdapter {

    enum TestPaneDefStatus {
        BLINKING,
        HIGHLIGHTED,
        UNHIGHLIGHTED
    }

    @Getter
    @Setter
    private static class TestPaneDef implements Updatable, Drawable {

        private final Rectangle rect;
        private final TimedAnimation metAnimation;
        private final TimedAnimation paneBlinkingAnimation;
        private final TimedAnimation paneHighlightedAnimation;
        private final TimedAnimation paneUnhighlightedAnimation;

        private final Sprite metSprite = new Sprite();
        private final Sprite paneSprite = new Sprite();

        private TestPaneDefStatus status;

        public TestPaneDef(int x, int y, TestAssetLoader assetLoader) {
            // animations
            TextureRegion metRegion = assetLoader.getAsset(MET_TEXTURE_ATLAS.getSrc(), TextureAtlas.class)
                    .findRegion("Run");
            this.metAnimation = new TimedAnimation(metRegion, 2, .125f);
            TextureAtlas decorationAtlas = assetLoader.getAsset(DECORATIONS_TEXTURE_ATLAS.getSrc(), TextureAtlas.class);
            TextureRegion paneBlinking = decorationAtlas.findRegion("StageSelectBox_Blinking");
            this.paneBlinkingAnimation = new TimedAnimation(paneBlinking, 2, .125f);
            TextureRegion paneHighlighted = decorationAtlas.findRegion("StageSelectBox_Highlighted");
            this.paneHighlightedAnimation = new TimedAnimation(paneHighlighted);
            TextureRegion paneUnhighlighted = decorationAtlas.findRegion("StageSelectBox_Unhighlighted");
            this.paneUnhighlightedAnimation = new TimedAnimation(paneUnhighlighted);
            // rectangle
            rect = new Rectangle(x * BOSS_PANE_WIDTH * PPM, BOTTOM_OFFSET * PPM + y * BOSS_PANE_HEIGHT * PPM,
                    BOSS_PANE_WIDTH * PPM, BOSS_PANE_HEIGHT * PPM);
            Vector2 centerPoint = centerPoint(rect);
            // met sprite
            metSprite.setSize(BOSS_PANE_WIDTH * PPM, BOSS_PANE_HEIGHT * PPM);
            metSprite.setCenter(centerPoint.x, centerPoint.y + 15f);
            // pane border
            paneSprite.setSize(BOSS_PANE_WIDTH * PPM, BOSS_PANE_HEIGHT * PPM);
            paneSprite.setCenter(centerPoint.x, centerPoint.y);
        }

        @Override
        public void update(float delta) {
            metAnimation.update(delta);
            metSprite.setRegion(metAnimation.getCurrentT());
            TimedAnimation timedAnimation;
            switch (status) {
                case BLINKING -> timedAnimation = paneBlinkingAnimation;
                case HIGHLIGHTED -> timedAnimation = paneHighlightedAnimation;
                case UNHIGHLIGHTED -> timedAnimation = paneUnhighlightedAnimation;
                default -> throw new IllegalStateException();
            }
            timedAnimation.update(delta);
            paneSprite.setRegion(timedAnimation.getCurrentT());
        }

        @Override
        public void draw(SpriteBatch spriteBatch) {
            Texture paneTexture = paneSprite.getTexture();
            if (paneTexture != null) {
                paneTexture.setFilter(Nearest, Nearest);
            }
            paneSprite.draw(spriteBatch);
            Texture metTexture = metSprite.getTexture();
            if (metTexture != null) {
                metTexture.setFilter(Nearest, Nearest);
            }
            metSprite.draw(spriteBatch);
        }

        public void drawRect(ShapeRenderer shapeRenderer) {
            shapeRenderer.rect(rect.x, rect.y, rect.width, rect.height);
        }

    }

    private static final float BOSS_PANE_WIDTH = 5.33f;
    private static final float BOSS_PANE_HEIGHT = 4f;
    private static final float BOTTOM_OFFSET = 1f;

    private final Viewport viewport = new FitViewport(VIEW_WIDTH * PPM, VIEW_HEIGHT * PPM);
    private final List<TestPaneDef> testPaneDefs = new ArrayList<>();

    private SpriteBatch spriteBatch;
    private ShapeRenderer shapeRenderer;

    @Override
    public void show() {
        spriteBatch = new SpriteBatch();
        shapeRenderer = new ShapeRenderer();
        TestAssetLoader assetLoader = new TestAssetLoader();
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                testPaneDefs.add(new TestPaneDef(i, j, assetLoader));
            }
        }
        Vector3 camPos = viewport.getCamera().position;
        camPos.x = (VIEW_WIDTH * PPM) / 2f;
        camPos.y = (VIEW_HEIGHT * PPM) / 2f;
    }

    @Override
    public void render(float delta) {
        shapeRenderer.setProjectionMatrix(viewport.getCamera().combined);
        shapeRenderer.begin(Line);
        for (int i = 0; i < 9; i++) {
            testPaneDefs.get(i).drawRect(shapeRenderer);
        }
        shapeRenderer.end();
        spriteBatch.setProjectionMatrix(viewport.getCamera().combined);
        spriteBatch.begin();
        for (int i = 0; i < 9; i++) {
            testPaneDefs.get(i).update(delta);
            testPaneDefs.get(i).draw(spriteBatch);
        }
        spriteBatch.end();
        viewport.apply();
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height);
    }

    @Override
    public void dispose() {
        spriteBatch.dispose();
    }

}
