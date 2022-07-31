package com.game.entities.projectiles;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.game.Entity;
import com.game.GameContext2d;
import com.game.animations.AnimationComponent;
import com.game.animations.TimedAnimation;
import com.game.sounds.SoundComponent;
import com.game.sprites.SpriteAdapter;
import com.game.sprites.SpriteComponent;
import com.game.updatables.UpdatableComponent;
import com.game.utils.objects.Timer;

import static com.game.ConstVals.SoundAssets.*;
import static com.game.ConstVals.TextureAssets.MEGAMAN_CHARGED_SHOT_TEXTURE_ATLAS;
import static com.game.ConstVals.ViewVals.PPM;

public class ChargedShotDisintegration extends Entity {

    private final Timer timer = new Timer(.75f);
    private final Timer soundTimer = new Timer(.15f);

    public ChargedShotDisintegration(GameContext2d gameContext, Vector2 center, boolean isLeft) {
        addComponent(new SoundComponent());
        addComponent(defineUpdatableComponent());
        addComponent(defineSpriteComponent(center, isLeft));
        addComponent(defineAnimationComponent(gameContext));
    }

    private UpdatableComponent defineUpdatableComponent() {
        return new UpdatableComponent(delta -> {
            soundTimer.update(delta);
            if (soundTimer.isFinished()) {
                getComponent(SoundComponent.class).requestSound(ENEMY_DAMAGE_SOUND);
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

    private AnimationComponent defineAnimationComponent(GameContext2d gameContext) {
        TextureRegion textureRegion = gameContext.getAsset(MEGAMAN_CHARGED_SHOT_TEXTURE_ATLAS, TextureAtlas.class)
                .findRegion("MegamanChargedShotCollision");
        return new AnimationComponent(new TimedAnimation(textureRegion, 3, .05f));
    }

}
