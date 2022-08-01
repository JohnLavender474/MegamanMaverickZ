package com.game.entities.megaman;

import com.game.ConstVals.MegamanVals;
import lombok.Getter;

import java.util.*;

import static com.game.entities.megaman.MegamanWeapon.*;
import static com.game.utils.UtilMethods.*;

@Getter
public class MegamanStats {

    private final Set<MegamanWeapon> megamanWeapons = EnumSet.of(MEGA_BUSTER);
    private final Map<Integer, Integer> healthTanks = new HashMap<>();

    private int credits;

    public void setCredits(int credits) {
        this.credits = boundNumber(credits, 0, 99);
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

    public void addMegamanWeapon(MegamanWeapon megamanWeapon) {
        megamanWeapons.add(megamanWeapon);
    }

    public boolean hasMegamanWeapon(MegamanWeapon megamanWeapon) {
        return megamanWeapons.contains(megamanWeapon);
    }

}
