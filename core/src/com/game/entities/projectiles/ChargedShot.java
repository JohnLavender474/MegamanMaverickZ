package com.game.entities.projectiles;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.game.core.Entity;
import com.game.core.GameContext2d;
import com.game.animations.AnimationComponent;
import com.game.animations.TimedAnimation;
import com.game.damage.Damageable;
import com.game.damage.Damager;
import com.game.entities.contracts.Faceable;
import com.game.entities.contracts.Facing;
import com.game.sounds.SoundComponent;
import com.game.sprites.SpriteProcessor;
import com.game.sprites.SpriteComponent;
import com.game.utils.enums.Position;
import com.game.utils.objects.Wrapper;
import com.game.world.BodyComponent;
import com.game.world.Fixture;
import lombok.Getter;
import lombok.Setter;

import static com.game.constants.SoundAsset.*;
import static com.game.constants.TextureAsset.MEGAMAN_CHARGED_SHOT;
import static com.game.constants.TextureAsset.MEGAMAN_HALF_CHARGED_SHOT;
import static com.game.constants.ViewVals.PPM;
import static com.game.entities.contracts.Facing.F_LEFT;
import static com.game.world.BodyType.*;
import static com.game.world.FixtureType.*;

@Getter
@Setter
public class ChargedShot extends AbstractProjectile implements Faceable {

    private final Vector2 trajectory = new Vector2();
    private final boolean fullyCharged;

    private Facing facing;

    public ChargedShot(GameContext2d gameContext, Entity owner, Vector2 trajectory, Vector2 spawn,
                       Facing facing, boolean fullyCharged) {
        super(gameContext, owner, .15f);
        this.fullyCharged = fullyCharged;
        this.trajectory.set(trajectory);
        setFacing(facing);
        addComponent(animationComponent());
        addComponent(bodyComponent(spawn));
        addComponent(spriteComponent());
    }

    @Override
    public boolean canDamage(Damageable damageable) {
        return owner == null ||
                (!owner.equals(damageable) && !(owner instanceof Damager && damageable instanceof Damager));
    }

    @Override
    public void onDamageInflictedTo(Damageable damageable) {
        setDead(true);
        gameContext.addEntity(new ChargedShotDisintegration(
                gameContext, getComponent(BodyComponent.class).getCenter(), isFacing(F_LEFT), fullyCharged));
    }

    @Override
    public void hit(Fixture fixture) {
        if (fixture.getEntity().equals(owner) ||
                (owner instanceof Damager && fixture.getEntity() instanceof Damager)) {
            return;
        }
        if (fixture.isAnyFixtureType(BLOCK, DAMAGEABLE)) {
            setDead(true);
            gameContext.addEntity(new ChargedShotDisintegration(
                    gameContext, getComponent(BodyComponent.class).getCenter(), isFacing(F_LEFT), fullyCharged));
        } else if (fixture.isFixtureType(SHIELD)) {
            setOwner(fixture.getEntity());
            swapFacing();
            trajectory.x *= -1f;
            String reflectDir = fixture.getUserData("reflectDir", String.class);
            if (reflectDir == null || reflectDir.equals("straight")) {
                trajectory.y = 0f;
            } else {
                trajectory.y = 5f * PPM * (reflectDir.equals("down") ? -1f : 1f);
            }
            getComponent(SoundComponent.class).requestSound(DINK_SOUND);
        }
    }

    private AnimationComponent animationComponent() {
        TextureRegion textureRegion;
        if (fullyCharged) {
            textureRegion = gameContext.getAsset(MEGAMAN_CHARGED_SHOT.getSrc(), TextureAtlas.class)
                    .findRegion("MegamanChargedShot");
        } else {
            textureRegion = gameContext.getAsset(MEGAMAN_HALF_CHARGED_SHOT.getSrc(), TextureAtlas.class)
                    .findRegion("Shoot");
        }
        return new AnimationComponent(new TimedAnimation(textureRegion, 2, .05f));
    }

    private SpriteComponent spriteComponent() {
        Sprite sprite = new Sprite();
        if (fullyCharged) {
            sprite.setSize(PPM * 1.75f, PPM * 1.75f);
        } else {
            sprite.setSize(PPM * 1.25f, PPM * 1.25f);
        }
        return new SpriteComponent(sprite, new SpriteProcessor() {

            @Override
            public boolean setPositioning(Wrapper<Rectangle> bounds, Wrapper<Position> position) {
                bounds.setData(getComponent(BodyComponent.class).getCollisionBox());
                position.setData(Position.CENTER);
                return true;
            }

            @Override
            public boolean isFlipX() {
                return isFacing(F_LEFT);
            }

        });
    }

    private BodyComponent bodyComponent(Vector2 spawn) {
        BodyComponent bodyComponent = new BodyComponent(DYNAMIC);
        bodyComponent.setPreProcess(delta -> bodyComponent.setVelocity(trajectory));
        if (fullyCharged) {
            bodyComponent.setSize(PPM, PPM);
        } else {
            bodyComponent.setSize(.5f * PPM, .5f * PPM);
        }
        bodyComponent.setCenter(spawn.x, spawn.y);
        // model
        Rectangle model = new Rectangle(0f, 0f, fullyCharged ? PPM : .5f * PPM, fullyCharged ? PPM : .5f * PPM);
        Fixture projectile = new Fixture(this, new Rectangle(model), HITTER);
        bodyComponent.addFixture(projectile);
        Fixture damageBox = new Fixture(this, new Rectangle(model), DAMAGER);
        bodyComponent.addFixture(damageBox);
        return bodyComponent;
    }

}
