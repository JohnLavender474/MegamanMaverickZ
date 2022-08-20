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

    MEGA_BUSTER("Mega Buster", ""),
    FLAME_TOSS("Flame Toss", "");

    private final String weaponText;
    private final String weaponSpriteSrc;

}
