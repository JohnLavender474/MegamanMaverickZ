package com.game.entities.megaman;

import com.game.utils.objects.Percentage;
import lombok.Getter;
import lombok.Setter;

import java.util.*;

@Getter
@Setter
public class MegamanStats {
    private int credits;
    private int maxHealth;
    private Map<Integer, Integer> healthTanks = new HashMap<>();
    private Map<MegamanWeapon, Percentage> specialWeapons = new EnumMap<>(MegamanWeapon.class);
    private Set<MegamanSpecialAbility> specialAbilities = EnumSet.noneOf(MegamanSpecialAbility.class);
}
