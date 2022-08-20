package com.game.entities.projectiles;

import com.game.core.Entity;
import com.game.core.GameContext2d;
import com.game.cull.CullOnCamTransComponent;
import com.game.cull.CullOutOfCamBoundsComponent;
import com.game.damage.Damageable;
import com.game.damage.Damager;
import com.game.entities.contracts.Hitter;
import com.game.entities.enemies.AbstractEnemy;
import com.game.messages.MessageListener;
import com.game.sounds.SoundComponent;
import com.game.world.BodyComponent;
import lombok.Getter;
import lombok.Setter;

import static com.game.core.ConstVals.Events.LEVEL_PAUSED;
import static com.game.core.ConstVals.Events.LEVEL_UNPAUSED;

@Getter
@Setter
public abstract class AbstractProjectile extends Entity implements MessageListener, Hitter, Damager {

    protected final GameContext2d gameContext;

    protected Entity owner;

    public AbstractProjectile(GameContext2d gameContext, Entity owner, float cullDuration) {
        this.owner = owner;
        this.gameContext = gameContext;
        addComponent(new SoundComponent());
        addComponent(new CullOnCamTransComponent());
        addComponent(defineCullOutOfCamBoundsComponent(cullDuration));
        gameContext.addMessageListener(this);
    }

    @Override
    public void onDeath() {
        gameContext.removeMessageListener(this);
    }

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

    private CullOutOfCamBoundsComponent defineCullOutOfCamBoundsComponent(float cullDuration) {
        return new CullOutOfCamBoundsComponent(() -> getComponent(BodyComponent.class).getCollisionBox(), cullDuration);
    }

    @Override
    public boolean canDamage(Damageable damageable) {
        return !owner.equals(damageable) && !(owner instanceof AbstractEnemy && damageable instanceof AbstractEnemy);
    }

}
