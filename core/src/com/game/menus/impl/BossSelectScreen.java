package com.game.menus.impl;

import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.*;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.game.GameContext2d;
import com.game.animations.TimedAnimation;
import com.game.menus.MenuButton;
import com.game.menus.MenuScreen;
import com.game.updatables.Updatable;
import com.game.utils.enums.Direction;
import com.game.utils.interfaces.Drawable;
import lombok.Setter;

import java.util.*;

import static com.badlogic.gdx.graphics.Texture.TextureFilter.Nearest;
import static com.game.ConstVals.*;
import static com.game.ConstVals.SoundAsset.CURSOR_MOVE_BLOOP_SOUND;
import static com.game.ConstVals.TextureAsset.*;
import static com.game.ConstVals.ViewVals.*;
import static com.game.menus.impl.BossSelectScreen.BossPaneStatus.*;
import static com.game.ConstVals.Boss.*;
import static com.game.utils.UtilMethods.centerPoint;

public class BossSelectScreen extends MenuScreen {

    enum BossPaneStatus {
        BLINKING,
        HIGHLIGHTED,
        UNHIGHLIGHTED
    }

    static class BossPane implements Updatable, Drawable {

        private final TimedAnimation metAnimation;
        private final TimedAnimation paneBlinkingAnimation;
        private final TimedAnimation paneHighlightedAnimation;
        private final TimedAnimation paneUnhighlightedAnimation;

        private final Sprite metSprite = new Sprite();
        private final Sprite paneSprite = new Sprite();

        @Setter
        private BossPaneStatus bossPaneStatus = UNHIGHLIGHTED;

        public BossPane(int x, int y, GameContext2d gameContext) {
            // set animations
            TextureRegion metRegion = gameContext.getAsset(MET_TEXTURE_ATLAS.getSrc(), TextureAtlas.class)
                    .findRegion("Run");
            this.metAnimation = new TimedAnimation(metRegion, 2, .125f);
            TextureAtlas decorationAtlas = gameContext.getAsset(DECORATIONS_TEXTURE_ATLAS.getSrc(), TextureAtlas.class);
            TextureRegion paneBlinking = decorationAtlas.findRegion("StageSelectBox_Blinking");
            this.paneBlinkingAnimation = new TimedAnimation(paneBlinking, 2, .125f);
            TextureRegion paneHighlighted = decorationAtlas.findRegion("StageSelectBox_Highlighted");
            this.paneHighlightedAnimation = new TimedAnimation(paneHighlighted);
            TextureRegion paneUnhighlighted = decorationAtlas.findRegion("StageSelectBox_Unhighlighted");
            this.paneUnhighlightedAnimation = new TimedAnimation(paneUnhighlighted);
            // rect
            Rectangle rect = new Rectangle(x * BOSS_PANE_WIDTH * PPM, BOTTOM_OFFSET * PPM + y * BOSS_PANE_HEIGHT * PPM,
                    BOSS_PANE_WIDTH * PPM, BOSS_PANE_HEIGHT * PPM);
            Vector2 centerPoint = centerPoint(rect);
            // met sprite
            metSprite.setSize(BOSS_PANE_WIDTH * PPM, BOSS_PANE_HEIGHT * PPM);
            metSprite.setCenter(centerPoint.x, centerPoint.y + 15f);
            // pane sprite
            paneSprite.setSize(BOSS_PANE_WIDTH * PPM, BOSS_PANE_HEIGHT * PPM);
            paneSprite.setCenter(centerPoint.x, centerPoint.y);
        }

        @Override
        public void update(float delta) {
            metAnimation.update(delta);
            metSprite.setRegion(metAnimation.getCurrentT());
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
            Texture metTexture = metSprite.getTexture();
            if (metTexture != null) {
                metTexture.setFilter(Nearest, Nearest);
            }
            metSprite.draw(spriteBatch);
        }

    }

    private static final float BOSS_PANE_WIDTH = 5.33f;
    private static final float BOSS_PANE_HEIGHT = 4f;
    private static final float BOTTOM_OFFSET = 1f;

    private static final String STORE = "Store";
    private static final String EXTRAS = "Extras";
    private static final String PASSWORD = "Password";
    private static final String QUIT_GAME = "Quit Game";

    private final Sprite topBlackBar = new Sprite();
    private final Sprite bottomBlackBar = new Sprite();

    private final Map<Boss, BossPane> bossPanes = new EnumMap<>(Boss.class);

    private Boss currentHighlightedBoss = null;

    /**
     * Instantiates a new Menu Screen.
     *
     * @param gameContext the {@link GameContext2d}
     */
    public BossSelectScreen(GameContext2d gameContext) {
        super(gameContext, null);
    }

