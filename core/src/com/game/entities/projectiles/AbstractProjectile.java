package com.game.entities.projectiles;

import com.game.entities.Entity;
import com.game.GameContext2d;
import com.game.cull.CullOnMessageComponent;
import com.game.cull.CullOutOfCamBoundsComponent;
import com.game.damage.Damageable;
import com.game.damage.Damager;
import com.game.entities.contracts.Hitter;
import com.game.entities.enemies.AbstractEnemy;
import com.game.sounds.SoundComponent;
import com.game.world.BodyComponent;
import lombok.Getter;
import lombok.Setter;

import static com.game.messages.MessageType.*;
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
        addComponent(cullOnMessageComponent());
        addComponent(cullOutOfCamBoundsComponent(cullDuration));
    }

    @Override
    public boolean canDamage(Damageable damageable) {
        return owner == null ||
                (!owner.equals(damageable) && !(owner instanceof AbstractEnemy && damageable instanceof AbstractEnemy));
    }

    public boolean isInGameCamBounds() {
        return isInCamBounds(gameContext.getViewport(PLAYGROUND).getCamera(),
                getComponent(BodyComponent.class).getCollisionBox());
    }

    protected CullOnMessageComponent cullOnMessageComponent() {
        CullOnMessageComponent cullOnMessageComponent = new CullOnMessageComponent();
        cullOnMessageComponent.addCullMessagePredicate(PLAYER_SPAWN, BEGIN_GAME_ROOM_TRANS, GATE_INIT_OPENING);
        return cullOnMessageComponent;
    }

    private CullOutOfCamBoundsComponent cullOutOfCamBoundsComponent(float cullDuration) {
        return new CullOutOfCamBoundsComponent(() -> getComponent(BodyComponent.class).getCollisionBox(), cullDuration);
    }

}
