package com.game.entities.projectiles;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.game.entities.Entity;
import com.game.GameContext2d;
import com.game.entities.decorations.Disintegration;
import com.game.entities.enemies.AbstractEnemy;
import com.game.entities.enemies.Matasaburo;
import com.game.entities.megaman.Megaman;
import com.game.sounds.SoundComponent;
import com.game.sprites.SpriteProcessor;
import com.game.sprites.SpriteComponent;
import com.game.utils.enums.Position;
import com.game.utils.objects.Wrapper;
import com.game.world.BodyComponent;
import com.game.world.Fixture;
import lombok.Getter;
import lombok.Setter;

import java.util.HashSet;
import java.util.function.Supplier;

import static com.game.GlobalKeys.*;
import static com.game.assets.SoundAsset.*;
import static com.game.assets.TextureAsset.OBJECTS;
import static com.game.ViewVals.PPM;
import static com.game.utils.enums.Position.*;
import static com.game.world.BodyType.*;
import static com.game.world.FixtureType.*;
import static com.game.world.FixtureType.BLOCK;
import static com.game.world.FixtureType.SHIELD;

@Getter
@Setter
public class Bullet extends AbstractProjectile {

    private static final float CLAMP = 10f;

    public Bullet(GameContext2d gameContext, Entity owner, Vector2 trajectory, Vector2 spawn) {
        super(gameContext, owner, .15f);
        addComponent(new SoundComponent());
        addComponent(spriteComponent());
        addComponent(bodyComponent(spawn, trajectory));
    }

    public void disintegrate() {
        gameContext.addEntity(new Disintegration(gameContext, getComponent(BodyComponent.class).getCenter()));
        if (isInGameCamBounds()) {
            getComponent(SoundComponent.class).requestSound(THUMP_SOUND);
        }
        setDead(true);
    }

    @Override
    public void hit(Fixture fixture) {
        if (fixture.getEntity().equals(owner)) {
            return;
        }
        if (fixture.isAnyFixtureType(BLOCK, DAMAGEABLE)) {
            disintegrate();
        } else if (fixture.isFixtureType(SHIELD)) {
            setOwner(fixture.getEntity());
            Vector2 velocity = getComponent(BodyComponent.class).getVelocity();
            velocity.x *= -1f;
            String reflectDir = fixture.getUserData("reflectDir", String.class);
            if (reflectDir == null || reflectDir.equals("straight")) {
                velocity.y = 0f;
            } else {
                velocity.y = 5f * PPM * (reflectDir.equals("down") ? -1f : 1f);
            }
            getComponent(SoundComponent.class).requestSound(DINK_SOUND);
        }
    }

    private SpriteComponent spriteComponent() {
        TextureRegion textureRegion = gameContext.getAsset(OBJECTS.getSrc(), TextureAtlas.class)
                .findRegion("YellowBullet");
        Sprite sprite = new Sprite();
        sprite.setRegion(textureRegion);
        sprite.setSize(PPM * 1.25f, PPM * 1.25f);
        return new SpriteComponent(sprite, new SpriteProcessor() {

            @Override
            public boolean setPositioning(Wrapper<Rectangle> bounds, Wrapper<Position> position) {
                bounds.setData(getComponent(BodyComponent.class).getCollisionBox());
                position.setData(CENTER);
                return true;
            }

            @Override
            public int getSpriteRenderPriority() {
                return 3;
            }

        });
    }

    private BodyComponent bodyComponent(Vector2 spawn, Vector2 trajectory) {
        BodyComponent bodyComponent = new BodyComponent(DYNAMIC);
        bodyComponent.setClamp(CLAMP * PPM, CLAMP * PPM);
        bodyComponent.setSize(.1f * PPM, .1f * PPM);
        bodyComponent.setCenter(spawn.x, spawn.y);
        bodyComponent.setVelocity(trajectory);
        bodyComponent.setAffectedByResistance(false);
        Rectangle model = new Rectangle(0f, 0f, .1f * PPM, .1f * PPM);
        // projectile
        Fixture projectile = new Fixture(this, new Rectangle(model), HITTER);
        bodyComponent.addFixture(projectile);
        // force listener
        Fixture forceListener = new Fixture(this, new Rectangle(model), FORCE_LISTENER);
        forceListener.putUserData(COLLECTION, new HashSet<>() {{
            add(Matasaburo.class);
        }});
        forceListener.putUserData(CONTINUE, true);
        forceListener.putUserData(UPDATE + PREDICATE, (Supplier<Boolean>) () -> true);
        forceListener.putUserData(REMOVE + PREDICATE, (Supplier<Boolean>) () -> false);
        bodyComponent.addFixture(forceListener);
        // damager box
        Fixture damageBox = new Fixture(this, new Rectangle(0f, 0f, .2f * PPM, .2f * PPM), DAMAGER);
        bodyComponent.addFixture(damageBox);
        return bodyComponent;
    }

}
