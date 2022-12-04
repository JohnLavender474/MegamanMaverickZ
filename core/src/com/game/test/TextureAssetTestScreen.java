package com.game.test;

import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.game.GameContext2d;
import com.game.animations.TimedAnimation;
import com.game.assets.TextureAsset;
import com.game.utils.objects.KeyValuePair;
import lombok.RequiredArgsConstructor;

import static com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType.*;
import static com.game.ViewVals.*;
import static com.game.assets.TextureAsset.MEGAMAN;
import static com.game.sprites.RenderingGround.*;
import static com.game.utils.UtilMethods.*;

@RequiredArgsConstructor
public class TextureAssetTestScreen extends ScreenAdapter {

    private final GameContext2d gameContext;
    private final TextureAsset textureAsset;

    private final Rectangle background = new Rectangle(0f, 0f, VIEW_WIDTH * PPM, VIEW_HEIGHT * PPM);
    private final Color bkgdColor = Color.GRAY;

    private TextureAtlas megamanAtlas;
    private TextureAtlas testAtlas;

    private KeyValuePair<Sprite, TimedAnimation> megamanSpriteAnim;
    private KeyValuePair<Sprite, TimedAnimation> testSpriteAnim;

    @Override
    public void show() {
        megamanAtlas = gameContext.getAsset(MEGAMAN.getSrc(), TextureAtlas.class);
        testAtlas = gameContext.getAsset(textureAsset.getSrc(), TextureAtlas.class);

        Sprite megamanSprite = new Sprite();
        megamanSprite.setBounds(PPM, PPM, 1.65f * PPM, 1.35f * PPM);
        megamanSpriteAnim = KeyValuePair.of(megamanSprite,
                new TimedAnimation(megamanAtlas.findRegion("Run"), 4, .125f));

        Sprite sprite = new Sprite();
        sprite.setPosition(8f * PPM, PPM);
        TimedAnimation timedAnimation = null;
        switch (textureAsset) {
            case TIMBER_WOMAN -> {
                sprite.setSize(2.75f * PPM, 2.35f * PPM);
                timedAnimation = new TimedAnimation(testAtlas.findRegion("Stand"), new float[]{1.5f, .15f});
            }
            case DISTRIBUTOR_MAN -> {
                sprite.setSize(1.85f * PPM, 1.5f * PPM);
                timedAnimation = new TimedAnimation(testAtlas.findRegion("Stand"), new float[]{1.5f, .15f});
            }
            case ROASTER_MAN -> {
                sprite.setSize(3f * PPM, 2.85f * PPM);
                // timedAnimation = new TimedAnimation(testAtlas.findRegion("Stand"), new float[]{1.5f, .15f});
                // timedAnimation = new TimedAnimation(testAtlas.findRegion("StandFlap"), 2, .2f);
                // timedAnimation = new TimedAnimation(testAtlas.findRegion("FlyFlap"), 2, .2f);
                timedAnimation = new TimedAnimation(testAtlas.findRegion("FallingWithStyle"), 2, .1f);
            }
            case MISTER_MAN -> {
                sprite.setSize(3.25f * PPM, 2.85f * PPM);
                // timedAnimation = new TimedAnimation(testAtlas.findRegion("Stand"), new float[]{1.5f, .15f});
                timedAnimation = new TimedAnimation(testAtlas.findRegion("Squirt"), 2, .2f);
            }
        }
        if (timedAnimation == null) {
            throw new IllegalStateException("Timed anim cannot be null");
        }
        testSpriteAnim = KeyValuePair.of(sprite, timedAnimation);
    }

    @Override
    public void render(float delta) {
        super.render(delta);
        // Megaman update
        Sprite megamanSprite = megamanSpriteAnim.key();
        TimedAnimation megamanAnim = megamanSpriteAnim.value();
        megamanAnim.update(delta);
        megamanSprite.setRegion(megamanAnim.getCurrentT());
        // Test update
        Sprite testSprite = testSpriteAnim.key();
        TimedAnimation testAnim = testSpriteAnim.value();
        testAnim.update(delta);
        testSprite.setRegion(testAnim.getCurrentT());
        // Render background
        ShapeRenderer shapeRenderer = gameContext.getShapeRenderer();
        gameContext.setShapeRendererProjectionMatrix(UI);
        shapeRenderer.setColor(bkgdColor);
        shapeRenderer.begin(Filled);
        shapeRenderer.rect(background.x, background.y, background.width, background.height);
        shapeRenderer.end();
        // Draw sprites
        SpriteBatch spriteBatch = gameContext.getSpriteBatch();
        gameContext.setSpriteBatchProjectionMatrix(UI);
        spriteBatch.begin();
        drawFiltered(megamanSprite, spriteBatch);
        drawFiltered(testSprite, spriteBatch);
        spriteBatch.end();
    }

}
