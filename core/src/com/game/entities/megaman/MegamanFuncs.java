package com.game.entities.megaman;

import com.game.health.HealthComponent;
import lombok.RequiredArgsConstructor;

import java.util.function.Supplier;

@RequiredArgsConstructor
public class MegamanFuncs {

    private final MegamanInfo megamanInfo;
    private final Supplier<Megaman> megamanSupplier;

    public boolean useHealthTank(int healthTank) {
        Megaman megaman = megamanSupplier.get();
        if (megaman == null || !megamanInfo.hasHealthTank(healthTank)) {
            return false;
        }
        int val = megamanInfo.getHealthTankValue(healthTank);
        megaman.getComponent(HealthComponent.class).add(val);
        megamanInfo.setHealthTankValue(healthTank, 0);
        return true;
    }

}
