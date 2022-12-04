package com.game.menus.impl.bosses;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.game.GameContext2d;
import com.game.animations.TimedAnimation;
import com.game.entities.bosses.BossEnum;
import com.game.utils.enums.Position;
import com.game.utils.interfaces.Drawable;
import com.game.utils.interfaces.Updatable;
import lombok.Getter;
import lombok.Setter;

import java.util.function.Supplier;

import static com.badlogic.gdx.graphics.Texture.TextureFilter.Nearest;
import static com.game.ViewVals.PPM;
import static com.game.assets.TextureAsset.BOSS_FACES;
import static com.game.assets.TextureAsset.STAGE_SELECT;
import static com.game.menus.impl.bosses.BossPaneStatus.*;

@Getter
@Setter
class BossPane implements Updatable, Drawable {

    public static final float PANE_BOUNDS_WIDTH = 5.33f;
    public static final float PANE_BOUNDS_HEIGHT = 4f;
    public static final float BOTTOM_OFFSET = 1.5f;
    public static final float SPRITE_HEIGHT = 2f;
    public static final float SPRITE_WIDTH = 2.5f;
    public static final float PANE_HEIGHT = 3f;
    public static final float PANE_WIDTH = 4f;

    private final String bossName;
    private final Supplier<TextureRegion> bossRegionSupplier;
    private final Sprite bossSprite = new Sprite();
    private final Sprite paneSprite = new Sprite();
    private final TimedAnimation paneBlinkingAnimation;
    private final TimedAnimation paneHighlightedAnimation;
    private final TimedAnimation paneUnhighlightedAnimation;

    private BossPaneStatus bossPaneStatus = UNHIGHLIGHTED;

    public BossPane(GameContext2d gameContext, BossEnum bossEnum) {
        this(gameContext, gameContext.getAsset(BOSS_FACES.getSrc(),
                        TextureAtlas.class).findRegion(bossEnum.getBossName()),
                bossEnum.getBossName(), bossEnum.getPosition());
    }

    public BossPane(GameContext2d gameContext, TextureRegion bossRegion, String bossName, Position position) {
        this(gameContext, bossRegion, bossName, position.getX(), position.getY());
    }

    public BossPane(GameContext2d gameContext, TextureRegion bossRegion, String bossName, int x, int y) {
        this(gameContext, () -> bossRegion, bossName, x, y);
    }

    public BossPane(GameContext2d gameContext2d, Supplier<TextureRegion> bossRegionSupplier,
                    String bossName, Position position) {
        this(gameContext2d, bossRegionSupplier, bossName, position.getX(), position.getY());
    }

    public BossPane(GameContext2d gameContext, Supplier<TextureRegion> bossRegionSupplier,
                    String bossName, int x, int y) {
        this.bossName = bossName;
        this.bossRegionSupplier = bossRegionSupplier;
        // center
        float centerX =
                (x * PANE_BOUNDS_WIDTH * PPM) + (PANE_BOUNDS_WIDTH * PPM / 2f);
        float centerY = (BOTTOM_OFFSET * PPM + y * PANE_BOUNDS_HEIGHT * PPM) + (PANE_BOUNDS_HEIGHT * PPM / 2f);
        // boss sprite
        bossSprite.setSize(SPRITE_WIDTH * PPM, SPRITE_HEIGHT * PPM);
        bossSprite.setCenter(centerX, centerY);
        // pane sprite
        paneSprite.setSize(PANE_WIDTH * PPM, PANE_HEIGHT * PPM);
        paneSprite.setCenter(centerX, centerY);
        // pane animations
        TextureAtlas decorationAtlas = gameContext.getAsset(STAGE_SELECT.getSrc(), TextureAtlas.class);
        TextureRegion paneUnhighlighted = decorationAtlas.findRegion("Pane");
        this.paneUnhighlightedAnimation = new TimedAnimation(paneUnhighlighted);
        TextureRegion paneBlinking = decorationAtlas.findRegion("PaneBlinking");
        this.paneBlinkingAnimation = new TimedAnimation(paneBlinking, 2, .125f);
        TextureRegion paneHighlighted = decorationAtlas.findRegion("PaneHighlighted");
        this.paneHighlightedAnimation = new TimedAnimation(paneHighlighted);
    }

    @Override
    public void update(float delta) {
        TimedAnimation timedAnimation;
        switch (bossPaneStatus) {
            case BLINKING -> timedAnimation = paneBlinkingAnimation;
            case HIGHLIGHTED -> timedAnimation = paneHighlightedAnimation;
            case UNHIGHLIGHTED -> timedAnimation = paneUnhighlightedAnimation;
            default -> throw new IllegalStateException();
        }
        timedAnimation.update(delta);
        paneSprite.setRegion(timedAnimation.getCurrentT());
    }

    @Override
    public void draw(SpriteBatch spriteBatch) {
        Texture paneTexture = paneSprite.getTexture();
        if (paneTexture != null) {
            paneTexture.setFilter(Nearest, Nearest);
        }
        paneSprite.draw(spriteBatch);
        Texture bossTexture = bossSprite.getTexture();
        if (bossTexture != null) {
            bossTexture.setFilter(Nearest, Nearest);
        }
        bossSprite.draw(spriteBatch);
    }

}
