package com.game.entities.projectiles;

import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.game.Entity;
import com.game.GameContext2d;
import com.game.core.IEntity;
import com.game.cull.CullOnCamTransComponent;
import com.game.cull.CullOutOfCamBoundsComponent;
import com.game.entities.contracts.Damageable;
import com.game.entities.contracts.Damager;
import com.game.entities.contracts.Hitter;
import com.game.entities.decorations.Disintegration;
import com.game.entities.enemies.AbstractEnemy;
import com.game.levels.CullOnLevelCamTrans;
import com.game.levels.CullOnOutOfCamBounds;
import com.game.sprites.SpriteAdapter;
import com.game.sprites.SpriteComponent;
import com.game.utils.enums.Position;
import com.game.utils.objects.Timer;
import com.game.utils.objects.Wrapper;
import com.game.world.BodyComponent;
import com.game.world.BodyType;
import com.game.world.Fixture;
import com.game.world.FixtureType;
import lombok.Getter;
import lombok.Setter;

import static com.game.ConstVals.SoundAssets.*;
import static com.game.ConstVals.TextureAssets.*;
import static com.game.ConstVals.ViewVals.PPM;
import static com.game.world.FixtureType.BLOCK;
import static com.game.world.FixtureType.SHIELD;

@Getter
@Setter
public class Bullet extends Entity implements Hitter, Damager, CullOnOutOfCamBounds, CullOnLevelCamTrans {

    private final GameContext2d gameContext;
    private final Timer cullTimer = new Timer(.15f);
    private final Vector2 trajectory = new Vector2();

    private IEntity owner;
    private int damage;

    public Bullet(GameContext2d gameContext, IEntity owner, Vector2 spawn, Vector2 trajectory) {
        this.owner = owner;
        this.gameContext = gameContext;
        this.trajectory.set(trajectory);
        addComponent(new CullOutOfCamBoundsComponent(() -> getComponent(BodyComponent.class).getCollisionBox(), .15f));
        addComponent(new CullOnCamTransComponent());
        addComponent(defineBodyComponent(spawn));
        addComponent(defineSpriteComponent());
    }

    @Override
    public Rectangle getCullBoundingBox() {
        return getComponent(BodyComponent.class).getCollisionBox();
    }

    @Override
    public boolean canDamage(Damageable damageable) {
        return !owner.equals(damageable) && !(owner instanceof AbstractEnemy && damageable instanceof AbstractEnemy);
    }

    public void disintegrate() {
        Disintegration disintegration = new Disintegration(gameContext, getComponent(BodyComponent.class).getCenter());
        gameContext.addEntity(disintegration);
        gameContext.getAsset(THUMP_SOUND, Sound.class).play();
    }

    @Override
    public void hit(Fixture fixture) {
        if (fixture.getEntity().equals(owner) || (owner instanceof AbstractEnemy &&
                fixture.getEntity() instanceof AbstractEnemy)) {
            return;
        }
        if (fixture.getFixtureType() == BLOCK) {
            setDead(true);
            disintegrate();
        } else if (fixture.getFixtureType() == SHIELD) {
            setOwner(fixture.getEntity());
            trajectory.x *= -1f;
            String reflectDir = fixture.getUserData("reflectDir", String.class);
            if (reflectDir == null || reflectDir.equals("straight")) {
                trajectory.y = 0f;
            } else {
                trajectory.y = 5f * PPM * (reflectDir.equals("down") ? -1f : 1f);
            }
            gameContext.getAsset(DINK_SOUND, Sound.class).play();
        }
    }

    private SpriteComponent defineSpriteComponent() {
        TextureRegion textureRegion = gameContext.getAsset(OBJECTS_TEXTURE_ATLAS, TextureAtlas.class)
                .findRegion("YellowBullet");
        Sprite sprite = new Sprite();
        sprite.setRegion(textureRegion);
        sprite.setSize(PPM * 1.25f, PPM * 1.25f);
        return new SpriteComponent(sprite, new SpriteAdapter() {
            @Override
            public boolean setPositioning(Wrapper<Rectangle> bounds, Wrapper<Position> position) {
                bounds.setData(getCullBoundingBox());
                position.setData(Position.CENTER);
                return true;
            }
        });
    }

    private BodyComponent defineBodyComponent(Vector2 spawn) {
        BodyComponent bodyComponent = new BodyComponent(BodyType.DYNAMIC);
        bodyComponent.setPreProcess(delta -> bodyComponent.setVelocity(trajectory));
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
