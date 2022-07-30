package com.game.weapons;

import com.game.core.IEntity;
import com.game.utils.objects.Percentage;
import com.game.utils.objects.Timer;
import lombok.Getter;

import java.util.function.Supplier;

@Getter
public class WeaponDef {

    private final Percentage percentage = Percentage.of(100);
    private final Supplier<IEntity> weaponSupplier;
    private final Timer shootCooldownTimer;
    private final Runnable runOnShoot;

    public WeaponDef(Supplier<IEntity> weaponSupplier, float shootCooldown, Runnable runOnShoot) {
        this.runOnShoot = runOnShoot;
        this.weaponSupplier = weaponSupplier;
        this.shootCooldownTimer = new Timer(shootCooldown);
        this.shootCooldownTimer.setToEnd();
    }

    public WeaponDef(Supplier<IEntity> weaponSupplier, float shootCooldown) {
        this(weaponSupplier, shootCooldown, () -> {});
    }

    public void runOnShoot() {
        runOnShoot.run();
    }

    public IEntity getWeaponInstance() {
        return weaponSupplier.get();
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
