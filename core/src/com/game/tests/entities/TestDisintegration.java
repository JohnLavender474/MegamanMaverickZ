package com.game.tests.entities;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.Vector2;
import com.game.Component;
import com.game.animations.AnimationComponent;
import com.game.animations.Animator;
import com.game.animations.TimedAnimation;
import com.game.core.IAssetLoader;
import com.game.core.IEntity;
import com.game.sprites.SpriteComponent;
import com.game.updatables.UpdatableComponent;
import com.game.utils.Timer;
import com.game.world.BodyComponent;
import com.game.world.BodyType;
import lombok.Getter;
import lombok.Setter;

import java.util.HashMap;
import java.util.Map;

import static com.game.ConstVals.TextureAssets.*;
import static com.game.ConstVals.ViewVals.PPM;

@Getter
@Setter
public class TestDisintegration implements IEntity {

    public static final float DISINTEGRATION_DURATION = .15f;

    private final Map<Class<? extends Component>, Component> components = new HashMap<>();
    private final Timer timer = new Timer(DISINTEGRATION_DURATION);
    private boolean dead;

    public TestDisintegration(IAssetLoader assetLoader, Vector2 center) {
        addComponent(defineBodyComponent(center));
        addComponent(defineAnimationComponent(assetLoader));
        addComponent(defineSpriteComponent(center));
        addComponent(defineUpdatableComponent());
    }

    private BodyComponent defineBodyComponent(Vector2 center) {
        BodyComponent bodyComponent = new BodyComponent(BodyType.ABSTRACT);
        bodyComponent.setSize(PPM, PPM);
        bodyComponent.setCenter(center);
        bodyComponent.setGravityOn(false);
        bodyComponent.setFriction(0f, 0f);
        return bodyComponent;
    }

    private SpriteComponent defineSpriteComponent(Vector2 center) {
        Sprite sprite = new Sprite();
        sprite.setSize(PPM, PPM);
        sprite.setCenter(center.x, center.y);
        return new SpriteComponent(sprite);
    }

    private AnimationComponent defineAnimationComponent(IAssetLoader assetLoader) {
        Map<String, TimedAnimation> animations = Map.of("Disintegration", new TimedAnimation(
                assetLoader.getAsset(DECORATIONS_TEXTURE_ATLAS, TextureAtlas.class).findRegion(
                        "Disintegration"), 3, .1f));
        Animator animator = new Animator(() -> "Disintegration", animations);
        return new AnimationComponent(animator);
    }

    private UpdatableComponent defineUpdatableComponent() {
        return new UpdatableComponent(delta -> {
            timer.update(delta);
            if (timer.isFinished()) {
                setDead(true);
            }
        });
    }

}

