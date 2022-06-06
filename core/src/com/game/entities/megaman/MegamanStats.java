package com.game.entities.megaman;

import com.game.utils.MutableInt;
import com.game.utils.Percentage;
import lombok.Getter;
import lombok.Setter;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@Getter
@Setter
public class MegamanStats {
    private MutableInt credits = MutableInt.of(0);
    private Percentage health = Percentage.of(100);
    private Map<Integer, Percentage> healthTanks = new HashMap<>();
    private Set<MegamanSpecialAbility> specialAbilities = new HashSet<>();
    private Map<MegamanSpecialWeapon, Percentage> specialWeapons = new HashMap<>();
}
