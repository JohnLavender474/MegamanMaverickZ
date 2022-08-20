package com.game.core;

import com.game.entities.megaman.MegamanSpecialAbility;
import com.game.entities.megaman.MegamanWeapon;
import com.game.utils.objects.Percentage;
import lombok.Getter;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.function.Supplier;

import static com.game.core.ConstVals.*;
import static com.game.core.ConstVals.MegamanVals.*;
import static com.game.entities.megaman.MegamanWeapon.*;
import static com.game.utils.UtilMethods.boundNumber;

@Getter
public class MegamanGameInfo {

    public static final int MAX_HEALTH_TANKS = 4;

    private final Set<MegamanSpecialAbility> megamanSpecialAbilities = EnumSet.noneOf(MegamanSpecialAbility.class);
    private final Set<MegamanWeapon> megamanWeaponsAttained = EnumSet.of(MEGA_BUSTER);
    private final Percentage[] healthTanks = new Percentage[MAX_HEALTH_TANKS];
    private final Set<Boss> defeatedBosses = EnumSet.noneOf(Boss.class);

    private int credits = 0;

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

    public Percentage getHealthTank(int healthTankNumber) {
        return healthTanks[healthTankNumber];
    }

    public void addHealthTank(int healthTankNumber) {
        healthTanks[healthTankNumber] = Percentage.of(0);
    }

    public void addHealthToHealthTank(int healthTankNumber, int health) {
        Percentage currentHealth = healthTanks[healthTankNumber];
        currentHealth.translate(health);
    }

    public int getHealthTankHealthAndPurge(int healthTankNumber) {
        Percentage healthPer = healthTanks[healthTankNumber];
        int health = healthPer.getAsWholeNumber();
        healthPer.setPercentage(0);
        return health;
    }

    public void setCredits(int credits) {
        this.credits = boundNumber(credits, 0, 99);
    }

    public Supplier<Integer> getCreditsSupplier() {
        return () -> credits;
    }

}
