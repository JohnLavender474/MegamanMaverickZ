package com.game.menus.impl;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.game.GameContext2d;
import com.game.animations.TimedAnimation;
import com.game.entities.megaman.Megaman;
import com.game.levels.BitsBarUi;
import com.game.menus.MenuButton;
import com.game.menus.MenuScreen;
import com.game.text.MegaTextHandle;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

import static com.game.ViewVals.*;
import static com.game.assets.TextureAsset.*;
import static com.game.entities.megaman.MegamanWeapon.MEGA_BUSTER;
import static com.game.sprites.RenderingGround.UI;
import static com.game.utils.UtilMethods.drawFiltered;
import static java.util.Arrays.stream;

public class PauseMenuScreen extends MenuScreen {

    private static final class WeaponSelection {
        private Vector2 arrowPos;
        private Sprite iconSprite;
        private BitsBarUi ammoBitsBar;
        private MegaTextHandle weaponText;
    }

    private static final String HEALTH_TANK_SELECTION_PREFIX = "HealthTankSelection";

    private static final class HealthTankSelection {

        private Sprite graySprite;
        private Sprite tankSprite;
        private TimedAnimation tankAnim;
        private MegaTextHandle amountText;
        private Supplier<Integer> amountSupplier;
        private Supplier<Boolean> canBeUsedSupplier;

        private int getAmount() {
            return amountSupplier.get();
        }

        private boolean canBeUsed() {
            return canBeUsedSupplier.get();
        }

    }

    private final Sprite canvas;
    private final List<Sprite> borderBlocks = new ArrayList<>();
    /*
    private final MegamanStats megamanInfo;
    private final BlinkingArrow blinkingArrow;
    private final Map<String, WeaponSelection> weaponsSelections;
    private final Map<String, HealthTankSelection> healthTankSelections;
     */

    @Setter
    private Supplier<Megaman> megamanSupplier;

    /**
     * Instantiates a new Menu Screen. The first button is setBounds and music begins playing on showing.
     *
     * @param gameContext the {@link GameContext2d}
     */
    public PauseMenuScreen(GameContext2d gameContext) {
        super(gameContext, MEGA_BUSTER.name());
        TextureAtlas pauseMenuTA = gameContext.getAsset(PAUSE_MENU.getSrc(), TextureAtlas.class);
        canvas = new Sprite(pauseMenuTA.findRegion("Canvas"));
        canvas.setBounds(0f, -1f, VIEW_WIDTH * PPM, VIEW_HEIGHT * PPM);
        TextureRegion bBlockReg = pauseMenuTA.findRegion("BorderBlock");
        float halfPPM = PPM / 2f;
        for (int i = 0; i < VIEW_WIDTH; i++) {
            for (int x = 0; x < 2; x++) {
                for (int y = 0; y < 2; y++) {
                    if (x == 0) {
                        float yOffset = (i * PPM) + (y * halfPPM);
                        Sprite leftBBlock = new Sprite(bBlockReg);
                        leftBBlock.setBounds(0f, yOffset, halfPPM, halfPPM);
                        borderBlocks.add(leftBBlock);
                        Sprite rightBBlock = new Sprite(bBlockReg);
                        rightBBlock.setBounds((VIEW_WIDTH * PPM) - halfPPM, yOffset, halfPPM, halfPPM);
                        borderBlocks.add(rightBBlock);
                    }
                    float blockX = (i * PPM) + (x * halfPPM);
                    Sprite bottomBBlock = new Sprite(bBlockReg);
                    bottomBBlock.setBounds(blockX, 0f, halfPPM, halfPPM);
                    borderBlocks.add(bottomBBlock);
                    if (i >= 4 && i < VIEW_WIDTH - 4 && y == 0) {
                        continue;
                    }
                    Sprite topBBlock = new Sprite(bBlockReg);
                    float yOffset = VIEW_HEIGHT - 1;
                    borderBlocks.add(topBBlock);
                    topBBlock.setBounds(blockX, (yOffset * PPM) + (y * halfPPM), halfPPM, halfPPM);
                }
            }
        }
        /*
        megamanInfo = gameContext.getBlackboardObject(MEGAMAN_STATS, MegamanStats.class);
        weaponsSelections = defineWeaponsSelections();
        healthTankSelections = defineHealthTankSelections();
        blinkingArrow = new BlinkingArrow(gameContext, weaponsSelections.get(MEGA_BUSTER.name()).arrowPos);
         */
    }

