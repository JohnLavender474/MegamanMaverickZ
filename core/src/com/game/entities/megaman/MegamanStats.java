package com.game.entities.megaman;

import com.game.utils.objects.KeyValuePair;
import com.game.utils.objects.Wrapper;
import lombok.Getter;
import lombok.Setter;

import java.util.EnumMap;
import java.util.Map;
import java.util.function.Consumer;

import static com.game.entities.megaman.MegamanVals.MAX_HEALTH_TANKS;
import static com.game.entities.megaman.MegamanVals.MAX_WEAPON_AMMO;
import static com.game.health.HealthVals.MAX_HEALTH;
import static java.util.Arrays.fill;

/**
 * This class contains megaman's stats. It also has some methods that directly affect megaman during gameplay.
 */
public class MegamanStats {

    private final Map<MegamanSpecialAbility, Boolean> specialAbilities = new EnumMap<>(MegamanSpecialAbility.class);
    private final Map<MegamanWeapon, KeyValuePair<Integer, Boolean>> weapons = new EnumMap<>(MegamanWeapon.class);
    private final int[] healthTanks = new int[MAX_HEALTH_TANKS];

    @Getter
    @Setter
    private boolean weaponsChargeable;
    @Getter
    @Setter
    private int credits = 0;
    @Setter
    private Consumer<MegamanWeapon> weaponSetter;

    /**
     * Instantiate megaman stats.
     */
    public MegamanStats() {
        fill(healthTanks, -1);
    }

    /**
     * Set megaman's current weapon. Requires that weapon setter is set.
     *
     * @param weapon the weapon to set to
     * @return if megaman's weapon can be set to the specified weapon
     */
    public boolean setWeapon(MegamanWeapon weapon) {
        if (weaponSetter == null || !hasWeapon(weapon)) {
            return false;
        }
        weaponSetter.accept(weapon);
        return true;
    }

    /**
     * Add a special ability to megaman. Specify the state of the special ability.
     *
     * @param specialAbility the special ability to add
     * @param state if the special ability is to be active when it's added
     */
    public void putSpecialAbility(MegamanSpecialAbility specialAbility, boolean state) {
        specialAbilities.put(specialAbility, state);
    }

    /**
     * Return if megaman has the special ability
     *
     * @param specialAbility the special ability
     * @return if megaman has the special ability
     */
    public boolean hasSpecialAbility(MegamanSpecialAbility specialAbility) {
        return specialAbilities.containsKey(specialAbility);
    }

    /**
     * If megaman can use the special ability
     *
     * @param specialAbility the special ability
     * @return if megaman can use the special ability
     */
    public boolean canUseSpecialAbility(MegamanSpecialAbility specialAbility) {
        return hasSpecialAbility(specialAbility) && specialAbilities.get(specialAbility);
    }

    /**
     * Put the weapon into megaman's repertoire!
     *
     * @param weapon the weapon to put
     */
    public void putWeapon(MegamanWeapon weapon) {
        putWeapon(weapon, true);
    }

    /**
     * Put the weapon into megaman's repertoire! Specify the state of can shoot.
     *
     * @param weapon the weapon
     * @param state the can shoot state
     */
    public void putWeapon(MegamanWeapon weapon, boolean state) {
        putWeapon(weapon, MAX_WEAPON_AMMO, state);
    }

    /**
     * Put the weapon into megaman's repertoire. Specify the state of can shoot and the ammo amount.
     *
     * @param weapon the weapon
     * @param ammo the ammo amount
     * @param state the can shoot state
     */
    public void putWeapon(MegamanWeapon weapon, int ammo, boolean state) {
        int val = ammo;
        if (val > MAX_WEAPON_AMMO) {
            val = MAX_WEAPON_AMMO;
        } else if (val < 0) {
            val = 0;
        }
        weapons.put(weapon, new KeyValuePair<>(val, state));
    }

    /**
     * Return if megaman has the weapon.
     *
     * @param weapon the weapon
     * @return if megaman has the weapon
     */
    public boolean hasWeapon(MegamanWeapon weapon) {
        return weapons.containsKey(weapon);
    }

