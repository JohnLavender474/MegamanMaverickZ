package com.game.entities.projectiles;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.game.GameContext2d;
import com.game.animations.AnimationComponent;
import com.game.animations.TimedAnimation;
import com.game.core.IEntity;
import com.game.cull.CullOnCamTransComponent;
import com.game.cull.CullOutOfCamBoundsComponent;
import com.game.damage.Damageable;
import com.game.damage.Damager;
import com.game.entities.contracts.Faceable;
import com.game.entities.contracts.Facing;
import com.game.sounds.SoundComponent;
import com.game.sprites.SpriteAdapter;
import com.game.sprites.SpriteComponent;
import com.game.utils.enums.Position;
import com.game.utils.objects.Wrapper;
import com.game.world.BodyComponent;
import com.game.world.Fixture;
import lombok.Getter;
import lombok.Setter;

import static com.game.ConstVals.SoundAssets.*;
import static com.game.ConstVals.TextureAssets.MEGAMAN_CHARGED_SHOT_TEXTURE_ATLAS;
import static com.game.ConstVals.ViewVals.PPM;
import static com.game.entities.contracts.Facing.F_LEFT;
import static com.game.world.BodyType.*;
import static com.game.world.FixtureType.*;

@Getter
@Setter
public class ChargedShot extends AbstractProjectile implements Faceable {

    private final Vector2 trajectory = new Vector2();

    private Facing facing;

    public ChargedShot(GameContext2d gameContext, IEntity owner, float cullDuration,
                       Vector2 spawn, Vector2 trajectory, Facing facing) {
        super(gameContext, owner, cullDuration);
        this.trajectory.set(trajectory);
        setFacing(facing);
        addComponent(new CullOutOfCamBoundsComponent(() -> getComponent(BodyComponent.class).getCollisionBox(), .15f));
        addComponent(new CullOnCamTransComponent());
        addComponent(defineAnimationComponent());
        addComponent(defineBodyComponent(spawn));
        addComponent(defineSpriteComponent());
        addComponent(new SoundComponent());
    }

    @Override
    public boolean canDamage(Damageable damageable) {
        return !owner.equals(damageable) && !(owner instanceof Damager && damageable instanceof Damager);
    }

    @Override
    public void onDamageInflictedTo(Class<? extends Damageable> damageableClass) {
        setDead(true);
        gameContext.addEntity(new ChargedShotDisintegration(
                gameContext, getComponent(BodyComponent.class).getCenter(), isFacing(F_LEFT)));
    }

    @Override
    public void hit(Fixture fixture) {
        if (fixture.getEntity().equals(owner) ||
                (owner instanceof Damager && fixture.getEntity() instanceof Damager)) {
            return;
        }
        if (fixture.getFixtureType() == BLOCK) {
            setDead(true);
            gameContext.addEntity(new ChargedShotDisintegration(
                    gameContext, getComponent(BodyComponent.class).getCenter(), isFacing(F_LEFT)));
        } else if (fixture.getFixtureType() == SHIELD) {
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

    private AnimationComponent defineAnimationComponent() {
        TextureAtlas textureAtlas = gameContext.getAsset(MEGAMAN_CHARGED_SHOT_TEXTURE_ATLAS, TextureAtlas.class);
        return new AnimationComponent(new TimedAnimation(textureAtlas.findRegion("MegamanChargedShot"), 2, .05f));
    }

    private SpriteComponent defineSpriteComponent() {
        Sprite sprite = new Sprite();
        sprite.setSize(PPM * 1.75f, PPM * 1.75f);
        return new SpriteComponent(sprite, new SpriteAdapter() {

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

    private BodyComponent defineBodyComponent(Vector2 spawn) {
        BodyComponent bodyComponent = new BodyComponent(DYNAMIC);
        bodyComponent.setPreProcess(delta -> bodyComponent.setVelocity(trajectory));
        bodyComponent.setSize(PPM, PPM);
        bodyComponent.setCenter(spawn.x, spawn.y);
        Fixture projectile = new Fixture(this, HITTER_BOX);
        projectile.setSize(PPM, PPM);
        bodyComponent.addFixture(projectile);
        Fixture damageBox = new Fixture(this, DAMAGER_BOX);
        damageBox.setSize(PPM, PPM);
        bodyComponent.addFixture(damageBox);
        return bodyComponent;
    }

}
