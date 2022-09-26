package com.game.entities.megaman;

import com.game.utils.objects.KeyValuePair;
import lombok.Getter;
import lombok.Setter;

import java.util.EnumMap;
import java.util.Map;

import static com.game.entities.megaman.MegamanVals.MAX_HEALTH_TANKS;
import static com.game.entities.megaman.MegamanVals.MAX_WEAPON_AMMO;
import static com.game.health.HealthVals.MAX_HEALTH;
import static java.util.Arrays.fill;

public class MegamanInfo {

    private final Map<MegamanSpecialAbility, Boolean> specialAbilities = new EnumMap<>(MegamanSpecialAbility.class);
    private final Map<MegamanWeapon, KeyValuePair<Integer, Boolean>> weapons = new EnumMap<>(MegamanWeapon.class);
    private final int[] healthTanks = new int[MAX_HEALTH_TANKS];

    @Getter
    @Setter
    private boolean weaponsChargeable;
    @Getter
    @Setter
    private int credits = 0;

    public MegamanInfo() {
        fill(healthTanks, -1);
    }

    public void putSpecialAbility(MegamanSpecialAbility specialAbility, boolean state) {
        specialAbilities.put(specialAbility, state);
    }

    public boolean hasSpecialAbility(MegamanSpecialAbility specialAbility) {
        return specialAbilities.containsKey(specialAbility);
    }

    public boolean canUseSpecialAbility(MegamanSpecialAbility specialAbility) {
        return hasSpecialAbility(specialAbility) && specialAbilities.get(specialAbility);
    }

    public void putWeapon(MegamanWeapon weapon) {
        putWeapon(weapon, true);
    }

    public void putWeapon(MegamanWeapon weapon, boolean state) {
        putWeapon(weapon, MAX_WEAPON_AMMO, state);
    }

    public void putWeapon(MegamanWeapon weapon, int ammo, boolean state) {
        int val = ammo;
        if (val > MAX_WEAPON_AMMO) {
            val = MAX_WEAPON_AMMO;
        } else if (val < 0) {
            val = 0;
        }
        weapons.put(weapon, new KeyValuePair<>(val, state));
    }

    public boolean hasWeapon(MegamanWeapon weapon) {
        return weapons.containsKey(weapon);
    }

    public boolean canUseWeapon(MegamanWeapon weapon, int chargeStatus) {
        if (!hasWeapon(weapon)) {
            return false;
        }
        if (!weapons.get(weapon).value()) {
            return false;
        }
        int cost;
        switch (chargeStatus) {
            case 0 -> cost = weapon.getCost();
            case 1 -> cost = weapon.getHalfChargedCost();
            case 2 -> cost = weapon.getFullyChargedCost();
            default -> throw new IllegalArgumentException("Charge status " + chargeStatus + " is invalid");
        }
        int ammo = weapons.get(weapon).key();
        return ammo >= cost;
    }

    public void translateWeaponAmmo(MegamanWeapon weapon, int delta) {
        if (!hasWeapon(weapon)) {
            throw new IllegalArgumentException("Megaman does not currently have weapon " + weapon);
        }
        KeyValuePair<Integer, Boolean> p = weapons.get(weapon);
        int val = p.key() + delta;
        if (val > MAX_WEAPON_AMMO) {
            val = MAX_WEAPON_AMMO;
        } else if (val < 0) {
            val = 0;
        }
        putWeapon(weapon, val, p.value());
    }

    public void addHealthTank(int healthTank) {
        addHealthTank(healthTank, 0);
    }

    public void addHealthTank(int healthTank, int initVal) {
        setHealthTankValue(healthTank, initVal);
    }

    public boolean hasHealthTank(int healthTank) {
        return healthTank >= 0 && healthTank < MAX_HEALTH_TANKS && healthTanks[healthTank] != -1;
    }

    public int getHealthTankValue(int healthTank) {
        if (!hasHealthTank(healthTank)) {
            throw new IllegalArgumentException("Megaman does not have health tank " + healthTank + " yet");
        }
        return healthTanks[healthTank];
    }

    public void setHealthTankValue(int healthTank, int healthTankValue) {
        if (!hasHealthTank(healthTank)) {
            throw new IllegalArgumentException("Megaman does not have health tank " + healthTank + " yet");
        }
        int val = healthTankValue;
        if (val > MAX_HEALTH) {
            val = MAX_HEALTH;
        } else if (val < 0) {
            val = 0;
        }
        healthTanks[healthTank] = val;
    }

    public int addHealthTankValue(int healthTank, int delta) {
        if (delta < 0) {
            throw new IllegalArgumentException("Delta cannot be less than zero");
        }
        if (!hasHealthTank(healthTank)) {
            throw new IllegalArgumentException("Megaman does not have health tank " + healthTank + " yet");
        }
        int val = healthTanks[healthTank] + delta;
        int leftover = 0;
        if (val > MAX_HEALTH) {
            int temp = MAX_HEALTH - healthTanks[healthTank];
            leftover = delta - temp;
            setHealthTankValue(healthTank, MAX_HEALTH);
        } else {
            healthTanks[healthTank] += delta;
        }
        return leftover;
    }

    public boolean isHealthTankFull(int healthTank) {
        if (!hasHealthTank(healthTank)) {
            throw new IllegalArgumentException("Megaman does not have health tank " + healthTank + " yet");
        }
        return healthTanks[healthTank] == MAX_HEALTH;
    }

}
