package com.game.entities.decorations;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.game.entities.Entity;
import com.game.GameContext2d;
import com.game.sprites.SpriteProcessor;
import com.game.sprites.SpriteComponent;
import com.game.updatables.UpdatableComponent;

import java.util.function.Predicate;
import java.util.function.Supplier;

public class DecorativeSprite extends Entity {

    public DecorativeSprite(GameContext2d gameContext, TextureRegion textureRegion, Vector2 dimensions,
                            SpriteProcessor spriteProcessor, Predicate<Float> isDead) {
        super(gameContext);
        addComponent(spriteComponent(textureRegion, dimensions, spriteProcessor));
        addComponent(updatableComponent(isDead));
    }

    public DecorativeSprite(GameContext2d gameContext, TextureRegion textureRegion, Vector2 dimensions,
                            Supplier<Vector2> posSupplier, Predicate<Float> isDead) {
        this(gameContext, textureRegion, dimensions, new SpriteProcessor() {
            @Override
            public void update(Sprite sprite, float delta) {
                Vector2 pos = posSupplier.get();
                sprite.setPosition(pos.x, pos.y);
            }
        }, isDead);
    }

    public DecorativeSprite(GameContext2d gameContext, TextureRegion textureRegion, Vector2 dimensions,
                            Supplier<Vector2> centerSupplier) {
        this(gameContext, textureRegion, dimensions, centerSupplier, delta -> false);
    }

    private UpdatableComponent updatableComponent(Predicate<Float> isDead) {
        return new UpdatableComponent(delta -> {
            if (isDead.test(delta)) {
                setDead(true);
            }
        });
    }

    private SpriteComponent spriteComponent(TextureRegion textureRegion, Vector2 dimensions,
                                                  SpriteProcessor spriteProcessor) {
        Sprite sprite = new Sprite(textureRegion);
        sprite.setSize(dimensions.x, dimensions.y);
        return new SpriteComponent(sprite, spriteProcessor);
    }


}
