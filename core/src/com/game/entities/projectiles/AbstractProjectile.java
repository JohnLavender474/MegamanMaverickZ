package com.game.entities.projectiles;

import com.game.Entity;
import com.game.GameContext2d;
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

import static com.game.sprites.RenderingGround.*;
import static com.game.utils.UtilMethods.*;

@Getter
@Setter
public abstract class AbstractProjectile extends Entity implements Hitter, Damager {

    protected Entity owner;

    public AbstractProjectile(GameContext2d gameContext, Entity owner, float cullDuration) {
        super(gameContext);
        this.owner = owner;
        addComponent(new SoundComponent());
        addComponent(new CullOnCamTransComponent());
        addComponent(cullOutOfCamBoundsComponent(cullDuration));
        gameContext.addEventListener(this);
    }

    @Override
    public void onDeath() {
        gameContext.removeEventListener(this);
    }

    public boolean isInGameCamBounds() {
        return isInCamBounds(gameContext.getViewport(PLAYGROUND).getCamera(),
                getComponent(BodyComponent.class).getCollisionBox());
    }

    private CullOutOfCamBoundsComponent cullOutOfCamBoundsComponent(float cullDuration) {
        return new CullOutOfCamBoundsComponent(() -> getComponent(BodyComponent.class).getCollisionBox(), cullDuration);
    }

    @Override
    public boolean canDamage(Damageable damageable) {
        return owner == null ||
                (!owner.equals(damageable) && !(owner instanceof AbstractEnemy && damageable instanceof AbstractEnemy));
    }

}
