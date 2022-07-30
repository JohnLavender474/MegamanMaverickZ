package com.game.entities.projectiles;

import com.badlogic.gdx.math.Vector2;
import com.game.GameContext2d;
import com.game.core.IEntity;
import com.game.entities.enemies.AbstractEnemy;
import com.game.updatables.UpdatableComponent;
import com.game.utils.objects.Timer;
import com.game.world.BodyComponent;
import com.game.world.BodyType;
import com.game.world.Fixture;
import com.game.world.FixtureType;

import static com.game.ConstVals.ViewVals.PPM;

public class Fireball extends AbstractProjectile {

    private final Timer burnTimer = new Timer(1.5f);

    private boolean isLanded;

    public Fireball(GameContext2d gameContext, IEntity owner, Vector2 impulse, Vector2 spawn) {
        super(gameContext, owner, .25f);
        addComponent(defineUpdatableComponent());
        addComponent(defineBodyComponent(spawn, impulse));
    }

    @Override
    public void hit(Fixture fixture) {
        if (fixture.getEntity().equals(owner) || (owner instanceof AbstractEnemy &&
                fixture.getEntity() instanceof AbstractEnemy)) {
            return;
        }
        if (fixture.getFixtureType().equals(FixtureType.BLOCK)) {
            isLanded = true;
        }
    }

    private UpdatableComponent defineUpdatableComponent() {
        return new UpdatableComponent(delta -> {
           if (isLanded) {
               burnTimer.update(delta);
           }
           setDead(burnTimer.isFinished());
        });
    }

    private BodyComponent defineBodyComponent(Vector2 spawn, Vector2 impulse) {
        BodyComponent bodyComponent = new BodyComponent(BodyType.DYNAMIC);
        bodyComponent.setPreProcess(delta -> {
            if (isLanded) {
                bodyComponent.setGravity(0f);
            }
        });
        bodyComponent.applyImpulse(impulse);
        bodyComponent.setGravity(-10f * PPM);
        bodyComponent.setSize(.1f * PPM, .1f * PPM);
        bodyComponent.setCenter(spawn.x, spawn.y);
        Fixture projectile = new Fixture(this, FixtureType.HITTER_BOX);
        projectile.setSize(.1f * PPM, .1f * PPM);
        projectile.setCenter(spawn.x, spawn.y);
        bodyComponent.addFixture(projectile);
        Fixture damageBox = new Fixture(this, FixtureType.DAMAGER_BOX);
        damageBox.setSize(.1f * PPM, .1f * PPM);
        damageBox.setCenter(spawn.x, spawn.y);
        bodyComponent.addFixture(damageBox);
        return bodyComponent;
    }

}
