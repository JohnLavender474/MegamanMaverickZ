package com.game.menus.impl;

import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.*;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.game.GameContext2d;
import com.game.animations.TimedAnimation;
import com.game.menus.BlinkingArrow;
import com.game.menus.MenuButton;
import com.game.menus.MenuScreen;
import com.game.updatables.Updatable;
import com.game.utils.enums.Direction;
import com.game.utils.enums.Position;
import com.game.utils.interfaces.Drawable;
import lombok.Getter;
import lombok.Setter;

import java.util.*;

import static com.badlogic.gdx.graphics.Texture.TextureFilter.Nearest;
import static com.game.ConstVals.*;
import static com.game.ConstVals.Boss.*;
import static com.game.ConstVals.MusicAsset.*;
import static com.game.ConstVals.SoundAsset.CURSOR_MOVE_BLOOP_SOUND;
import static com.game.ConstVals.TextureAsset.*;
import static com.game.ConstVals.ViewVals.*;
import static com.game.menus.impl.BossSelectScreen.BossPaneStatus.*;
import static com.game.utils.UtilMethods.centerPoint;
import static com.game.utils.enums.Position.*;

public class BossSelectScreen extends MenuScreen {

    // TODO: Fix the damn panes!

    enum BossPaneStatus {
        BLINKING,
        HIGHLIGHTED,
        UNHIGHLIGHTED
    }

    @Getter
    @Setter
    static class Pane implements Updatable, Drawable {

        private final String bossName;
        private final TimedAnimation bossAnimation;
        private final TimedAnimation paneBlinkingAnimation;
        private final TimedAnimation paneHighlightedAnimation;
        private final TimedAnimation paneUnhighlightedAnimation;

        private final Sprite bossSprite = new Sprite();
        private final Sprite paneSprite = new Sprite();

        private BossPaneStatus bossPaneStatus = UNHIGHLIGHTED;

        public Pane(String bossName, int x, int y, GameContext2d gameContext) {
            this.bossName = bossName;
            // set animations
            TextureRegion metRegion = gameContext.getAsset(MET_TEXTURE_ATLAS.getSrc(), TextureAtlas.class)
                    .findRegion("Run");
            this.bossAnimation = new TimedAnimation(metRegion, 2, .125f);
            TextureAtlas decorationAtlas = gameContext.getAsset(DECORATIONS_TEXTURE_ATLAS.getSrc(), TextureAtlas.class);
            TextureRegion paneBlinking = decorationAtlas.findRegion("StageSelectBox_Blinking");
            this.paneBlinkingAnimation = new TimedAnimation(paneBlinking, 2, .125f);
            TextureRegion paneHighlighted = decorationAtlas.findRegion("StageSelectBox_Highlighted");
            this.paneHighlightedAnimation = new TimedAnimation(paneHighlighted);
            TextureRegion paneUnhighlighted = decorationAtlas.findRegion("StageSelectBox_Unhighlighted");
            this.paneUnhighlightedAnimation = new TimedAnimation(paneUnhighlighted);
            // rect
            Rectangle rect = new Rectangle(x * PANE_BOUNDS_WIDTH * PPM, BOTTOM_OFFSET * PPM + y * PANE_BOUNDS_HEIGHT * PPM,
                    PANE_BOUNDS_WIDTH * PPM, PANE_BOUNDS_HEIGHT * PPM);
            Vector2 centerPoint = centerPoint(rect);
            // met sprite
            // bossSprite.setSize(PANE_BOUNDS_WIDTH * PPM, PANE_BOUNDS_HEIGHT * PPM);
            bossSprite.setSize(4f * PPM, 3f * PPM);
            bossSprite.setCenter(centerPoint.x, centerPoint.y + 15f);
            // pane sprite
            // paneSprite.setSize(PANE_BOUNDS_WIDTH * PPM, PANE_BOUNDS_HEIGHT * PPM);
            paneSprite.setSize(4f * PPM, 3f * PPM);
            paneSprite.setCenter(centerPoint.x, centerPoint.y);
        }

