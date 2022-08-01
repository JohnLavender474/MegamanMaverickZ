package com.game.entities.enemies;

import com.badlogic.gdx.math.Rectangle;
import com.game.Entity;
import com.game.GameContext2d;
import com.game.cull.CullOnCamTransComponent;
import com.game.cull.CullOutOfCamBoundsComponent;
import com.game.damage.DamageNegotiation;
import com.game.damage.Damageable;
import com.game.damage.Damager;
import com.game.entities.decorations.Disintegration;
import com.game.entities.decorations.Explosion;
import com.game.entities.megaman.Megaman;
import com.game.health.HealthComponent;
import com.game.sounds.SoundComponent;
import com.game.sprites.SpriteAdapter;
import com.game.updatables.Updatable;
import com.game.utils.enums.Position;
import com.game.utils.objects.Timer;
import com.game.utils.objects.Wrapper;
import com.game.world.BodyComponent;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.function.Supplier;

import static com.game.ConstVals.SoundAsset.*;
import static com.game.entities.contracts.Facing.*;
import static com.game.utils.enums.Position.*;

public abstract class AbstractEnemy extends Entity implements Damager, Damageable {

    protected final GameContext2d gameContext;
    protected final Timer damageTimer = new Timer();
    protected final Supplier<Megaman> megamanSupplier;
    protected final Map<Class<? extends Damager>, DamageNegotiation> damageNegotiations = new HashMap<>();

    public AbstractEnemy(GameContext2d gameContext, Supplier<Megaman> megamanSupplier, float damageDuration) {
        this.gameContext = gameContext;
        this.megamanSupplier = megamanSupplier;
        damageTimer.setDuration(damageDuration);
        damageTimer.setToEnd();
        addComponent(new SoundComponent());
        addComponent(new CullOnCamTransComponent());
        addComponent(new HealthComponent(30, this::disintegrate));
        addComponent(new CullOutOfCamBoundsComponent(() -> getComponent(BodyComponent.class).getCollisionBox(), 1.5f));
    }

    @Override
    public Set<Class<? extends Damager>> getDamagerMaskSet() {
        return damageNegotiations.keySet();
    }

    @Override
    public boolean isInvincible() {
        return !damageTimer.isFinished();
    }

    @Override
    public void takeDamageFrom(Damager damager) {
        DamageNegotiation damageNegotiation = damageNegotiations.get(damager.getClass());
        if (damageNegotiation == null) {
            return;
        }
        damageTimer.reset();
        damageNegotiation.runOnDamage();
        getComponent(HealthComponent.class).sub(damageNegotiation.damage());
        getComponent(SoundComponent.class).requestSound(ENEMY_DAMAGE_SOUND);
    }

    protected void disintegrate() {
        getComponent(SoundComponent.class).requestSound(ENEMY_DAMAGE_SOUND);
        gameContext.addEntity(new Disintegration(gameContext, getComponent(BodyComponent.class).getCenter()));
    }

    protected void explode() {
        gameContext.addEntity(new Explosion(gameContext, getComponent(BodyComponent.class).getCenter()));
        getComponent(SoundComponent.class).requestSound(EXPLOSION_SOUND);
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

    protected class StandardEnemyUpdater implements Updatable {

        @Override
        public void update(float delta) {
            damageTimer.update(delta);
        }

    }

    protected class StandardEnemySpriteAdapter implements SpriteAdapter {

        private final Timer blinkTimer = new Timer(.05f);

        @Override
        public void update(float delta) {
            if (isInvincible()) {
                blinkTimer.update(delta);
            } else {
                blinkTimer.reset();
            }
        }

        @Override
        public boolean setPositioning(Wrapper<Rectangle> bounds, Wrapper<Position> position) {
            bounds.setData(getComponent(BodyComponent.class).getCollisionBox());
            position.setData(BOTTOM_CENTER);
            return true;
        }

        @Override
        public boolean isHidden() {
            if (isInvincible() && blinkTimer.isFinished()) {
                blinkTimer.reset();
                return true;
            }
            return false;
        }

    }

}
