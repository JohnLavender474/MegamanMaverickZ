package com.game.tests.entities;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.game.Entity;
import com.game.sprites.SpriteAdapter;
import com.game.sprites.SpriteComponent;
import com.game.updatables.UpdatableComponent;

import java.util.function.Predicate;
import java.util.function.Supplier;

public class TestDecorativeSprite extends Entity {

    public TestDecorativeSprite(TextureRegion textureRegion, Vector2 dimensions, Supplier<Vector2> posSupplier) {
        this(textureRegion, dimensions, posSupplier, delta -> false);
    }

    public TestDecorativeSprite(TextureRegion textureRegion, Vector2 dimensions,
                                Supplier<Vector2> posSupplier, Predicate<Float> isDead) {
        addComponent(defineSpriteComponent(textureRegion, dimensions, posSupplier));
        addComponent(defineUpdatableComponent(isDead));
    }

    private UpdatableComponent defineUpdatableComponent(Predicate<Float> isDead) {
        return new UpdatableComponent(delta -> {
            if (isDead.test(delta)) {
                setDead(true);
            }
        });
    }

    private SpriteComponent defineSpriteComponent(TextureRegion textureRegion, Vector2 dimensions,
                                                  Supplier<Vector2> posSupplier) {
        Sprite sprite = new Sprite(textureRegion);
        sprite.setSize(dimensions.x, dimensions.y);
        return new SpriteComponent(sprite, new SpriteAdapter() {
            @Override
            public void update(float delta) {
                Vector2 pos = posSupplier.get();
                sprite.setPosition(pos.x, pos.y);
            }
        });
    }

}