        @Override
        public void update(float delta) {
            bossAnimation.update(delta);
            bossSprite.setRegion(bossAnimation.getCurrentT());
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

    private static final float PANE_BOUNDS_WIDTH = 5.33f;
    private static final float PANE_BOUNDS_HEIGHT = 4f;
    private static final float BOTTOM_OFFSET = 1f;

    private static final String MEGAMAN_FACE = "MegamanFace";
    private static final String PASSWORD = "Password";
    private static final String STORE = "Store";
    private static final String EXIT = "Exit";

    private final Sprite blueSprite = new Sprite();
    private final Sprite megamanFaceSprite = new Sprite();
    private final List<Pane> panes = new ArrayList<>();
    private final Map<String, Vector2> blinkingArrowCenters = new HashMap<>();
    private final Map<Position, TimedAnimation> megamanFaceAnimations = new EnumMap<>(Position.class);

    private Boss currentHighlightedBoss = null;
    private BlinkingArrow blinkingArrow;

    /**
     * Instantiates a new Menu Screen.
     *
     * @param gameContext the {@link GameContext2d}
     */
    public BossSelectScreen(GameContext2d gameContext) {
        super(gameContext, MEGAMAN_FACE, STAGE_SELECT_MM3_MUSIC.getSrc());
    }

    @Override
    public void show() {
        super.show();
        // megaman face sprite and animations
        megamanFaceSprite.setSize(4f * PPM, 3f * PPM);
        Rectangle rect = new Rectangle(PANE_BOUNDS_WIDTH * PPM, BOTTOM_OFFSET * PPM + PANE_BOUNDS_HEIGHT * PPM,
                PANE_BOUNDS_WIDTH * PPM, PANE_BOUNDS_HEIGHT * PPM);
        Vector2 centerPoint = centerPoint(rect);
        megamanFaceSprite.setCenter(centerPoint.x, centerPoint.y);
        TextureAtlas megamanFacesAtlas = gameContext.getAsset(MEGAMAN_FACES_TEXTURE_ATLAS.getSrc(), TextureAtlas.class);
        for (Position position : Position.values()) {
            TextureRegion region = megamanFacesAtlas.findRegion(position.name());
            megamanFaceAnimations.put(position, new TimedAnimation(region, new float[] {2f, .035f, .035f, .035f}));
        }
        // blinking arrow
        blinkingArrow = new BlinkingArrow(gameContext);
        blinkingArrowCenters.put(PASSWORD, new Vector2());
        blinkingArrowCenters.put(STORE, new Vector2());
        blinkingArrowCenters.put(EXIT, new Vector2());
        // boss panes

        for (Boss boss : Boss.values()) {
            panes.add(new Pane(boss.getBossName(), boss.getPosition().getX(), boss.getPosition().getY(), gameContext));
        }
        // blue background
        TextureAtlas decorations = gameContext.getAsset(DECORATIONS_TEXTURE_ATLAS.getSrc(), TextureAtlas.class);
        TextureRegion blueRegion = decorations.findRegion("Blue");
        blueSprite.setRegion(blueRegion);
        blueSprite.setSize(VIEW_WIDTH * PPM, VIEW_HEIGHT * PPM);
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
        // blue background
        blueSprite.draw(spriteBatch);
        // set current highlighted boss, null if no boss is highlighted
        currentHighlightedBoss = findByName(getCurrentMenuButtonKey());
        TimedAnimation megamanFaceAnimation = megamanFaceAnimations.get(currentHighlightedBoss != null ?
                currentHighlightedBoss.getPosition() : CENTER);
        megamanFaceAnimation.update(delta);
        megamanFaceSprite.setRegion(megamanFaceAnimation.getCurrentT());
        Texture texture = megamanFaceSprite.getTexture();
        if (texture != null) {
            texture.setFilter(Nearest, Nearest);
        }
        megamanFaceSprite.draw(spriteBatch);
        // boss panes
        panes.forEach(pane -> {
            if (pane.getBossName().equals(currentHighlightedBoss.getBossName())) {
                pane.setBossPaneStatus(isSelectionMade() ? HIGHLIGHTED : BLINKING);
            } else {
                pane.setBossPaneStatus(UNHIGHLIGHTED);
            }
            pane.update(delta);
            pane.draw(spriteBatch);
        });
        // blinking arrow
        if (blinkingArrowCenters.containsKey(getCurrentMenuButtonKey())) {
            Vector2 center = blinkingArrowCenters.get(getCurrentMenuButtonKey());
            blinkingArrow.setCenter(center);
            blinkingArrow.draw(spriteBatch);
        }
        // end spritebatch
        spriteBatch.end();
    }

    @Override
    protected Map<String, MenuButton> defineMenuButtons() {
        Map<String, MenuButton> menuButtons = new HashMap<>();
        menuButtons.put(MEGAMAN_FACE, new MenuButton() {

            @Override
            public boolean onSelect(float delta) {
                return false;
            }

            @Override
            public void onNavigate(Direction direction, float delta) {
                switch (direction) {
                    case DIR_UP -> setMenuButton(findByPos(1, 2).getBossName());
                    case DIR_DOWN -> setMenuButton(findByPos(1, 0).getBossName());
                    case DIR_LEFT -> setMenuButton(findByPos(0, 1).getBossName());
                    case DIR_RIGHT -> setMenuButton(findByPos(2, 1).getBossName());
                }
            }

        });
        menuButtons.put(STORE, new MenuButton() {

            @Override
            public boolean onSelect(float delta) {
                // TODO: Change to store screen
                return true;
            }

            @Override
            public void onNavigate(Direction direction, float delta) {
                switch (direction) {
                    case DIR_UP -> setMenuButton(findByPos(0, 0).getBossName());
                    case DIR_DOWN -> setMenuButton(findByPos(0, 2).getBossName());
                    case DIR_LEFT -> setMenuButton(EXIT);
                    case DIR_RIGHT -> setMenuButton(PASSWORD);
                }
            }

        });
        menuButtons.put(PASSWORD, new MenuButton() {

            @Override
            public boolean onSelect(float delta) {
                // TODO: Pop up dialog with password, disappears after any bossName is pressed
                return false;
            }

            @Override
            public void onNavigate(Direction direction, float delta) {
                switch (direction) {
                    case DIR_UP -> setMenuButton(findByPos(1, 0).getBossName());
                    case DIR_DOWN -> setMenuButton(findByPos(1, 2).getBossName());
                    case DIR_LEFT -> setMenuButton(STORE);
                    case DIR_RIGHT -> setMenuButton(EXIT);
                }
            }

        });
        menuButtons.put(EXIT, new MenuButton() {

            @Override
            public boolean onSelect(float delta) {
                // TODO: Pop up dialog asking to confirm exit to main menu, press X to accept, any other to abort
                return false;
            }

            @Override
            public void onNavigate(Direction direction, float delta) {
                switch (direction) {
                    case DIR_UP -> setMenuButton(findByPos(2, 0).getBossName());
                    case DIR_DOWN -> setMenuButton(findByPos(2, 2).getBossName());
                    case DIR_LEFT -> setMenuButton(PASSWORD);
                    case DIR_RIGHT -> setMenuButton(STORE);
                }
            }

        });
        for (Boss boss : Boss.values()) {
            menuButtons.put(boss.getBossName(), new MenuButton() {

                @Override
                public boolean onSelect(float delta) {
                    gameContext.setScreen(boss.getGameScreen());
                    return true;
                }

                @Override
                public void onNavigate(Direction direction, float delta) {
                    int x = boss.getPosition().getX();
                    int y = boss.getPosition().getY();
                    switch (direction) {
                        case DIR_UP -> y += 1;
                        case DIR_DOWN -> y -= 1;
                        case DIR_LEFT -> x -= 1;
                        case DIR_RIGHT -> x += 1;
                    }
                    if (y < 0 || y > 2) {
                        setMenuButton(STORE);
                        return;
                    }
                    if (x < 0) {
                        x = 2;
                    }
                    if (x > 2) {
                        x = 0;
                    }
                    Position position = getByGridIndex(x, y);
                    if (position == null) {
                        throw new IllegalStateException();
                    } else if (position.equals(CENTER)) {
                        setMenuButton(MEGAMAN_FACE);
                    } else {
                        setMenuButton(findByPos(x, y).getBossName());
                    }
                }

            });
        }
        return menuButtons;
    }

}
