package com.game.tests.entities;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.Vector2;
import com.game.Component;
import com.game.animations.AnimationComponent;
import com.game.animations.TimedAnimation;
import com.game.core.IAssetLoader;
import com.game.core.IEntity;
import com.game.sprites.SpriteComponent;
import com.game.updatables.UpdatableComponent;
import com.game.utils.objects.Timer;
import lombok.Getter;
import lombok.Setter;

import java.util.HashMap;
import java.util.Map;

import static com.game.ConstVals.TextureAssets.DECORATIONS_TEXTURE_ATLAS;
import static com.game.ConstVals.ViewVals.PPM;

@Getter
@Setter
public class TestExplosionOrb implements IEntity {

    private static final float DURATION = 4f;

    private final Map<Class<? extends Component>, Component> components = new HashMap<>();
    private final Timer cullTimer = new Timer(4f);
    private boolean dead;

    public TestExplosionOrb(IAssetLoader assetLoader, Vector2 spawn, Vector2 trajectory) {
        addComponent(defineUpdatableComponent(trajectory));
        addComponent(defineAnimationComponent(assetLoader));
        addComponent(defineSpriteComponent(spawn));
    }

    private UpdatableComponent defineUpdatableComponent(Vector2 trajectory) {
        return new UpdatableComponent(delta -> {
            getComponent(SpriteComponent.class).getSprite().translate(
                    trajectory.x * PPM * delta, trajectory.y * PPM * delta);
            cullTimer.update(delta);
            if (cullTimer.isFinished()) {
                setDead(true);
            }
        });
    }

    private SpriteComponent defineSpriteComponent(Vector2 spawn) {
        Sprite sprite = new Sprite();
        sprite.setSize(3f * PPM, 3f * PPM);
        sprite.setCenter(spawn.x, spawn.y);
        return new SpriteComponent(sprite);
    }

    private AnimationComponent defineAnimationComponent(IAssetLoader assetLoader) {
        TimedAnimation timedAnimation = new TimedAnimation(assetLoader.getAsset(
                DECORATIONS_TEXTURE_ATLAS, TextureAtlas.class).findRegion("PlayerExplosionOrbs"), 2, .075f);
        return new AnimationComponent(timedAnimation);
    }

}