package com.game.menus.impl;

import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.game.core.GameContext2d;
import com.game.core.MegaTextHandle;
import com.game.constants.Boss;
import com.game.menus.MenuButton;
import com.game.menus.MenuScreen;
import com.game.utils.enums.Direction;
import com.game.utils.interfaces.Drawable;

import java.util.Map;

import static com.game.constants.MusicAsset.*;
import static com.game.constants.SoundAsset.CURSOR_MOVE_BLOOP_SOUND;
import static com.game.constants.TextureAsset.BOSS_FACES;
import static com.game.constants.ViewVals.PPM;
import static com.game.utils.UtilMethods.drawFiltered;

public class ExtrasScreen extends MenuScreen {

    private static final String BACK = "BACK";

    private static class BossDef implements Drawable {

        private final Sprite sprite;
        private final Sprite portrait;
        private final MegaTextHandle bio;

        private BossDef(GameContext2d gameContext, Boss boss, TextureRegion textureRegion) {
            TextureRegion portraitRegion = gameContext.getAsset(BOSS_FACES.getSrc(), TextureAtlas.class)
                    .findRegion(boss.getBossName());
            portrait = new Sprite(portraitRegion);
            portrait.setSize(4f * PPM, 4f * PPM);
            bio = new MegaTextHandle(new Vector2(), boss.getBio());
            sprite = new Sprite(textureRegion);
            sprite.setBounds(0f, 0f, 1.5f * PPM, 1.5f * PPM);
        }

        @Override
        public void draw(SpriteBatch spriteBatch) {
            drawFiltered(sprite, spriteBatch);
            drawFiltered(portrait, spriteBatch);
            bio.draw(spriteBatch);
        }

    }

    public ExtrasScreen(GameContext2d gameContext) {
        super(gameContext, BACK, MM11_MAIN_MENU_MUSIC.getSrc());

    }

    @Override
    public void render(float delta) {
        super.render(delta);

    }

    @Override
    protected void onAnyMovement(Direction direction) {
        Sound sound = gameContext.getAsset(CURSOR_MOVE_BLOOP_SOUND.getSrc(), Sound.class);
        gameContext.playSound(sound);
    }

    @Override
    protected Map<String, MenuButton> defineMenuButtons() {
        return null;
    }

}
