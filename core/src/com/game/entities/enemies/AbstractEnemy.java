package com.game.entities.enemies;

import com.badlogic.gdx.math.Rectangle;
import com.game.core.Entity;
import com.game.core.GameContext2d;
import com.game.cull.CullOnCamTransComponent;
import com.game.cull.CullOutOfCamBoundsComponent;
import com.game.damage.DamageNegotiation;
import com.game.damage.Damageable;
import com.game.damage.Damager;
import com.game.entities.decorations.Disintegration;
import com.game.entities.decorations.Explosion;
import com.game.entities.megaman.Megaman;
import com.game.graph.GraphComponent;
import com.game.health.HealthComponent;
import com.game.messages.MessageListener;
import com.game.sounds.SoundComponent;
import com.game.sprites.SpriteAdapter;
import com.game.updatables.Updatable;
import com.game.utils.enums.Position;
import com.game.utils.objects.Timer;
import com.game.utils.objects.Wrapper;
import com.game.world.BodyComponent;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Supplier;

import static com.game.core.ConstVals.Events.LEVEL_PAUSED;
import static com.game.core.ConstVals.Events.LEVEL_UNPAUSED;
import static com.game.core.ConstVals.SoundAsset.*;
import static com.game.entities.contracts.Facing.*;
import static com.game.utils.enums.Position.*;
import static java.util.Collections.unmodifiableSet;

public abstract class AbstractEnemy extends Entity implements MessageListener, Damager, Damageable {

    protected final GameContext2d gameContext;
    protected final Timer damageTimer = new Timer();
    protected final Supplier<Megaman> megamanSupplier;
    protected final Map<Class<? extends Damager>, DamageNegotiation> damageNegotiations;

    public AbstractEnemy(GameContext2d gameContext, Supplier<Megaman> megamanSupplier, float damageDuration) {
        this(gameContext, megamanSupplier, damageDuration, 1.5f);
    }

    public AbstractEnemy(GameContext2d gameContext, Supplier<Megaman> megamanSupplier,
                         float damageDuration, float cullDuration) {
        this.gameContext = gameContext;
        this.megamanSupplier = megamanSupplier;
        this.damageNegotiations = defineDamageNegotiations();
        addComponent(new SoundComponent());
        addComponent(defineGraphComponent());
        addComponent(new CullOnCamTransComponent());
        addComponent(new HealthComponent(30, this::disintegrate));
        addComponent(new CullOutOfCamBoundsComponent(
                () -> getComponent(BodyComponent.class).getCollisionBox(), cullDuration));
        damageTimer.setToEnd();
        damageTimer.setDuration(damageDuration);
        gameContext.addMessageListener(this);
    }

    protected abstract Map<Class<? extends Damager>, DamageNegotiation> defineDamageNegotiations();

    @Override
    public void listenToMessage(Object owner, Object message, float delta) {
        if (message.equals(LEVEL_PAUSED)) {
            getComponents().values().forEach(component -> {
                if (component instanceof CullOutOfCamBoundsComponent || component instanceof CullOnCamTransComponent) {
                    return;
                }
                component.setOn(false);
            });
        } else if (message.equals(LEVEL_UNPAUSED)) {
            getComponents().values().forEach(component -> {
                if (component instanceof CullOutOfCamBoundsComponent || component instanceof CullOnCamTransComponent) {
                    return;
                }
                component.setOn(true);
            });
        }
    }

    @Override
    public void onDeath() {
        gameContext.removeMessageListener(this);
    }

    protected GraphComponent defineGraphComponent() {
        return new GraphComponent(() -> getComponent(BodyComponent.class).getCollisionBox(), () -> List.of(this));
    }

    @Override
    public Set<Class<? extends Damager>> getDamagerMaskSet() {
        return unmodifiableSet(damageNegotiations.keySet());
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
