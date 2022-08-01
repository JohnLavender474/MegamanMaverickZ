package com.game.tests.entities;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.game.Entity;
import com.game.animations.AnimationComponent;
import com.game.animations.TimedAnimation;
import com.game.core.IAssetLoader;
import com.game.sprites.SpriteAdapter;
import com.game.sprites.SpriteComponent;
import com.game.updatables.UpdatableComponent;
import com.game.utils.objects.Timer;

import static com.game.ConstVals.TextureAsset.MEGAMAN_CHARGED_SHOT_TEXTURE_ATLAS;
import static com.game.ConstVals.ViewVals.PPM;

public class TestChargedShotDisintegration extends Entity {

    private final Timer timer = new Timer(.75f);
    private final Timer soundTimer = new Timer(.15f);

    public TestChargedShotDisintegration(IAssetLoader assetLoader, Vector2 center, boolean isLeft) {
        addComponent(defineUpdatableComponent());
        addComponent(defineSpriteComponent(center, isLeft));
        addComponent(defineAnimationComponent(assetLoader));
    }

    private UpdatableComponent defineUpdatableComponent() {
        return new UpdatableComponent(delta -> {
            soundTimer.update(delta);
            if (soundTimer.isFinished()) {
                Gdx.audio.newSound(Gdx.files.internal("sounds/EnemyDamage.mp3")).play();
                soundTimer.reset();
            }
            timer.update(delta);
            if (timer.isFinished()) {
                setDead(true);
            }
        });
    }

    private SpriteComponent defineSpriteComponent(Vector2 center, boolean isLeft) {
        Sprite sprite = new Sprite();
        sprite.setSize(1.75f * PPM, 1.75f * PPM);
        sprite.setCenter(center.x, center.y);
        return new SpriteComponent(sprite, new SpriteAdapter() {
            @Override
            public boolean isFlipX() {
                return isLeft;
            }
        });
    }

    private AnimationComponent defineAnimationComponent(IAssetLoader assetLoader) {
        TextureRegion textureRegion = assetLoader.getAsset(
                        MEGAMAN_CHARGED_SHOT_TEXTURE_ATLAS.getSrc(), TextureAtlas.class)
                .findRegion("MegamanChargedShotCollision");
        return new AnimationComponent(new TimedAnimation(textureRegion, 3, .05f));
    }

}
