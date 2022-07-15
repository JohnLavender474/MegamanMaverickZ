package com.game.damage;

import com.game.Component;
import com.game.utils.Timer;
import com.game.utils.UpdatableConsumer;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

import java.util.Map;
import java.util.function.Function;
import java.util.function.Supplier;

import static com.game.damage.DamageProcess.*;

@Getter
public class DamageComponent implements Component {

    private final Map<DamageProcess, UpdatableConsumer<DamagerDef>> damageProcessSteps;
    private final Function<DamagerDef, Boolean> damageAcceptanceFunc;
    private final Supplier<Boolean> invincibilitySupplier;
    private final Function<DamagerDef, Integer> damageFunc;
    private final Timer recoveryTimer;
    private final Timer damageTimer;

    @Setter(AccessLevel.PACKAGE)
    private DamagerDef damagerDef;

    public DamageComponent(Map<DamageProcess, UpdatableConsumer<DamagerDef>> damageProcessSteps,
                           Function<DamagerDef, Integer> damageFunc,
                           Function<DamagerDef, Boolean> damageAcceptanceFunc, float damageDuration) {
        this(damageProcessSteps, damageAcceptanceFunc, damageFunc, null, damageDuration, 0f);
    }

    public DamageComponent(Map<DamageProcess, UpdatableConsumer<DamagerDef>> damageProcessSteps,
                           Function<DamagerDef, Boolean> damageAcceptanceFunc,
                           Function<DamagerDef, Integer> damageFunc,
                           Supplier<Boolean> invincibilitySupplier, float damageDuration, float recoveryDuration) {
        this.invincibilitySupplier = invincibilitySupplier;
        this.damageAcceptanceFunc = damageAcceptanceFunc;
        this.recoveryTimer = new Timer(recoveryDuration);
        this.damageTimer = new Timer(damageDuration);
        this.damageProcessSteps = damageProcessSteps;
        this.damageFunc = damageFunc;
        recoveryTimer.setToEnd();
        damageTimer.setToEnd();
    }

    public void setDamagedBy(DamagerDef damagerDef) {
        this.damagerDef = damagerDef;
        damageTimer.reset();
    }

    int getDamageAmount(DamagerDef damagerDef) {
        return damageFunc.apply(damagerDef);
    }

    boolean isInvincible() {
        return !damageTimer.isFinished() || !recoveryTimer.isFinished() || invincibilitySupplier.get();
    }

    boolean canBeDamagedBy(DamagerDef damagerDef) {
        return damageAcceptanceFunc.apply(damagerDef);
    }

    void initDamageProcess(float delta) {
        UpdatableConsumer<DamagerDef> initDamage = damageProcessSteps.get(DAMAGE_INIT);
        if (initDamage != null) {
            initDamage.consumeAndUpdate(damagerDef, delta);
        }
    }

    void continueDamageProcess(float delta) {
        UpdatableConsumer<DamagerDef> continueDamage = damageProcessSteps.get(DAMAGE_CONTINUE);
        if (continueDamage != null) {
            continueDamage.consumeAndUpdate(damagerDef, delta);
        }
    }

    void endDamageProcess(float delta) {
        UpdatableConsumer<DamagerDef> endDamage = damageProcessSteps.get(DAMAGE_END);
        if (endDamage != null) {
            endDamage.consumeAndUpdate(damagerDef, delta);
        }
        recoveryTimer.reset();
    }

    void initRecoveryProcess(float delta) {
        UpdatableConsumer<DamagerDef> initRecovery = damageProcessSteps.get(RECOVERY_INIT);
        if (initRecovery != null) {
            initRecovery.consumeAndUpdate(damagerDef, delta);
        }
    }

    void continueRecoveryProcess(float delta) {
        UpdatableConsumer<DamagerDef> continueRecovery = damageProcessSteps.get(RECOVERY_CONTINUE);
        if (continueRecovery != null) {
            continueRecovery.consumeAndUpdate(damagerDef, delta);
        }
    }

    void endRecoveryProcess(float delta) {
        UpdatableConsumer<DamagerDef> endRecovery = damageProcessSteps.get(RECOVERY_END);
        if (endRecovery != null) {
            endRecovery.consumeAndUpdate(damagerDef, delta);
        }
    }

}