    @Override
    public void show() {
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                Boss boss = Boss.values()[i * 3 + j];
                bossPanes.put(boss, new BossPane(boss.getX(), boss.getY(), gameContext));
            }
        }
        TextureRegion blackRegion = gameContext.getAsset(DECORATIONS_TEXTURE_ATLAS.getSrc(), TextureAtlas.class)
                .findRegion("Black");
        topBlackBar.setRegion(blackRegion);
        topBlackBar.setSize(VIEW_WIDTH * PPM, PPM);
        bottomBlackBar.set(topBlackBar);
        topBlackBar.setPosition(0f, (VIEW_HEIGHT - 1) * PPM);
    }

    @Override
    protected void onMovement() {
        gameContext.getAsset(CURSOR_MOVE_BLOOP_SOUND.getSrc(), Sound.class).play();
    }

    @Override
    public void render(float delta) {
        super.render(delta);
        // begin spritebatch
        SpriteBatch spriteBatch = gameContext.getSpriteBatch();
        spriteBatch.setProjectionMatrix(uiViewport.getCamera().combined);
        spriteBatch.begin();
        // boss panes
        bossPanes.forEach((boss, bossPane) -> {
            if (boss.equals(currentHighlightedBoss)) {
                bossPane.setBossPaneStatus(isSelectionMade() ? HIGHLIGHTED : BLINKING);
            } else {
                bossPane.setBossPaneStatus(UNHIGHLIGHTED);
            }
            bossPane.update(delta);
            bossPane.draw(spriteBatch);
        });
        // top and bottom black bars
        topBlackBar.draw(spriteBatch);
        bottomBlackBar.draw(spriteBatch);
        // end spritebatch
        spriteBatch.end();
    }

    @Override
    protected Map<String, MenuButton> defineMenuButtons() {
        return new HashMap<>() {{
                put(TIMBER_WOMAN.getName(), new MenuButton() {

                    @Override
                    public void onSelect(float delta) {

                    }

                    @Override
                    public void onHighlighted(float delta) {
                        currentHighlightedBoss = TIMBER_WOMAN;
                    }

                    @Override
                    public void onNavigate(Direction direction, float delta) {

                    }

                });
                put(JELLY_WOMAN.getName(), new MenuButton() {

                    @Override
                    public void onSelect(float delta) {

                    }

                    @Override
                    public void onHighlighted(float delta) {
                        currentHighlightedBoss = JELLY_WOMAN;
                    }

                    @Override
                    public void onNavigate(Direction direction, float delta) {

                    }

                });
                put(TSUNAMI_MAN.getName(), new MenuButton() {

                    @Override
                    public void onSelect(float delta) {

                    }

                    @Override
                    public void onHighlighted(float delta) {
                        currentHighlightedBoss = TSUNAMI_MAN;
                    }

                    @Override
                    public void onNavigate(Direction direction, float delta) {

                    }

                });
                put(MANIAC_MAN.getName(), new MenuButton() {

                    @Override
                    public void onSelect(float delta) {

                    }

                    @Override
                    public void onHighlighted(float delta) {
                        currentHighlightedBoss = MANIAC_MAN;
                    }

                    @Override
                    public void onNavigate(Direction direction, float delta) {

                    }

                });
                put(SHROOM_MAN.getName(), new MenuButton() {

                    @Override
                    public void onSelect(float delta) {

                    }

                    @Override
                    public void onHighlighted(float delta) {
                        currentHighlightedBoss = SHROOM_MAN;
                    }

                    @Override
                    public void onNavigate(Direction direction, float delta) {

                    }

                });
                put(ATTIC_MAN.getName(), new MenuButton() {

                    @Override
                    public void onSelect(float delta) {

                    }

                    @Override
                    public void onHighlighted(float delta) {
                        currentHighlightedBoss = ATTIC_MAN;
                    }

                    @Override
                    public void onNavigate(Direction direction, float delta) {

                    }

                });
                put(LION_MAN.getName(), new MenuButton() {

                    @Override
                    public void onSelect(float delta) {

                    }

                    @Override
                    public void onHighlighted(float delta) {
                        currentHighlightedBoss = LION_MAN;
                    }

                    @Override
                    public void onNavigate(Direction direction, float delta) {

                    }

                });
                put(STORE, new MenuButton() {

                    @Override
                    public void onSelect(float delta) {

                    }

                    @Override
                    public void onNavigate(Direction direction, float delta) {

                    }

                });
                put(EXTRAS, new MenuButton() {

                    @Override
                    public void onSelect(float delta) {

                    }

                    @Override
                    public void onNavigate(Direction direction, float delta) {

                    }

                });
                put(PASSWORD, new MenuButton() {

                    @Override
                    public void onSelect(float delta) {

                    }

                    @Override
                    public void onNavigate(Direction direction, float delta) {

                    }

                });
                put(QUIT_GAME, new MenuButton() {

                    @Override
                    public void onSelect(float delta) {

                    }

                    @Override
                    public void onNavigate(Direction direction, float delta) {

                    }

                });
            }};
    }

}