    /**
     * Return if megaman can use the weapon. If megaman does not have the weapon, then return false immediately and
     * write -1 to the supplied wrapper. The charge status is indicated by an int where 2 = fully charge,
     * 1 = halfway charged, and 0 = not charged. Each charge status has a different cost. If the cost for the
     * specified charge status is greater than the weapon ammo, then return false and write the next best charge
     * status and write the charge status value to the supplied wrapper. If no charge status can be achieved, then
     * the wrapper will contain the value -1.
     *
     * @param weapon the weapon
     * @param chargeStatus the charge status
     * @param nextBestChargeStatus the next best charge status is any, or written with data -1 if none is possible
     * @return if megaman can use the weapon with the given charge status
     */
    public boolean canUseWeapon(MegamanWeapon weapon, int chargeStatus, Wrapper<Integer> nextBestChargeStatus) {
        if (!hasWeapon(weapon) || !weapons.get(weapon).value()) {
            nextBestChargeStatus.setData(-1);
            return false;
        }
        int ammo = weapons.get(weapon).key();
        boolean canShootRequest = true;
        int cost;
        if (chargeStatus == 2) {
            cost = weapon.getFullyChargedCost();
            if (ammo < cost) {
                chargeStatus = 1;
                canShootRequest = false;
            }
        }
        if (chargeStatus == 1) {
            cost = weapon.getHalfChargedCost();
            if (ammo < cost) {
                chargeStatus = 0;
                canShootRequest = false;
            }
        }
        if (chargeStatus == 0) {
            cost = weapon.getCost();
            if (ammo < cost) {
                chargeStatus = -1;
                canShootRequest = false;
            }
        }
        if (!canShootRequest) {
            nextBestChargeStatus.setData(chargeStatus);
        }
        return canShootRequest;
    }

    /**
     * Chage the ammo amount by the specified delta
     *
     * @param weapon the weapon whose ammo should be changed
     * @param delta the amount to change the ammo by
     */
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

    /**
     * Get the amount of ammo for the weapon.
     *
     * @param weapon the weapon
     * @return the amount of ammo for the weapon
     */
    public int getWeaponAmmo(MegamanWeapon weapon) {
        return weapons.get(weapon).key();
    }

    /**
     * Add a new health tank mapped to the health tank index value.
     *
     * @param healthTank the health tank index value
     */
    public void addHealthTank(int healthTank) {
        addHealthTank(healthTank, 0);
    }

    /**
     * Add a new health tank mapped to the health tank index value. Specify initial fill amount.
     *
     * @param healthTank the health tank index value
     * @param initVal the initial fill amount
     */
    public void addHealthTank(int healthTank, int initVal) {
        setHealthTankValue(healthTank, initVal);
    }

    /**
     * Return if megaman has a health tank mapped to the health tank index value.
     *
     * @param healthTank the health tank index value
     * @return if megaman has the health tank
     */
    public boolean hasHealthTank(int healthTank) {
        return healthTank >= 0 && healthTank < MAX_HEALTH_TANKS && healthTanks[healthTank] != -1;
    }

    /**
     * Return if megaman can use the health tank.
     *
     * @param healthTank the health tank index value
     * @return if megaman can use the health tank
     */
    public boolean canBeUsed(int healthTank) {
        return hasHealthTank(healthTank) && getHealthTankValue(healthTank) > 0;
    }

    /**
     * Get the amount of health in the health tank mapped to the health tank index value.
     *
     * @param healthTank the health tank index value
     * @return the amount of health in the health tank
     */
    public int getHealthTankValue(int healthTank) {
        if (!hasHealthTank(healthTank)) {
            throw new IllegalArgumentException("Megaman does not have health tank " + healthTank + " yet");
        }
        return healthTanks[healthTank];
    }

    /**
     * Set the amount of health in the health tank mapped to the health tank index value.
     *
     * @param healthTank the health tank index value.
     * @param healthTankValue the amount of health
     */
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

    /**
     * Add health to the health tank mapped to the health tank index value.
     *
     * @param healthTank the health tank index value
     * @param delta the amount of health to add
     * @return the amount of health left over if there is excess
     */
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

    /**
     * Return if the health tank mapped to the health tank index value if full.
     *
     * @param healthTank the health tank index value
     * @return if the health tank is full
     */
    public boolean isHealthTankFull(int healthTank) {
        if (!hasHealthTank(healthTank)) {
            throw new IllegalArgumentException("Megaman does not have health tank " + healthTank + " yet");
        }
        return healthTanks[healthTank] == MAX_HEALTH;
    }

}
