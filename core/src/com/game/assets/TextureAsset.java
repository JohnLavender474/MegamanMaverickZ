package com.game.assets;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
public enum TextureAsset {

    // Colors
    COLORS("Colors.txt"),

    // Enemies
    MET("Met.txt"),
    ENEMIES_1("Enemies1.txt"),

    // Objects
    OBJECTS("Objects.txt"),

    // Hazards
    SAW("SawBeam.txt"),
    SPIKES("Spikes.txt"),
    HAZARDS_1("Hazards1.txt"),

    // Weapons
    FIRE("Fire.txt"),
    ELECTRIC("Electric.txt"),
    MEGAMAN_CHARGED_SHOT("MegamanChargedShot.txt"),
    MEGAMAN_HALF_CHARGED_SHOT("MegamanHalfChargedShot.txt"),

    // Items
    ITEMS("Items.txt"),

    // Environment
    DOORS("Door.txt"),

    // Decorations
    DECORATIONS("Decorations.txt"),

    // Backgrounds
    BACKGROUNDS_1("Backgrounds1.txt"),
    BACKGROUNDS_2("Backgrounds2.txt"),

    // UI
    BITS("HealthAndWeaponBits.txt"),
    STAGE_SELECT("StageSelect.txt"),
    BOSS_FACES("BossFaces.txt"),
    MEGAMAN_FACES("MegamanFaces.txt"),
    PAUSE_MENU("PauseMenu.txt"),
    MEGAMAN_MAIN_MENU("MegamanMainMenu.txt"),

    // Megaman
    MEGAMAN("Megaman.txt"),
    MEGAMAN_FIRE("MegamanFire.txt"),
    CHARGE_ORBS("ChargeOrbs.txt"),

    // Tiles
    CUSTOM_TILES("CustomTiles.txt"),

    // Bosses
    TIMBER_WOMAN("TimberWoman.txt"),
    DISTRIBUTOR_MAN("DistributorMan.txt"),
    ROASTER_MAN("RoasterMan.txt"),
    MISTER_MAN("MisterMan.txt"),
    BLUNT_MAN("BluntMan.txt");

    private static final String prefix = "sprites/SpriteSheets/";

    private final String src;

    public String getSrc() {
        return prefix + src;
    }

}
