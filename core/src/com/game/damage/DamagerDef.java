package com.game.damage;

import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.util.function.Consumer;
import java.util.function.Function;

@Setter
@RequiredArgsConstructor
public class DamagerDef {

    private final Function<DamageComponent, Boolean> canDamageFunc;
    private final Consumer<DamageComponent> onDamageInflictedTo;

    public DamagerDef() {
        this(damageComponent -> true);
    }
    
    public DamagerDef(Function<DamageComponent, Boolean> canDamageFunc) {
        this.canDamageFunc = canDamageFunc;
        this.onDamageInflictedTo = damageComponent -> {};
    }

    public boolean canDamage(DamageComponent damageComponent) {
        return canDamageFunc.apply(damageComponent);
    }

    public void onDamageInflictedTo(DamageComponent damageComponent) {
        if (onDamageInflictedTo == null) {
            return;
        }
        onDamageInflictedTo.accept(damageComponent);
    }

}
