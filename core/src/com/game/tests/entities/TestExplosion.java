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
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

import java.util.HashMap;
import java.util.Map;

import static com.game.ConstVals.TextureAssets.DECORATIONS_TEXTURE_ATLAS;
import static com.game.ConstVals.ViewVals.PPM;

@Getter
@Setter
public class TestExplosion implements IEntity {

    private final Map<Class<? extends Component>, Component> components = new HashMap<>();
    private boolean dead;

    @Getter(AccessLevel.NONE)
    @Setter(AccessLevel.NONE)
    private TimedAnimation timedAnimation;

    public TestExplosion(IAssetLoader assetLoader, Vector2 center) {
        addComponent(defineSpriteComponent(center));
        addComponent(defineAnimationComponent(assetLoader));
        addComponent(defineUpdatableComponent());
    }

    private SpriteComponent defineSpriteComponent(Vector2 center) {
        Sprite sprite = new Sprite();
        sprite.setSize(2.5f * PPM, 2.5f * PPM);
        sprite.setCenter(center.x, center.y);
        return new SpriteComponent(sprite);
    }

    private AnimationComponent defineAnimationComponent(IAssetLoader assetLoader) {
        timedAnimation = new TimedAnimation(assetLoader.getAsset(DECORATIONS_TEXTURE_ATLAS, TextureAtlas.class)
                .findRegion("Explosion"), 11, .025f);
        timedAnimation.setLoop(false);
        return new AnimationComponent(new Animator(() -> "explosion", Map.of("explosion", timedAnimation)));
    }

    private UpdatableComponent defineUpdatableComponent() {
        return new UpdatableComponent(delta -> {
            if (timedAnimation.isFinished()) {
                setDead(true);
            }
        });
    }

}
