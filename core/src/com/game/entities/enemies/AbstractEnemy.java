package com.game.entities.enemies;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Rectangle;
import com.game.GameContext2d;
import com.game.cull.CullOnMessageComponent;
import com.game.cull.CullOutOfCamBoundsComponent;
import com.game.damage.DamageNegotiation;
import com.game.damage.Damageable;
import com.game.damage.Damager;
import com.game.entities.Entity;
import com.game.entities.decorations.Disintegration;
import com.game.entities.decorations.Explosion;
import com.game.entities.megaman.Megaman;
import com.game.graph.GraphComponent;
import com.game.health.HealthComponent;
import com.game.sounds.SoundComponent;
import com.game.sprites.SpriteProcessor;
import com.game.utils.enums.Position;
import com.game.utils.interfaces.Updatable;
import com.game.utils.objects.Timer;
import com.game.utils.objects.Wrapper;
import com.game.world.BodyComponent;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Supplier;

import static com.game.assets.SoundAsset.ENEMY_DAMAGE_SOUND;
import static com.game.assets.SoundAsset.EXPLOSION_SOUND;
import static com.game.entities.contracts.Facing.F_LEFT;
import static com.game.entities.contracts.Facing.F_RIGHT;
import static com.game.health.HealthVals.MAX_HEALTH;
import static com.game.messages.MessageType.*;
import static com.game.utils.enums.Position.BOTTOM_CENTER;
import static java.util.Collections.unmodifiableSet;

public abstract class AbstractEnemy extends Entity implements Damager, Damageable {

    protected final Timer damageTimer = new Timer();
    protected final Supplier<Megaman> megamanSupplier;
    protected final Map<Class<? extends Damager>, DamageNegotiation> damageNegotiations;

    public AbstractEnemy(GameContext2d gameContext, Supplier<Megaman> megamanSupplier, float damageDuration) {
        this(gameContext, megamanSupplier, damageDuration, 1.5f);
    }

    public AbstractEnemy(GameContext2d gameContext, Supplier<Megaman> megamanSupplier,
                         float damageDuration, float cullDuration) {
        super(gameContext);
        this.megamanSupplier = megamanSupplier;
        this.damageNegotiations = defineDamageNegotiations();
        addComponent(graphComponent());
        addComponent(new SoundComponent());
        addComponent(cullOnMessageComponent());
        addComponent(new CullOutOfCamBoundsComponent(() ->
                getComponent(BodyComponent.class).getCollisionBox(), cullDuration));
        addComponent(new HealthComponent(MAX_HEALTH, this::disintegrate));
        damageTimer.setDuration(damageDuration);
        damageTimer.setToEnd();
    }

    protected abstract Map<Class<? extends Damager>, DamageNegotiation> defineDamageNegotiations();

    protected GraphComponent graphComponent() {
        return new GraphComponent(() -> getComponent(BodyComponent.class).getCollisionBox(), () -> List.of(this));
    }

    protected CullOnMessageComponent cullOnMessageComponent() {
        CullOnMessageComponent cullOnMessageComponent = new CullOnMessageComponent();
        cullOnMessageComponent.addCullMessagePredicate(PLAYER_SPAWN, BEGIN_GAME_ROOM_TRANS, GATE_INIT_OPENING);
        return cullOnMessageComponent;
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
        getComponent(HealthComponent.class).sub(damageNegotiation.getDamage(damager));
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

    protected class StandardEnemySpriteProcessor implements SpriteProcessor {

        private final Timer blinkTimer = new Timer(.05f);

        @Override
        public void update(Sprite sprite1, float delta) {
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
