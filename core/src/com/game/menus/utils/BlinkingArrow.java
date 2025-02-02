package com.game.menus.utils;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.Vector2;
import com.game.GameContext2d;
import com.game.utils.interfaces.Updatable;
import com.game.utils.interfaces.Drawable;
import com.game.utils.objects.Timer;
import lombok.Getter;

import static com.game.assets.TextureAsset.*;
import static com.game.ViewVals.PPM;
import static com.game.utils.UtilMethods.drawFiltered;

@Getter
public class BlinkingArrow implements Updatable, Drawable {

    private static final float ARROW_BLINK_DURATION = .2f;

    private final Vector2 center;
    private final Sprite arrowSprite = new Sprite();
    private final Timer arrowBlinkTimer = new Timer();

    private boolean arrowVisible;

    public BlinkingArrow(GameContext2d gameContext, Vector2 center) {
        this.center = center;
        arrowSprite.setRegion(gameContext.getAsset(DECORATIONS.getSrc(), TextureAtlas.class)
                .findRegion("Arrow"));
        arrowSprite.setSize(PPM / 2f, PPM / 2f);
        arrowSprite.setCenter(center.x, center.y);
        arrowBlinkTimer.setDuration(ARROW_BLINK_DURATION);
    }

    public void setCenter(Vector2 center) {
        setCenter(center.x, center.y);
    }

    public void setCenter(float x, float y) {
        center.set(x, y);
    }

    @Override
    public void update(float delta) {
        arrowBlinkTimer.update(delta);
        if (arrowBlinkTimer.isFinished()) {
            arrowVisible = !arrowVisible;
            arrowBlinkTimer.reset();
        }
    }

    @Override
    public void draw(SpriteBatch spriteBatch) {
        if (!isArrowVisible()) {
            return;
        }
        drawFiltered(arrowSprite, spriteBatch);
    }

}
