package com.game.entities.projectiles;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.game.entities.Damageable;
import com.game.entities.Damager;
import com.game.entities.Entity;
import com.game.screens.levels.CullOnOutOfGameCamBounds;
import com.game.sprites.SpriteComponent;
import com.game.utils.Timer;
import com.game.world.BodyComponent;
import com.game.world.BodyType;
import com.game.world.Fixture;
import com.game.world.FixtureType;
import lombok.Getter;
import lombok.Setter;

import static com.game.ConstVals.ViewVals.PPM;

@Getter
@Setter
public class Bullet extends Entity implements Projectile, Damager, CullOnOutOfGameCamBounds {

    private int damage;
    private Entity owner;
    private final Vector2 trajectory = new Vector2();
    private final Timer cullTimer = new Timer(0.15f);

    public Bullet(Entity owner, Vector2 trajectory, Vector2 spawn, TextureRegion textureRegion) {
        this.owner = owner;
        this.trajectory.set(trajectory);
        addComponent(defineSpriteComponent(textureRegion));
        addComponent(defineBodyComponent(spawn));
    }

    @Override
    public boolean canDamage(Damageable damageable) {
        return !owner.equals(damageable);
    }

    @Override
    public void onDamageInflicted(Damageable damageable) {

    }

    @Override
    public void hit(Fixture fixture) {
        switch (fixture.getFixtureType()) {
            case HIT_BOX -> {
                Damageable damageable = (Damageable) fixture.getEntity();
                if (canDamage(damageable) && damageable.canBeDamagedBy(this)) {
                    damageable.takeDamageFrom(this);
                    onDamageInflicted(damageable);
                }
            }
            case BLOCK -> {
                die();
                // if block is instance of breakable, then break the block
            }
            case SHIELD -> {
                trajectory.set(-trajectory.x, PPM / 2f);
                // damage the shield
            }
        }
    }

    @Override
    public Rectangle getBoundingBox() {
        return getComponent(BodyComponent.class).getCollisionBox();
    }

    private SpriteComponent defineSpriteComponent(TextureRegion textureRegion) {
        SpriteComponent spriteComponent = new SpriteComponent();
        Sprite sprite = spriteComponent.getSprite();
        sprite.setRegion(textureRegion);
        sprite.setSize(0.15f * PPM, 0.15f * PPM);
        spriteComponent.setSpriteUpdater(delta -> {
            BodyComponent bodyComponent = getComponent(BodyComponent.class);
            sprite.setCenter(bodyComponent.getCenter().x, bodyComponent.getCenter().y);
        });
        return spriteComponent;
    }

    private BodyComponent defineBodyComponent(Vector2 spawn) {
        BodyComponent bodyComponent = new BodyComponent(BodyType.DYNAMIC);
        bodyComponent.setPreProcess(delta -> bodyComponent.setVelocity(trajectory));
        bodyComponent.setSize(0.1f * PPM, 0.1f * PPM);
        bodyComponent.setCenter(spawn.x, spawn.y);
        Fixture projectile = new Fixture(this, FixtureType.PROJECTILE);
        projectile.setSize(0.1f * PPM, 0.1f * PPM);
        projectile.setCenter(spawn.x, spawn.y);
        bodyComponent.addFixture(projectile);
        return bodyComponent;
    }

}
