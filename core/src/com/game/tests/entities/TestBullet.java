package com.game.tests.entities;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.game.Entity;
import com.game.core.IAssetLoader;
import com.game.core.IEntitiesAndSystemsManager;
import com.game.core.IEntity;
import com.game.cull.CullOnCamTransComponent;
import com.game.cull.CullOnOutOfCamBoundsComponent;
import com.game.debugging.DebugComponent;
import com.game.entities.contracts.Damageable;
import com.game.entities.contracts.Damager;
import com.game.entities.contracts.Hitter;
import com.game.sprites.SpriteComponent;
import com.game.utils.Position;
import com.game.world.BodyComponent;
import com.game.world.BodyType;
import com.game.world.Fixture;
import lombok.Getter;
import lombok.Setter;

import static com.game.ConstVals.ViewVals.PPM;
import static com.game.world.FixtureType.*;

@Getter
@Setter
public class TestBullet extends Entity implements Hitter, Damager {

    private final IEntitiesAndSystemsManager entitiesAndSystemsManager;
    private final Vector2 trajectory = new Vector2();
    private final IAssetLoader assetLoader;

    private IEntity owner;

    public TestBullet(IEntity owner, Vector2 trajectory, Vector2 spawn, TextureRegion textureRegion,
                      IAssetLoader assetLoader, IEntitiesAndSystemsManager entitiesAndSystemsManager) {
        this.owner = owner;
        this.trajectory.set(trajectory);
        this.assetLoader = assetLoader;
        this.entitiesAndSystemsManager = entitiesAndSystemsManager;
        addComponent(new CullOnOutOfCamBoundsComponent(() ->
                getComponent(BodyComponent.class).getCollisionBox(), .15f));
        addComponent(new CullOnCamTransComponent());
        addComponent(defineSpriteComponent(textureRegion));
        addComponent(defineBodyComponent(spawn));
        addComponent(defineDebugComponent());
    }

    public void disintegrate() {
        TestDisintegration disintegration = new TestDisintegration(
                assetLoader, getComponent(BodyComponent.class).getCenter());
        entitiesAndSystemsManager.addEntity(disintegration);
        Gdx.audio.newSound(Gdx.files.internal("sounds/Thump.mp3")).play(.5f);
    }

    @Override
    public boolean canDamage(Damageable damageable) {
        return !owner.equals(damageable);
    }

    @Override
    public void onDamageInflictedTo(Class<? extends Damageable> damageableClass) {
        disintegrate();
        setDead(true);
    }

    private SpriteComponent defineSpriteComponent(TextureRegion textureRegion) {
        Sprite sprite = new Sprite();
        sprite.setRegion(textureRegion);
        sprite.setSize(PPM * 1.25f, PPM * 1.25f);
        return new SpriteComponent(sprite, (bounds, position) -> {
            bounds.setData(getComponent(BodyComponent.class).getCollisionBox());
            position.setData(Position.CENTER);
            return true;
        });
    }

    private BodyComponent defineBodyComponent(Vector2 spawn) {
        BodyComponent bodyComponent = new BodyComponent(BodyType.DYNAMIC);
        bodyComponent.setPreProcess(delta -> bodyComponent.setVelocity(trajectory));
        bodyComponent.setSize(.1f * PPM, .1f * PPM);
        bodyComponent.setCenter(spawn.x, spawn.y);
        Fixture projectile = new Fixture(this, HITTER_BOX);
        projectile.setSize(.1f * PPM, .1f * PPM);
        bodyComponent.addFixture(projectile);
        Fixture damageBox = new Fixture(this, DAMAGER_BOX);
        damageBox.setSize(.1f * PPM, .1f * PPM);
        bodyComponent.addFixture(damageBox);
        return bodyComponent;
    }

    private DebugComponent defineDebugComponent() {
        DebugComponent debugComponent = new DebugComponent();
        getComponent(BodyComponent.class).getFixtures().forEach(fixture ->
                debugComponent.addDebugHandle(fixture::getFixtureBox, () -> switch (fixture.getFixtureType()) {
                    case HITTER_BOX -> Color.BLUE;
                    case DAMAGER_BOX -> Color.RED;
                    default -> Color.GREEN;
                }));
        return debugComponent;
    }

    @Override
    public void hit(Fixture fixture) {
        if (fixture.getEntity().equals(owner)) {
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
            Gdx.audio.newSound(Gdx.files.internal("sounds/Dink.mp3")).play();
        }
    }

}
