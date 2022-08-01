package com.game.entities.projectiles;

import com.game.Entity;
import com.game.GameContext2d;
import com.game.core.IEntity;
import com.game.cull.CullOnCamTransComponent;
import com.game.cull.CullOutOfCamBoundsComponent;
import com.game.damage.Damageable;
import com.game.damage.Damager;
import com.game.entities.contracts.Hitter;
import com.game.entities.enemies.AbstractEnemy;
import com.game.sounds.SoundComponent;
import com.game.world.BodyComponent;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public abstract class AbstractProjectile extends Entity implements Hitter, Damager {

    protected final GameContext2d gameContext;

    protected IEntity owner;

    public AbstractProjectile(GameContext2d gameContext, IEntity owner, float cullDuration) {
        this.owner = owner;
        this.gameContext = gameContext;
        addComponent(new SoundComponent());
        addComponent(new CullOnCamTransComponent());
        addComponent(defineCullOutOfCamBoundsComponent(cullDuration));
    }

    private CullOutOfCamBoundsComponent defineCullOutOfCamBoundsComponent(float cullDuration) {
        return new CullOutOfCamBoundsComponent(() -> getComponent(BodyComponent.class).getCollisionBox(), cullDuration);
    }

    @Override
    public boolean canDamage(Damageable damageable) {
        return !owner.equals(damageable) && !(owner instanceof AbstractEnemy && damageable instanceof AbstractEnemy);
    }

}
