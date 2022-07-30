package com.game.entities.projectiles;

import com.badlogic.gdx.math.Vector2;
import com.game.GameContext2d;
import com.game.core.IEntity;
import com.game.entities.contracts.Facing;
import com.game.utils.objects.Timer;
import com.game.world.BodyComponent;
import com.game.world.BodyType;
import com.game.world.Fixture;
import com.game.world.FixtureType;

import static com.game.ConstVals.ViewVals.PPM;

public class Fireball extends AbstractProjectile {

    private final Timer burnTimer = new Timer(1.5f);

    private boolean isLanded;

    public Fireball(GameContext2d gameContext, IEntity owner, Vector2 spawn, boolean isRight) {
        super(gameContext, owner, .25f);

    }

    @Override
    public void hit(Fixture fixture) {

    }

    private BodyComponent defineBodyComponent(Vector2 spawn, boolean isRight) {
        BodyComponent bodyComponent = new BodyComponent(BodyType.DYNAMIC);
        bodyComponent.applyImpulse((isRight ? 15f : -15f) * PPM, 0f);
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
