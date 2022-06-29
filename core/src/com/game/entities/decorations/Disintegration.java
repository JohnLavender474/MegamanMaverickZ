package com.game.entities.decorations;

import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.Vector2;
import com.game.Component;
import com.game.ConstVals.TextureAssets;
import com.game.Entity;
import com.game.GameContext2d;
import com.game.animations.AnimationComponent;
import com.game.animations.Animator;
import com.game.animations.TimedAnimation;
import com.game.sprites.SpriteComponent;
import com.game.updatables.UpdatableComponent;
import com.game.utils.Timer;
import com.game.world.BodyComponent;
import com.game.world.BodyType;
import lombok.Getter;
import lombok.Setter;

import java.util.HashMap;
import java.util.Map;

import static com.game.ConstVals.ViewVals.PPM;

@Getter
@Setter
public class Disintegration implements Entity {

    public static final float DISINTEGRATION_DURATION = 1f;

    private final Map<Class<? extends Component>, Component> components = new HashMap<>();
    private boolean dead;

    private final Timer timer = new Timer(DISINTEGRATION_DURATION);

    public Disintegration(GameContext2d gameContext, Vector2 center) {
        addComponent(new SpriteComponent());
        addComponent(defineUpdatableComponent());
        addComponent(defineBodyComponent(center));
        addComponent(defineSpriteComponent(center));
        addComponent(defineAnimationComponent(gameContext));
    }

    private BodyComponent defineBodyComponent(Vector2 center) {
        BodyComponent bodyComponent = new BodyComponent(BodyType.ABSTRACT);
        bodyComponent.setFriction(0f, 0f);
        bodyComponent.setSize(PPM, PPM);
        bodyComponent.setCenter(center);
        bodyComponent.setGravityOn(false);
        return bodyComponent;
    }

    private SpriteComponent defineSpriteComponent(Vector2 center) {
        SpriteComponent spriteComponent = new SpriteComponent();
        spriteComponent.getSprite().setSize(PPM, PPM);
        spriteComponent.getSprite().setCenter(center.x, center.y);
        return spriteComponent;
    }

    private AnimationComponent defineAnimationComponent(GameContext2d gameContext) {
        Map<String, TimedAnimation> animations = Map.of("Disintegration", new TimedAnimation(
                gameContext.getAsset(TextureAssets.DECORATIONS_TEXTURE_ATLAS, TextureAtlas.class).findRegion("Disintegration"), 3, 0.1f));
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
