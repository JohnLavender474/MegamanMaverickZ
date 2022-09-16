package com.game.core;

import com.game.core.constants.Boss;
import com.game.entities.megaman.MegamanSpecialAbility;
import com.game.entities.megaman.MegamanWeapon;
import com.game.utils.objects.Percentage;
import lombok.Getter;
import lombok.Setter;

import java.util.EnumSet;
import java.util.Set;
import java.util.function.Supplier;

import static com.game.entities.megaman.MegamanWeapon.*;

@Getter
@Setter
public class MegamanGameInfo {

    public static final int MAX_HEALTH_TANKS = 4;

    private final Set<MegamanSpecialAbility> megamanSpecialAbilities = EnumSet.noneOf(MegamanSpecialAbility.class);
    private final Set<MegamanWeapon> megamanWeaponsAttained = EnumSet.of(MEGA_BUSTER);
    private final Percentage[] healthTanks = new Percentage[MAX_HEALTH_TANKS];
    private final Set<Boss> defeatedBosses = EnumSet.noneOf(Boss.class);

    private boolean canChargeWeapons = true;
    private int credits = 0;

    public Supplier<Integer> getCreditsSupplier() {
        return () -> credits;
    }

    public Supplier<Boolean> canChargeWeaponsSupplier() {
        return () -> canChargeWeapons;
    }

}
