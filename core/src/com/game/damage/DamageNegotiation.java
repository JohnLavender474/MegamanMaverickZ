package com.game.damage;

import java.util.function.Function;

public class DamageNegotiation {

    private final Function<Damager, Integer> damageFunction;
    private final Runnable runOnDamage;

    public DamageNegotiation(int damage) {
        this(damage, () -> {});
    }

    public DamageNegotiation(int damage, Runnable runOnDamage) {
        this(damager -> damage, runOnDamage);
    }

    public DamageNegotiation(Function<Damager, Integer> damageFunction) {
        this(damageFunction, () -> {});
    }

    public DamageNegotiation(Function<Damager, Integer> damageFunction, Runnable runOnDamage) {
        this.damageFunction = damageFunction;
        this.runOnDamage = runOnDamage;
    }

    public int getDamage(Damager damager) {
        return damageFunction.apply(damager);
    }

    public void runOnDamage() {
        runOnDamage.run();
    }

}
