package com.game.entities.megaman;

import com.game.utils.MutableInt;
import com.game.utils.Percentage;
import lombok.Getter;
import lombok.Setter;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static com.game.ConstVals.MegamanVals.MAX_HEALTH_TANKS;

@Getter
@Setter
public class MegamanStats {
    private MutableInt credits = MutableInt.of(0);
    private Percentage health = Percentage.of(100);
    private Percentage[] healthTanks = new Percentage[MAX_HEALTH_TANKS];
    private Set<MegamanSpecialAbility> specialAbilities = new HashSet<>();
    private Map<MegamanSpecialWeapon, Percentage> specialWeapons = new HashMap<>();
}
