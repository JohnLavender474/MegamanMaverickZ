package com.game.entities.enemies;

import com.badlogic.gdx.audio.Sound;
import com.game.Entity;
import com.game.GameContext2d;
import com.game.cull.CullOnCamTransComponent;
import com.game.cull.CullOutOfCamBoundsComponent;
import com.game.damage.Damageable;
import com.game.damage.Damager;
import com.game.entities.decorations.Disintegration;
import com.game.entities.megaman.Megaman;
import com.game.health.HealthComponent;
import com.game.utils.objects.Timer;
import com.game.world.BodyComponent;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.function.Supplier;

import static com.game.ConstVals.SoundAssets.*;
import static com.game.entities.contracts.Facing.*;

public abstract class AbstractEnemy extends Entity implements Damager, Damageable {

    protected final GameContext2d gameContext;
    protected final Supplier<Megaman> megamanSupplier;

    protected final Map<Class<? extends Damager>, Integer> damageNegotiation = new HashMap<>();
    protected final Timer damageTimer = new Timer();

    public AbstractEnemy(GameContext2d gameContext, Supplier<Megaman> megamanSupplier) {
        this.gameContext = gameContext;
        this.megamanSupplier = megamanSupplier;
        addComponent(new CullOnCamTransComponent());
        addComponent(new HealthComponent(30, this::disintegrate));
        addComponent(new CullOutOfCamBoundsComponent(() -> getComponent(BodyComponent.class).getCollisionBox(), 1.5f));
    }

    @Override
    public Set<Class<? extends Damager>> getDamagerMaskSet() {
        return damageNegotiation.keySet();
    }

    @Override
    public boolean isInvincible() {
        return !damageTimer.isFinished();
    }

    @Override
    public void takeDamageFrom(Damager damager) {
        damageTimer.reset();
        gameContext.getAsset(ENEMY_DAMAGE_SOUND, Sound.class).play();
        getComponent(HealthComponent.class).sub(damageNegotiation.get(damager.getClass()));
    }

    protected void disintegrate() {
        gameContext.getAsset(ENEMY_DAMAGE_SOUND, Sound.class).play();
        gameContext.addEntity(new Disintegration(gameContext, getComponent(BodyComponent.class).getCenter()));
    }

    protected Megaman getMegaman() {
        return megamanSupplier.get();
    }

    protected boolean playerIsAttacking() {
        BodyComponent thisBody = getComponent(BodyComponent.class);
        Megaman megaman = getMegaman();
        BodyComponent playerBody = megaman.getComponent(BodyComponent.class);
        return megaman.isShooting() &&
                ((thisBody.getPosition().x < playerBody.getPosition().x && megaman.isFacing(F_LEFT)) ||
                        (thisBody.getPosition().x > playerBody.getPosition().x && megaman.isFacing(F_RIGHT)));
    }

}