    /*
    // TODO: Set values
    private Map<String, WeaponSelection> defineWeaponsSelections() {
        Map<String, WeaponSelection> weaponsSelectionMap = new HashMap<>();
        TextureAtlas weaponIcons = gameContext.getAsset("", TextureAtlas.class);
        TextureRegion bit = gameContext.getAsset(BITS.getSrc(), TextureAtlas.class).findRegion("StandardBit");
        final int leftColSize = 6;
        for (int i = 0; i < MegamanWeapon.values().length; i++) {
            MegamanWeapon weapon = MegamanWeapon.values()[i];
            WeaponSelection selection = new WeaponSelection();
            if (i < leftColSize) {

            } else {

            }
            // set arrow pos
            Vector2 arrowPos = new Vector2();
            selection.arrowPos = arrowPos;
            // set icon sprite
            Sprite iconSprite = new Sprite(weaponIcons.findRegion(""));
            iconSprite.setBounds(0f, 0f, 0f, 0f);
            selection.iconSprite = iconSprite;
            // set ammo bits bar
            BitsBarUi ammoBitsBar = new BitsBarUi(gameContext, () -> megamanInfo.getWeaponAmmo(weapon), bit,
                    new Vector2(), new Rectangle());
            selection.ammoBitsBar = ammoBitsBar;
            // set weapon text
            MegaTextHandle weaponText = new MegaTextHandle(new Vector2(), weapon.getWeaponText());
            weaponText.setText(weapon.getWeaponText());
            selection.weaponText = weaponText;
            // put into map
            weaponsSelectionMap.put(weapon.name(), selection);
        }
        return weaponsSelectionMap;
    }
     */

    /*
    // TODO: Set values
    private Map<String, HealthTankSelection> defineHealthTankSelections() {
        Map<String, HealthTankSelection> healthTankSelections = new HashMap<>();
        TextureAtlas itemsAtlas = gameContext.getAsset(ITEMS.getSrc(), TextureAtlas.class);
        TextureRegion tankAnimRegion = itemsAtlas.findRegion("HealthTank");
        for (int i = 0; i < MAX_HEALTH_TANKS; i++) {
            final int finalI = i;
            HealthTankSelection selection = new HealthTankSelection();
            // gray sprite
            Sprite graySprite = new Sprite();
            graySprite.setBounds(0f, 0f, 0f, 0f);
            TextureRegion grayTankRegion = itemsAtlas.findRegion("EmptyHealthTank");
            graySprite.setRegion(grayTankRegion);
            selection.graySprite = graySprite;
            // tank sprite
            Sprite tankSprite = new Sprite();
            tankSprite.setBounds(0f, 0f, 0f, 0f);
            selection.tankSprite = tankSprite;
            // tank anim
            TimedAnimation tankAnim = new TimedAnimation(tankAnimRegion, 2, .2f);
            selection.tankAnim = tankAnim;
            // amount text
            MegaTextHandle amountText = new MegaTextHandle(new Vector2());
            selection.amountText = amountText;
            // can be used
            Supplier<Boolean> canBeUsedSupplier = () -> megamanInfo.canBeUsed(finalI);
            selection.canBeUsedSupplier = canBeUsedSupplier;
            // amount supplier
            Supplier<Integer> amountSupplier = () -> megamanInfo.getHealthTankValue(finalI);
            selection.amountSupplier = amountSupplier;
            // put into map
            healthTankSelections.put(HEALTH_TANK_SELECTION_PREFIX + i, selection);
        }
        return healthTankSelections;
    }
     */

