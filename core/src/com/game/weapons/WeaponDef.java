package com.game.weapons;

import com.game.core.Entity;
import com.game.utils.objects.Percentage;
import com.game.utils.objects.Timer;
import lombok.Getter;

import java.util.Collection;
import java.util.function.Supplier;

@Getter
public class WeaponDef {

    private final Supplier<Collection<Entity>> weaponsSupplier;
    private final Percentage percentage = Percentage.of(100);
    private final Timer shootCooldownTimer;
    private final Runnable runOnShoot;

    public WeaponDef(Supplier<Collection<Entity>> weaponsSupplier, float shootCooldown, Runnable runOnShoot) {
        this.runOnShoot = runOnShoot;
        this.weaponsSupplier = weaponsSupplier;
        this.shootCooldownTimer = new Timer(shootCooldown);
        this.shootCooldownTimer.setToEnd();
    }

    public WeaponDef(Supplier<Collection<Entity>> weaponsSupplier, float shootCooldown) {
        this(weaponsSupplier, shootCooldown, () -> {});
    }

    public void runOnShoot() {
        runOnShoot.run();
    }

    public Collection<Entity> getWeaponsInstances() {
        return weaponsSupplier.get();
    }

    public void translatePercentage(int percentage) {
        this.percentage.translate(percentage);
    }

    public boolean isDepleted() {
        return percentage.isZero();
    }

    public int getWeaponPercentage() {
        return percentage.getAsWholeNumber();
    }

    public void updateCooldownTimer(float delta) {
        shootCooldownTimer.update(delta);
    }

    public void resetCooldownTimer() {
        shootCooldownTimer.reset();
    }

    public boolean isCooldownTimerFinished() {
        return shootCooldownTimer.isFinished();
    }

}
