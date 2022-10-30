package com.game.levels;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.game.GameContext2d;
import com.game.assets.TextureAsset;

import java.util.function.Supplier;

/** Draws the UI element for bits, e.g. health or weapon ammo. */
public class BitsBarUi {

    private final GameContext2d gameContext;
    private final Supplier<Integer> countSupplier;
    private final Supplier<TextureRegion> bitSupplier;
    private final Rectangle bounds;
    private final Vector2 bitSize;
    private final Sprite black = new Sprite();

    private boolean verticalStack;

    /**
     * Bit texture region is static. See {@link #BitsBarUi(GameContext2d, Supplier, Supplier, Vector2, Rectangle)}.
     *
     * @param gameContext the game context
     * @param countSupplier the count supplier
     * @param bit the bit
     * @param bitSize the bit size
     * @param bounds the bounds
     */
    public BitsBarUi(GameContext2d gameContext, Supplier<Integer> countSupplier, TextureRegion bit, Vector2 bitSize,
                     Rectangle bounds) {
        this(gameContext, countSupplier, () -> bit, bitSize, bounds, true);
    }

    /**
     * See {@link #BitsBarUi(GameContext2d, Supplier, Supplier, Vector2, Rectangle, boolean)}.
     *
     * @param gameContext the game context
     * @param countSupplier the count supplier
     * @param bitSupplier the bit supplier
     * @param bitSize the bit size
     * @param bounds the bounds
     */
    public BitsBarUi(GameContext2d gameContext, Supplier<Integer> countSupplier, Supplier<TextureRegion> bitSupplier,
                     Vector2 bitSize, Rectangle bounds) {
        this(gameContext, countSupplier, bitSupplier, bitSize, bounds, true);
    }

    /**
     * UI representation of bits, e.g. health or ammo. By default, the bits are drawn as a vertical stack from top
     * to bottom. However, setting the boolean parameter vertical stack to true will result in a horizontal stack
     * from left to right.
     *
     * @param gameContext the game context
     * @param countSupplier the count supplier
     * @param bitSupplier the bit supplier
     * @param bitSize the bit size
     * @param bounds the bounds
     * @param verticalStack if the bits should be stacked vertically or horizontally
     */
    public BitsBarUi(GameContext2d gameContext, Supplier<Integer> countSupplier, Supplier<TextureRegion> bitSupplier,
                     Vector2 bitSize, Rectangle bounds, boolean verticalStack) {
        this.gameContext = gameContext;
        this.countSupplier = countSupplier;
        this.bitSupplier = bitSupplier;
        this.bounds = bounds;
        this.bitSize = bitSize;
        this.verticalStack = verticalStack;
        TextureRegion blackRegion = gameContext.getAsset(TextureAsset.DECORATIONS.getSrc(), TextureAtlas.class)
                .findRegion("Black");
        black.setRegion(blackRegion);
        black.setBounds(bounds.x, bounds.y, bounds.width, bounds.height);
    }

    /** Draws the bits and the bounds as a black box. */
    public void draw() {
        int count = countSupplier.get();
        TextureRegion bit = bitSupplier.get();
        SpriteBatch spriteBatch = gameContext.getSpriteBatch();
        black.draw(spriteBatch);
        for (int i = 0; i < count; i++) {
            Sprite bitSprite = new Sprite(bit);
            if (verticalStack) {
                bitSprite.setBounds(bounds.x, bounds.y + (i * bitSize.y), bitSize.x, bitSize.y);
            } else {
                bitSprite.rotate(90f);
                bitSprite.setBounds(bounds.x + (i * bitSize.x), bounds.y, bitSize.x, bitSize.y);
            }
            bitSprite.draw(spriteBatch);
        }
    }

}
