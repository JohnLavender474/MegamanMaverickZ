package com.game.entities.decorations;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.game.GameContext2d;
import com.game.animations.AnimationComponent;
import com.game.animations.TimedAnimation;
import com.game.entities.Entity;
import com.game.sprites.SpriteComponent;
import com.game.sprites.SpriteProcessor;
import com.game.updatables.UpdatableComponent;
import com.game.utils.objects.KeyValuePair;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static com.game.ViewVals.PPM;
import static com.game.assets.TextureAsset.WATER;
import static com.game.utils.UtilMethods.setBottomCenterToPoint;

public class WaterSplash extends Entity {

    public static List<WaterSplash> create(GameContext2d gameContext, Rectangle bounds) {
        int waterSplashes = (int) Math.ceil(bounds.width / PPM);
        List<Vector2> bottomCenterPositions = new ArrayList<>();
        for (int i = 0; i < waterSplashes; i++) {
            bottomCenterPositions.add(new Vector2(bounds.x + (PPM / 2f) + i * PPM, bounds.y));
        }
        return create(gameContext, bottomCenterPositions);
    }

    public static List<WaterSplash> create(GameContext2d gameContext, List<Vector2> bottomCenterPositions) {
        return bottomCenterPositions.stream().map(bottomCenterPos -> new WaterSplash(gameContext, bottomCenterPos))
                .collect(Collectors.toList());
    }

    public WaterSplash(GameContext2d gameContext, Vector2 bottomCenterPos) {
        super(gameContext);
        Rectangle bounds = new Rectangle();
        bounds.setSize(PPM, PPM);
        setBottomCenterToPoint(bounds, bottomCenterPos);
        addComponent(spriteComponent(bounds));
        KeyValuePair<AnimationComponent, TimedAnimation> animPair = animationComponent(gameContext);
        addComponent(animPair.key());
        addComponent(updatableComponent(animPair.value()));
    }

    protected UpdatableComponent updatableComponent(TimedAnimation splashAnimation) {
        return new UpdatableComponent(delta -> {
            if (splashAnimation.isFinished()) {
                setDead(true);
            }
        });
    }

    protected SpriteComponent spriteComponent(Rectangle bounds) {
        Sprite sprite = new Sprite();
        sprite.setBounds(bounds.x, bounds.y, bounds.width, bounds.height);
        return new SpriteComponent(sprite, new SpriteProcessor() {

            @Override
            public int getSpriteRenderPriority() {
                return -1;
            }

            @Override
            public float getAlpha() {
                return .35f;
            }

        });
    }

    protected KeyValuePair<AnimationComponent, TimedAnimation> animationComponent(GameContext2d gameContext) {
        TextureRegion splashRegion = gameContext.getAsset(WATER.getSrc(), TextureAtlas.class).findRegion("Splash");
        TimedAnimation splashAnimation = new TimedAnimation(splashRegion, 5, .075f, false);
        return KeyValuePair.of(new AnimationComponent(splashAnimation), splashAnimation);
    }

}
