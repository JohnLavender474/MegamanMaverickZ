package com.game;

import com.game.entities.megaman.MegamanSpecialAbility;
import com.game.entities.megaman.MegamanWeapon;
import lombok.Getter;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import static com.game.ConstVals.*;
import static com.game.entities.megaman.MegamanWeapon.*;
import static com.game.utils.UtilMethods.boundNumber;

@Getter
public class MegamanGameInfo {

    private final Set<MegamanSpecialAbility> megamanSpecialAbilities = EnumSet.noneOf(MegamanSpecialAbility.class);
    private final Set<MegamanWeapon> megamanWeaponsAttained = EnumSet.of(MEGA_BUSTER);
    private final Set<Boss> defeatedBosses = EnumSet.noneOf(Boss.class);
    private final Map<Integer, Integer> healthTanks = new HashMap<>();

    private int credits;

    public void addSpecialAbilityToMegaman(MegamanSpecialAbility megamanSpecialAbility) {
        megamanSpecialAbilities.add(megamanSpecialAbility);
    }

    public boolean megamanHasSpecialAbility(MegamanSpecialAbility megamanSpecialAbility) {
        return megamanSpecialAbilities.contains(megamanSpecialAbility);
    }

    public void addWeaponToMegaman(MegamanWeapon megamanWeapon) {
        megamanWeaponsAttained.add(megamanWeapon);
    }

    public boolean megamanHasWeapon(MegamanWeapon megamanWeapon) {
        return megamanWeaponsAttained.contains(megamanWeapon);
    }

    public void setBossToDefeated(Boss boss) {
        defeatedBosses.add(boss);
    }

    public boolean isBossDefeated(Boss boss) {
        return defeatedBosses.contains(boss);
    }

    public boolean areAllBossesDefeated() {
        for (Boss boss : Boss.values()) {
            if (!isBossDefeated(boss)) {
                return false;
            }
        }
        return true;
    }

    public void addHealthTank(int healthTankNumber) {
        healthTanks.put(healthTankNumber, 0);
    }

    public void addHealthToHealthTank(int healthTankNumber, int health) {
        Integer currentHealth = healthTanks.get(healthTankNumber);
        healthTanks.put(healthTankNumber, boundNumber(currentHealth + health, 0, MegamanVals.MEGAMAN_MAX_HEALTH));
    }

    public int getHealthTankHealthAndPurge(int healthTankNumber) {
        int health = healthTanks.get(healthTankNumber);
        healthTanks.replace(healthTankNumber, 0);
        return health;
    }

    public void setCredits(int credits) {
        this.credits = boundNumber(credits, 0, 99);
    }

}
