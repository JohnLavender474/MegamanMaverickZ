package com.game.tests.entities;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.game.Component;
import com.game.ConstVals;
import com.game.core.IAssetLoader;
import com.game.core.IEntitiesAndSystemsManager;
import com.game.core.IEntity;
import com.game.entities.contracts.Damageable;
import com.game.entities.contracts.Damager;
import com.game.entities.projectiles.IProjectile;
import com.game.sound.SoundComponent;
import com.game.sound.SoundRequest;
import com.game.sprites.SpriteComponent;
import com.game.utils.Percentage;
import com.game.utils.Position;
import com.game.utils.Timer;
import com.game.world.BodyComponent;
import com.game.world.BodyType;
import com.game.world.Fixture;
import com.game.levels.CullOnLevelCamTrans;
import com.game.levels.CullOnOutOfCamBounds;
import lombok.Getter;
import lombok.Setter;

import java.util.HashMap;
import java.util.Map;

import static com.game.ConstVals.SoundAssets.THUMP_SOUND;
import static com.game.ConstVals.ViewVals.PPM;
import static com.game.world.FixtureType.*;

@Getter
@Setter
public class TestBullet implements IEntity, IProjectile, Damager, CullOnOutOfCamBounds, CullOnLevelCamTrans {

    private final Map<Class<? extends Component>, Component> components = new HashMap<>();
    private final IEntitiesAndSystemsManager entitiesAndSystemsManager;
    private final Vector2 trajectory = new Vector2();
    private final Timer cullTimer = new Timer(5f);
    private final IAssetLoader assetLoader;

    private int damage;
    private boolean dead;
    private IEntity owner;

    public TestBullet(IEntity owner, Vector2 trajectory, Vector2 spawn, TextureRegion textureRegion,
                      IAssetLoader assetLoader, IEntitiesAndSystemsManager entitiesAndSystemsManager) {
        this.owner = owner;
        this.trajectory.set(trajectory);
        this.assetLoader = assetLoader;
        this.entitiesAndSystemsManager = entitiesAndSystemsManager;
        addComponent(defineSpriteComponent(textureRegion));
        addComponent(defineBodyComponent(spawn));
        addComponent(new SoundComponent());
    }

    @Override
    public Rectangle getCullBoundingBox() {
        return getComponent(BodyComponent.class).getCollisionBox();
    }

    public void disintegrate() {
        SoundComponent soundComponent = getComponent(SoundComponent.class);
        soundComponent.request(new SoundRequest(THUMP_SOUND, false, Percentage.of(ConstVals.VolumeVals.HIGH_VOLUME)));
        TestDisintegration disintegration = new TestDisintegration(
                assetLoader, getComponent(BodyComponent.class).getCenter());
        entitiesAndSystemsManager.addEntity(disintegration);
        Gdx.audio.newSound(Gdx.files.internal("sounds/Thump.mp3")).play(.5f);
    }

    private SpriteComponent defineSpriteComponent(TextureRegion textureRegion) {
        Sprite sprite = new Sprite();
        sprite.setRegion(textureRegion);
        sprite.setSize(PPM * 1.25f, PPM * 1.25f);
        return new SpriteComponent(sprite, (bounds, position) -> {
            bounds.setData(getCullBoundingBox());
            position.setData(Position.CENTER);
            return true;
        });
    }

    private BodyComponent defineBodyComponent(Vector2 spawn) {
        BodyComponent bodyComponent = new BodyComponent(BodyType.DYNAMIC);
        bodyComponent.setPreProcess(delta -> bodyComponent.setVelocity(trajectory));
        bodyComponent.setSize(.1f * PPM, .1f * PPM);
        bodyComponent.setCenter(spawn.x, spawn.y);
        Fixture projectile = new Fixture(this, PROJECTILE);
        projectile.setSize(.115f * PPM, .115f * PPM);
        projectile.setCenter(spawn.x, spawn.y);
        bodyComponent.addFixture(projectile);
        Fixture damageBox = new Fixture(this, DAMAGE_BOX);
        damageBox.setSize(.1f * PPM, .1f * PPM);
        damageBox.setCenter(spawn.x, spawn.y);
        bodyComponent.addFixture(damageBox);
        return bodyComponent;
    }

    @Override
    public void hit(Fixture fixture) {
        if (fixture.getEntity().equals(owner)) {
            return;
        }
        if (fixture.getFixtureType() == BLOCK || (fixture.getFixtureType() == HIT_BOX)) {
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
            Gdx.audio.newSound(Gdx.files.internal("sounds/Dink.mp3")).play();
        }
    }

    @Override
    public boolean canDamage(Damageable damageable) {
        return !owner.equals(damageable);
    }

}
