package com.game.damage;

import com.game.System;
import com.game.core.IEntity;
import com.game.health.HealthComponent;
import com.game.utils.Timer;

import java.util.Set;

public class DamageSystem extends System {

    public DamageSystem() {
        super(Set.of(DamageComponent.class, HealthComponent.class));
    }

    @Override
    protected void processEntity(IEntity entity, float delta) {
        // get damageable along with damager def if it exists
        DamageComponent damageable = entity.getComponent(DamageComponent.class);
        DamagerDef damagerDef = damageable.getDamagerDef();
        // if damager def is null or damage process cannot happen, then reset and return
        if (damagerDef == null || damageable.isInvincible() || !damageable.canBeDamagedBy(damagerDef) ||
                !damagerDef.canDamage(damageable)) {
            damageable.setDamagerDef(null);
            damageable.getDamageTimer().setToEnd();
            damageable.getRecoveryTimer().setToEnd();
            return;
        }
        // perform damage process
        Timer damageTimer = damageable.getDamageTimer();
        if (damageTimer.isAtBeginning()) {
            damageable.initDamageProcess(delta);
            damagerDef.onDamageInflictedTo(damageable);
            // subtract health from entity on damage init
            entity.getComponent(HealthComponent.class).sub(damageable.getDamageAmount(damagerDef));
        }
        damageTimer.update(delta);
        if (!damageTimer.isFinished()) {
            damageable.continueDamageProcess(delta);
        }
        Timer recoveryTimer = damageable.getRecoveryTimer();
        if (damageTimer.isJustFinished()) {
            damageable.endDamageProcess(delta);
            // on damage end, start recovery process
            recoveryTimer.reset();
        }
        // perform recovery process
        if (recoveryTimer.isAtBeginning()) {
            damageable.initRecoveryProcess(delta);
        }
        recoveryTimer.update(delta);
        if (!recoveryTimer.isFinished()) {
            damageable.continueRecoveryProcess(delta);
        }
        if (recoveryTimer.isJustFinished()) {
            damageable.endRecoveryProcess(delta);
            // detach damager def from damageable on recovery end
            damageable.setDamagerDef(null);
        }
    }

}
