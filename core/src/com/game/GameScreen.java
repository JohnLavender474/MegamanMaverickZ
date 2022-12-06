package com.game;

/**
 * Game screens.
 */
public enum GameScreen {
    // Test
    TEST_STAGE,
    TEST_TEXTURE_ASSET,

    // Menus
    MAIN_MENU,
    PASSWORD,
    EXTRAS,
    BOSS_SELECT,
    PAUSE_MENU,
    CONTROLLER_SETTINGS,

    // Decorative
    LEVEL_INTRO,

    // Boss levels
    TIMBER_WOMAN,
    DISTRIBUTOR_MAN,
    ROASTER_MAN,
    MISTER_MAN,
    BLUNT_MAN,
    NUKE_MAN,
    FRIDGE_MAN,
    MICROWAVE_MAN;

    /**
     * Get the boss level screen enum mapped to the boss name.
     *
     * @param bossName the boss name
     * @return the boss level screen enum
     */
    public static GameScreen getBossLevelScreenEnum(String bossName) {
        switch (bossName) {
            case "Timber Woman" -> {
                return TIMBER_WOMAN;
            }
            case "Distributor Man" -> {
                return DISTRIBUTOR_MAN;
            }
            case "Roaster Man" -> {
                return ROASTER_MAN;
            }
            case "Mister Man" -> {
                return MISTER_MAN;
            }
            case "Blunt Man" -> {
                return BLUNT_MAN;
            }
            case "Nuke Man" -> {
                return NUKE_MAN;
            }
            case "Fridge Man" -> {
                return FRIDGE_MAN;
            }
            case "Microwave Man" -> {
                return MICROWAVE_MAN;
            }
            default -> throw new IllegalArgumentException("No boss level screen mapped to " + bossName);
        }
    }

}
