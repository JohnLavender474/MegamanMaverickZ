package com.game.damageable;

import com.game.Component;
import com.game.System;
import com.game.core.IEntity;

import java.util.Set;

public class DamageableSystem extends System {

    @Override
    public Set<Class<? extends Component>> getComponentMask() {
        return Set.of(DamageableComponent.class);
    }

    @Override
    protected void processEntity(IEntity entity, float delta) {
        DamageableComponent damageableComponent = entity.getComponent(DamageableComponent.class);
        if (damageableComponent.isDamaged()) {
            damageableComponent.updateDamageTimer(delta);
        }
        if (damageableComponent.isRecovering()) {
            damageableComponent.updateRecoveryTimer(delta);
        }
        if (!damageableComponent.isInvincible() && damageableComponent.isHitByDamagers()) {
            damageableComponent.resetDamageTimer();

        }
    }

}
