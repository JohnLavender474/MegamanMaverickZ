package com.mygdx.game.entities.megaman;

import com.mygdx.game.Entity;
import lombok.Getter;

import java.util.Collection;
import java.util.EnumSet;
import java.util.Set;

@Getter
public class Megaman extends Entity {

    private final Set<MegamanWeapon> megamanWeapons = EnumSet.noneOf(MegamanWeapon.class);

    public Megaman() {
        this(9, 100, EnumSet.noneOf(MegamanWeapon.class));
    }

    public Megaman(int lives, int health, Collection<MegamanWeapon> megamanWeapons) {
        this.megamanWeapons.addAll(megamanWeapons);
    }

}
