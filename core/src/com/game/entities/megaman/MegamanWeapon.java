package com.game.entities.megaman;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import static lombok.AccessLevel.*;

/**
 * Megaman's various weapons.
 */
@Getter
@RequiredArgsConstructor(access = PRIVATE)
public enum MegamanWeapon {

    MEGA_BUSTER(0, 0, 0, "Mega Buster", ""),
    FLAME_TOSS(3, 5, 8, "Flame Toss", "");

    private final int cost;
    private final int halfChargedCost;
    private final int fullyChargedCost;

    private final String weaponText;
    private final String weaponSpriteSrc;

}
