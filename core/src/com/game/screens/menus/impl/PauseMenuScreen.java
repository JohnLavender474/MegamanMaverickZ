package com.game.screens.menus.impl;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.game.core.GameContext2d;
import com.game.core.MegamanGameInfo;
import com.game.entities.megaman.MegamanWeapon;
import com.game.screens.menus.MenuButton;
import com.game.screens.menus.MenuScreen;
import com.game.core.MegaTextHandle;
import com.game.utils.objects.Percentage;

import java.util.Map;
import java.util.Set;
import java.util.function.Supplier;

import static com.badlogic.gdx.graphics.Color.*;
import static com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType.*;
import static com.game.core.constants.MegamanVals.*;
import static com.game.core.constants.RenderingGround.*;
import static com.game.core.constants.ViewVals.*;
import static com.game.core.MegamanGameInfo.*;
import static com.game.entities.megaman.MegamanWeapon.*;

public class PauseMenuScreen extends MenuScreen {

    private final MegaTextHandle[] weaponTexts = new MegaTextHandle[MegamanWeapon.values().length];
    private final Sprite[] weaponSprites = new Sprite[MegamanWeapon.values().length];
    private final Sprite[] healthTankSprites = new Sprite[MAX_HEALTH_TANKS];
    private final Set<MegamanWeapon> weaponsAttained;
    private final Supplier<Integer> creditsSupplier;
    private final MegaTextHandle creditsAmountText;
    private final Percentage[] healthTanks;

    /**
     * Instantiates a new Menu Screen. The first button is setBounds and music begins playing on showing.
     *
     * @param gameContext the {@link GameContext2d}
     */
    public PauseMenuScreen(GameContext2d gameContext) {
        super(gameContext, MEGA_BUSTER.name());
        MegamanGameInfo gameInfo = gameContext.getBlackboardObject(MEGAMAN_GAME_INFO, MegamanGameInfo.class);
        this.weaponsAttained = gameInfo.getMegamanWeaponsAttained();
        this.creditsSupplier = gameInfo.getCreditsSupplier();
        this.healthTanks = gameInfo.getHealthTanks();

        // TODO: Create texture atlas for weaon sprites
        for (MegamanWeapon megamanWeapon : MegamanWeapon.values()) {
            // TODO: Set sprite and text for each weapon
            weaponSprites[megamanWeapon.ordinal()] = new Sprite();
            weaponTexts[megamanWeapon.ordinal()] = new MegaTextHandle(8, new Vector2(), megamanWeapon.getWeaponText());
        }

        // TODO: Create texture atlas for health tank sprites
        for (int i = 0; i < MAX_HEALTH_TANKS; i++) {
            // TODO: Set sprite for each health tank
            healthTankSprites[i] = new Sprite();
        }

        // TODO: Credits text
        creditsAmountText = new MegaTextHandle(8, new Vector2());

    }

    @Override
    public void render(float delta) {
        super.render(delta);
        // render background shape
        ShapeRenderer shapeRenderer = gameContext.getShapeRenderer();
        shapeRenderer.begin(Filled);
        gameContext.setShapeRendererProjectionMatrix(UI);
        shapeRenderer.setColor(BLUE);
        shapeRenderer.rect(0f, 0f, VIEW_WIDTH * PPM, VIEW_HEIGHT * PPM);
        shapeRenderer.end();
        // begin sprite batch
        SpriteBatch spriteBatch = gameContext.getSpriteBatch();
        gameContext.setSpriteBatchProjectionMatrix(UI);
        spriteBatch.begin();

        // TODO: Render background

        // TODO: Render health tanks
        for (int i = 0; i < MAX_HEALTH_TANKS; i++) {
            if (healthTanks[i] != null) {
                healthTankSprites[i].draw(gameContext.getSpriteBatch());
            }
        }

        // TODO: Render weapons
        for (int i = 0; i < MegamanWeapon.values().length; i++) {
            if (weaponsAttained.contains(MegamanWeapon.values()[i])) {
                // weaponSprites[i].draw(spriteBatch);
                weaponTexts[i].draw(spriteBatch);
            }
        }

        // TODO: Credits
        creditsAmountText.setText("Credits: " + creditsSupplier.get());
        creditsAmountText.draw(spriteBatch);

        spriteBatch.end();

    }

    @Override
    protected Map<String, MenuButton> defineMenuButtons() {
        return Map.of();
    }

}
