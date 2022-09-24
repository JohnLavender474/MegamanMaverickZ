package com.game.sprites;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.game.utils.enums.Position;
import com.game.utils.interfaces.UpdatableConsumer;
import com.game.utils.objects.Wrapper;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;

@Getter
@Setter
@NoArgsConstructor
public class SpriteAdaptor implements SpriteProcessor {

    private Supplier<Float> alphaSupplier = () -> 1f;
    private Supplier<Float> offsetXSupplier = () -> 0f;
    private Supplier<Float> offsetYSupplier = () -> 0f;
    private Supplier<Float> rotationSupplier = () -> 1f;
    private UpdatableConsumer<Sprite> updatableConsumer;
    private Supplier<Boolean> flipYSupplier = () -> false;
    private Supplier<Boolean> flipXSupplier = () -> false;
    private Supplier<Boolean> hiddenSupplier = () -> false;
    private Supplier<Integer> renderPrioritySupplier = () -> 1;
    private Supplier<Vector2> sizeTransSupplier = () -> Vector2.Zero;
    private BiFunction<Wrapper<Rectangle>, Wrapper<Position>, Boolean> positioningFunc;
    private Function<Sprite, Vector2> originFunc = s -> new Vector2(s.getWidth() / 2f, s.getHeight() / 2f);

    @Override
    public int getSpriteRenderPriority() {
        return renderPrioritySupplier.get();
    }

    @Override
    public void update(Sprite sprite, float delta) {
        if (updatableConsumer == null) {
            return;
        }
        updatableConsumer.consumeAndUpdate(sprite, delta);
    }

    @Override
    public boolean setPositioning(Wrapper<Rectangle> bounds, Wrapper<Position> position) {
        if (positioningFunc == null) {
            return false;
        }
        return positioningFunc.apply(bounds, position);
    }

    @Override
    public Vector2 getSizeTrans() {
        return sizeTransSupplier.get();
    }

    @Override
    public Vector2 getOrigin(Sprite sprite) {
        return originFunc.apply(sprite);
    }

    @Override
    public float getAlpha() {
        return alphaSupplier.get();
    }

    @Override
    public float getRotation() {
        return rotationSupplier.get();
    }

    @Override
    public boolean isHidden() {
        return hiddenSupplier.get();
    }

    @Override
    public boolean isFlipX() {
        return flipXSupplier.get();
    }

    @Override
    public boolean isFlipY() {
        return flipYSupplier.get();
    }

    @Override
    public float getOffsetX() {
        return offsetXSupplier.get();
    }

    @Override
    public float getOffsetY() {
        return offsetYSupplier.get();
    }

}
