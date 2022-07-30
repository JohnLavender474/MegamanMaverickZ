package com.game.tests.entities;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.game.ConstVals;
import com.game.Entity;
import com.game.core.IAssetLoader;
import com.game.core.IEntitiesAndSystemsManager;
import com.game.core.IEntity;
import com.game.cull.CullOnCamTransComponent;
import com.game.cull.CullOutOfCamBoundsComponent;
import com.game.debugging.DebugRectComponent;
import com.game.damage.Damageable;
import com.game.damage.Damager;
import com.game.entities.contracts.Hitter;
import com.game.sprites.SpriteAdapter;
import com.game.sprites.SpriteComponent;
import com.game.utils.enums.Position;
import com.game.utils.objects.Wrapper;
import com.game.world.BodyComponent;
import com.game.world.BodyType;
import com.game.world.Fixture;
import lombok.Getter;
import lombok.Setter;

import static com.game.ConstVals.TextureAssets.*;
import static com.game.ConstVals.ViewVals.PPM;
import static com.game.world.FixtureType.*;

@Getter
@Setter
public class TestBullet extends Entity implements Hitter, Damager {

    private final IEntitiesAndSystemsManager entitiesAndSystemsManager;
    private final Vector2 trajectory = new Vector2();
    private final IAssetLoader assetLoader;

    private IEntity owner;

    public TestBullet(IEntity owner, Vector2 trajectory, Vector2 spawn, IAssetLoader assetLoader,
                      IEntitiesAndSystemsManager entitiesAndSystemsManager) {
        this.owner = owner;
        this.trajectory.set(trajectory);
        this.assetLoader = assetLoader;
        this.entitiesAndSystemsManager = entitiesAndSystemsManager;
        addComponent(new CullOutOfCamBoundsComponent(() -> getComponent(BodyComponent.class).getCollisionBox(), .15f));
        addComponent(new CullOnCamTransComponent());
        addComponent(defineSpriteComponent(assetLoader));
        addComponent(defineBodyComponent(spawn));
        addComponent(defineDebugRectComponent());
    }

    public void disintegrate() {
        TestDisintegration disintegration = new TestDisintegration(
                assetLoader, getComponent(BodyComponent.class).getCenter());
        entitiesAndSystemsManager.addEntity(disintegration);
        Gdx.audio.newSound(Gdx.files.internal("sounds/Thump.mp3")).play(.5f);
    }

    @Override
    public boolean canDamage(Damageable damageable) {
        return !owner.equals(damageable) && !(owner instanceof Damager && damageable instanceof Damager);
    }

    @Override
    public void onDamageInflictedTo(Class<? extends Damageable> damageableClass) {
        setDead(true);
        disintegrate();
    }

    @Override
    public void hit(Fixture fixture) {
        if (fixture.getEntity().equals(owner) ||
                (owner instanceof Damager && fixture.getEntity() instanceof Damager)) {
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


    private SpriteComponent defineSpriteComponent(IAssetLoader assetLoader) {
        Sprite sprite = new Sprite();
        TextureRegion textureRegion = assetLoader.getAsset(OBJECTS_TEXTURE_ATLAS, TextureAtlas.class)
                        .findRegion("YellowBullet");
        sprite.setRegion(textureRegion);
        sprite.setSize(PPM * 1.25f, PPM * 1.25f);
        return new SpriteComponent(sprite, new SpriteAdapter() {
            @Override
            public boolean setPositioning(Wrapper<Rectangle> bounds, Wrapper<Position> position) {
                bounds.setData(getComponent(BodyComponent.class).getCollisionBox());
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
        Fixture projectile = new Fixture(this, HITTER_BOX);
        projectile.setSize(.1f * PPM, .1f * PPM);
        bodyComponent.addFixture(projectile);
        Fixture damageBox = new Fixture(this, DAMAGER_BOX);
        damageBox.setSize(.1f * PPM, .1f * PPM);
        bodyComponent.addFixture(damageBox);
        return bodyComponent;
    }

    private DebugRectComponent defineDebugRectComponent() {
        DebugRectComponent debugRectComponent = new DebugRectComponent();
        getComponent(BodyComponent.class).getFixtures().forEach(fixture ->
                debugRectComponent.addDebugHandle(fixture::getFixtureBox, () -> switch (fixture.getFixtureType()) {
                    case HITTER_BOX -> Color.BLUE;
                    case DAMAGER_BOX -> Color.RED;
                    default -> Color.GREEN;
                }));
        return debugRectComponent;
    }

}