    @Override
    public void render(float delta) {
        super.render(delta);
        // TODO: Render background
        SpriteBatch spriteBatch = gameContext.getSpriteBatch();
        gameContext.setSpriteBatchProjectionMatrix(UI);
        spriteBatch.begin();
        // canvas
        drawFiltered(canvas, spriteBatch);
        borderBlocks.forEach(bBlock -> drawFiltered(bBlock, spriteBatch));
        // draw weapon selections
        /*
        stream(MegamanWeapon.values()).forEach(weapon -> {
            if (!megamanInfo.hasWeapon(weapon)) {
                return;
            }
            WeaponSelection selection = weaponsSelections.get(weapon.name());
            selection.ammoBitsBar.draw();
            selection.weaponText.draw(spriteBatch);
            selection.iconSprite.draw(spriteBatch);
        });
        // draw health tank selections
        for (int i = 0; i < MAX_HEALTH_TANKS; i++) {
            if (!megamanInfo.hasHealthTank(i)) {
                continue;
            }
            HealthTankSelection selection = healthTankSelections.get(HEALTH_TANK_SELECTION_PREFIX + i);
            // health tank sprite
            Sprite tankSprite = selection.graySprite;
            if (selection.canBeUsed()) {
                selection.tankAnim.update(delta);
                selection.tankSprite.setRegion(selection.tankAnim.getCurrentT());
                tankSprite = selection.tankSprite;
            }
            drawFiltered(tankSprite, spriteBatch);
            // amount text
            int amount = selection.getAmount();
            selection.amountText.setText("" + amount);
            selection.amountText.draw(spriteBatch);
        }
         */
        spriteBatch.end();
    }

    @Override
    protected Map<String, MenuButton> defineMenuButtons() {
        return Map.of();
        /*
        return new HashMap<>() {{
            for (int i = 0; i < MegamanWeapon.values().length; i++) {
                MegamanWeapon weapon = MegamanWeapon.values()[i];
                put(weapon.name(), new MenuButton() {

                    @Override
                    public boolean onSelect(float delta) {
                        // TODO: switch megaman to weapon choice using megaman supplier
                        return false;
                    }

                    @Override
                    public void onNavigate(Direction direction, float delta) {

                    }

                });
            }
            for (int i = 0; i < MAX_HEALTH_TANKS; i++) {
                final int finalI = i;
                put(HEALTH_TANK_SELECTION_PREFIX + i, new MenuButton() {

                    @Override
                    public boolean onSelect(float delta) {
                        if (!megamanInfo.canBeUsed(finalI)) {
                            // TODO: play ERROR sound and do nothing else
                            return false;
                        }
                        // TODO: call func to fill up megaman's health
                        return false;
                    }

                    @Override
                    public void onNavigate(Direction direction, float delta) {
                        switch (direction) {
                            case DIR_UP -> {
                                MegamanWeapon weapon = MegamanWeapon.values()[5];
                                setMenuButton(weapon.name());
                            }
                            case DIR_DOWN -> {
                                MegamanWeapon weapon = MegamanWeapon.values()[0];
                                setMenuButton(weapon.name());
                            }
                            case DIR_LEFT -> {
                                if (finalI == 0) {
                                    // TODO: set to rightmost element
                                } else {
                                    int x = finalI - 1;
                                    setMenuButton(HEALTH_TANK_SELECTION_PREFIX + x);
                                }
                            }
                            case DIR_RIGHT -> {
                                if (finalI == MAX_HEALTH_TANKS - 1) {
                                    // TODO: set to next right element
                                } else {
                                    int x = finalI + 1;
                                    setMenuButton(HEALTH_TANK_SELECTION_PREFIX + x);
                                }
                            }
                        }
                    }

                });
            }
        }};
         */
    }

}
