package com.game.test;

import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.game.GameContext2d;
import com.game.animations.TimedAnimation;
import com.game.utils.objects.KeyValuePair;
import lombok.RequiredArgsConstructor;

import static com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType.*;
import static com.game.ViewVals.*;
import static com.game.assets.TextureAsset.*;
import static com.game.sprites.RenderingGround.*;
import static com.game.utils.UtilMethods.*;

@RequiredArgsConstructor
public class TextureAssetTestScreen extends ScreenAdapter {

    private final GameContext2d gameContext;
    private final String textureAssetSrc;

    private final Rectangle background = new Rectangle(0f, 0f, VIEW_WIDTH * PPM, VIEW_HEIGHT * PPM);
    private final Color bkgdColor = Color.GRAY;

    private KeyValuePair<Sprite, TimedAnimation> megamanSpriteAnim;
    private KeyValuePair<Sprite, TimedAnimation> testSpriteAnim;

    @Override
    public void show() {
        // set Megaman sprite
        Sprite megamanSprite = new Sprite();
        megamanSprite.setBounds(PPM, PPM, 1.65f * PPM, 1.35f * PPM);
        megamanSpriteAnim = KeyValuePair.of(megamanSprite, new TimedAnimation(
                gameContext.getAsset(MEGAMAN.getSrc(), TextureAtlas.class).findRegion("Stand"),
                new float[]{1.5f, .15f}));
        // set test sprite
        Sprite sprite = new Sprite();
        sprite.setPosition(8f * PPM, PPM);
        TimedAnimation timedAnimation = null;
        if (textureAssetSrc.equals("Gear Trolley Platform")) {
            TextureRegion region = gameContext.getAsset(CUSTOM_TILES_1.getSrc(), TextureAtlas.class)
                    .findRegion("GearTrolleyPlatform");
            sprite.setSize(1.5f * PPM, 1.5f * PPM);
            timedAnimation = new TimedAnimation(region, 2, .15f);
        }
        if (timedAnimation == null) {
            throw new IllegalStateException("Timed anim cannot be null");
        }
        testSpriteAnim = KeyValuePair.of(sprite, timedAnimation);
    }

    @Override
    public void render(float delta) {
        super.render(delta);
        // update Megaman
        Sprite megamanSprite = megamanSpriteAnim.key();
        TimedAnimation megamanAnim = megamanSpriteAnim.value();
        megamanAnim.update(delta);
        megamanSprite.setRegion(megamanAnim.getCurrentT());
        // update test
        Sprite testSprite = testSpriteAnim.key();
        TimedAnimation testAnim = testSpriteAnim.value();
        testAnim.update(delta);
        testSprite.setRegion(testAnim.getCurrentT());
        // render background
        ShapeRenderer shapeRenderer = gameContext.getShapeRenderer();
        gameContext.setShapeRendererProjectionMatrix(UI);
        shapeRenderer.setColor(bkgdColor);
        shapeRenderer.begin(Filled);
        shapeRenderer.rect(background.x, background.y, background.width, background.height);
        shapeRenderer.end();
        // draw sprites
        SpriteBatch spriteBatch = gameContext.getSpriteBatch();
        gameContext.setSpriteBatchProjectionMatrix(UI);
        spriteBatch.begin();
        drawFiltered(megamanSprite, spriteBatch);
        drawFiltered(testSprite, spriteBatch);
        spriteBatch.end();
    }

}
