package com.game.weapons;

import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.math.Vector2;
import com.game.Entity;
import com.game.utils.Timer;
import com.game.utils.Updatable;
import lombok.RequiredArgsConstructor;

import java.util.function.Supplier;

@RequiredArgsConstructor
public class Weapon implements Updatable {

    private final Entity owner;
    private final Timer cooldown;
    private final Sound soundEffect;
    private final Supplier<Boolean> canShoot;
    private final Supplier<Vector2> spawnSupplier;
    private final Supplier<Boolean> isShootRequested;
    private final Supplier<Vector2> trajectorySupplier;
    private final Class<? extends Projectile> projectileClass;

    @Override
    public void update(float delta) {
        if (!cooldown.isFinished()) {
            cooldown.update(delta);
        }
        if (cooldown.isFinished() && canShoot.get() && isShootRequested.get()) {

        }
    }

}
