package com.game.entities.decorations;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.game.core.Entity;
import com.game.sprites.SpriteAdapter;
import com.game.sprites.SpriteComponent;
import com.game.updatables.UpdatableComponent;

import java.util.function.Predicate;
import java.util.function.Supplier;

public class DecorativeSprite extends Entity {

    public DecorativeSprite(TextureRegion textureRegion, Vector2 dimensions, SpriteAdapter spriteAdapter,
                            Predicate<Float> isDead) {
        addComponent(defineSpriteComponent(textureRegion, dimensions, spriteAdapter));
        addComponent(defineUpdatableComponent(isDead));
    }

    public DecorativeSprite(TextureRegion textureRegion, Vector2 dimensions,
                            Supplier<Vector2> centerSupplier, Predicate<Float> isDead) {
        this(textureRegion, dimensions, new SpriteAdapter() {
            @Override
            public void update(Sprite sprite, float delta) {
                Vector2 center = centerSupplier.get();
                sprite.setCenter(center.x, center.y);
            }
        }, isDead);
    }

    public DecorativeSprite(TextureRegion textureRegion, Vector2 dimensions, SpriteAdapter spriteAdapter) {
        this(textureRegion, dimensions, spriteAdapter, delta -> false);
    }

    public DecorativeSprite(TextureRegion textureRegion, Vector2 dimensions, Supplier<Vector2> centerSupplier) {
        this(textureRegion, dimensions, centerSupplier, delta -> false);
    }

    private UpdatableComponent defineUpdatableComponent(Predicate<Float> isDead) {
        return new UpdatableComponent(delta -> {
            if (isDead.test(delta)) {
                setDead(true);
            }
        });
    }

    private SpriteComponent defineSpriteComponent(TextureRegion textureRegion, Vector2 dimensions,
                                                  SpriteAdapter spriteAdapter) {
        Sprite sprite = new Sprite(textureRegion);
        sprite.setSize(dimensions.x, dimensions.y);
        return new SpriteComponent(sprite, spriteAdapter);
    }


}
