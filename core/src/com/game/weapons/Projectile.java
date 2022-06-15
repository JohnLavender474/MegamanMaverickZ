package com.game.weapons;

import com.badlogic.gdx.math.Vector2;
import com.game.Entity;
import lombok.RequiredArgsConstructor;

import java.util.function.Supplier;

@RequiredArgsConstructor
public abstract class Projectile {

    protected final Entity owner;
    protected final Supplier<Vector2> spawnSupplier;
    protected final Supplier<Vector2> trajectorySupplier;

    public abstract void shoot();

}
