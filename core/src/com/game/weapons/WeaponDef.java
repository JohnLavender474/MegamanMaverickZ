package com.game.weapons;

import com.game.entities.Entity;
import com.game.utils.objects.Timer;
import lombok.Getter;

import java.util.Collection;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Function;

@Getter
public class WeaponDef {

    private final Function<Map<String, Object>, Collection<Entity>> weaponFunction;
    private final Consumer<Map<String, Object>> runOnShoot;
    private final Timer shootCooldownTimer;

    public WeaponDef(Function<Map<String, Object>, Collection<Entity>> weaponFunction,
                     float shootCooldown, Consumer<Map<String, Object>> runOnShoot) {
        this.runOnShoot = runOnShoot;
        this.weaponFunction = weaponFunction;
        this.shootCooldownTimer = new Timer(shootCooldown);
        this.shootCooldownTimer.setToEnd();
    }

    public void runOnShoot(Map<String, Object> m) {
        runOnShoot.accept(m);
    }

    public Collection<Entity> getWeaponsInstances(Map<String, Object> m) {
        return weaponFunction.apply(m);
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
