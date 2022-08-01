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
import com.game.world.BodyComponent;
import com.game.world.BodyType;
import lombok.Getter;
import lombok.Setter;

import java.util.HashMap;
import java.util.Map;

import static com.game.ConstVals.TextureAsset.DECORATIONS_TEXTURE_ATLAS;
import static com.game.ConstVals.ViewVals.PPM;

@Getter
public class TestDisintegration implements IEntity {

    private final Map<Class<? extends Component>, Component> components = new HashMap<>();

    private TimedAnimation timedAnimation;
    @Setter
    private boolean dead;

    public TestDisintegration(IAssetLoader assetLoader, Vector2 center) {
        this(assetLoader, center, new Vector2(PPM, PPM));
    }

    public TestDisintegration(IAssetLoader assetLoader, Vector2 center, Vector2 size) {
        addComponent(defineBodyComponent(center));
        addComponent(defineAnimationComponent(assetLoader));
        addComponent(defineSpriteComponent(center, size));
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

    private SpriteComponent defineSpriteComponent(Vector2 center, Vector2 size) {
        Sprite sprite = new Sprite();
        sprite.setSize(size.x, size.y);
        sprite.setCenter(center.x, center.y);
        return new SpriteComponent(sprite);
    }

    private AnimationComponent defineAnimationComponent(IAssetLoader assetLoader) {
        timedAnimation = new TimedAnimation(assetLoader.getAsset(
                DECORATIONS_TEXTURE_ATLAS.getSrc(), TextureAtlas.class).findRegion("Disintegration"), 3, .1f);
        timedAnimation.setLoop(false);
        return new AnimationComponent(timedAnimation);
    }

    private UpdatableComponent defineUpdatableComponent() {
        return new UpdatableComponent(delta -> {
            if (timedAnimation.isFinished()) {
                setDead(true);
            }
        });
    }

}

