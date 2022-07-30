package com.game.damage;

public record DamageNegotiation(Integer damage, Runnable runnable) {

    public DamageNegotiation(Integer damage) {
        this(damage, () -> {});
    }

    public void runOnDamage() {
        runnable().run();
    }

}
