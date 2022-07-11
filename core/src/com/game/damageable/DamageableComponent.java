package com.game.damageable;

import com.game.Component;
import com.game.utils.Timer;
import com.game.world.Fixture;
import lombok.*;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

public class DamageableComponent implements Component {

    private Timer damageTimer;
    private Timer recoveryTimer;
    private Runnable onRecovery;
    private Consumer<Fixture> onDamaged;
    private Supplier<Boolean> isInvincible;
    private Function<Fixture, Boolean> canBeDamaged;
    private Set<Fixture> damagerFixturesInContact = new HashSet<>();

    public DamageableComponent(Function<Fixture, Boolean> canBeDamaged, Consumer<Fixture> onDamaged,
                               Runnable onRecovery, Supplier<Boolean> isInvincible,
                               float damageDuration, float recoveryDuration) {
        this.onDamaged = onDamaged;
        this.onRecovery = onRecovery;
        this.canBeDamaged = canBeDamaged;
        this.isInvincible = isInvincible;
        this.damageTimer = new Timer(damageDuration);
        this.recoveryTimer = new Timer(recoveryDuration);
        this.damageTimer.setToEnd();
        this.recoveryTimer.setToEnd();
    }

    public void updateDamageTimer(float delta) {
        damageTimer.update(delta);
    }

    public void resetDamageTimer() {
        damageTimer.reset();
    }

    public boolean isDamaged() {
        return !damageTimer.isFinished();
    }

    public boolean isDamagedJustFinished() {
        return damageTimer.isJustFinished();
    }

    public void updateRecoveryTimer(float delta) {
        recoveryTimer.update(delta);
    }

    public void resetRecoveryTimer() {
        recoveryTimer.reset();
    }

    public boolean isRecovering() {
        return !recoveryTimer.isFinished();
    }

    public boolean isRecoveryJustFinished() {
        return recoveryTimer.isJustFinished();
    }

    public boolean isInvincible() {
        return isInvincible.get();
    }

    public void onDamaged(Fixture fixture) {
        damageTimer.reset();
        onDamaged.accept(fixture);
    }

    public boolean canBeDamaged(Fixture fixture) {
        return canBeDamaged.apply(fixture);
    }

    public void addHitByDamager(Fixture fixture) {
        damagerFixturesInContact.add(fixture);
    }

    public boolean isHitByDamagers() {
        return !damagerFixturesInContact.isEmpty();
    }

    public void clearHitByDamagers() {
        damagerFixturesInContact.clear();
    }

}
