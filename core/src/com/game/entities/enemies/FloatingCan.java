package com.game.entities.enemies;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.game.GameContext2d;
import com.game.animations.AnimationComponent;
import com.game.animations.TimedAnimation;
import com.game.cull.CullOnCamTransComponent;
import com.game.cull.CullOutOfCamBoundsComponent;
import com.game.damage.DamageNegotiation;
import com.game.entities.blocks.Block;
import com.game.entities.megaman.Megaman;
import com.game.entities.projectiles.Bullet;
import com.game.entities.projectiles.ChargedShot;
import com.game.entities.projectiles.ChargedShotDisintegration;
import com.game.entities.projectiles.Fireball;
import com.game.graph.GraphComponent;
import com.game.health.HealthComponent;
import com.game.pathfinding.PathfindingComponent;
import com.game.sprites.SpriteComponent;
import com.game.updatables.UpdatableComponent;
import com.game.utils.enums.Position;
import com.game.utils.objects.Wrapper;
import com.game.world.BodyComponent;
import com.game.world.Fixture;

import java.util.List;
import java.util.function.Supplier;

import static com.badlogic.gdx.math.MathUtils.*;
import static com.game.ConstVals.TextureAsset.ENEMIES_TEXTURE_ATLAS;
import static com.game.ConstVals.ViewVals.PPM;
import static com.game.utils.UtilMethods.centerPoint;
import static com.game.utils.UtilMethods.setBottomCenterToPoint;
import static com.game.utils.enums.Position.*;
import static com.game.world.BodyType.*;
import static com.game.world.FixtureType.*;

public class FloatingCan extends AbstractEnemy {

    private static final float SPEED = 1.5f;

    private final Vector2 trajectory = new Vector2();

    public FloatingCan(GameContext2d gameContext, Supplier<Megaman> megamanSupplier, Vector2 spawn) {
        super(gameContext, megamanSupplier, 0.05f);
        defineDamageNegotiations();
        addComponent(new CullOnCamTransComponent());
        addComponent(new UpdatableComponent(new StandardEnemyUpdater()));
        addComponent(new CullOutOfCamBoundsComponent(() -> getComponent(BodyComponent.class).getCollisionBox(), 1.5f));
        addComponent(new HealthComponent(30, this::disintegrate));
        addComponent(definePathfindingComponent());
        addComponent(defineAnimationComponent());
        addComponent(defineBodyComponent(spawn));
        addComponent(defineSpriteComponent());
        addComponent(defineGraphComponent());
    }

    private void defineDamageNegotiations() {
        damageNegotiations.put(Bullet.class, new DamageNegotiation(10));
        damageNegotiations.put(Fireball.class, new DamageNegotiation(30));
        damageNegotiations.put(ChargedShot.class, new DamageNegotiation(30));
        damageNegotiations.put(ChargedShotDisintegration.class, new DamageNegotiation(15));
    }

    private GraphComponent defineGraphComponent() {
        return new GraphComponent(() -> getComponent(BodyComponent.class).getCollisionBox(), () -> List.of(this));
    }

    private BodyComponent defineBodyComponent(Vector2 spawn) {
        BodyComponent bodyComponent = new BodyComponent(ABSTRACT);
        bodyComponent.setSize(.75f * PPM, .75f * PPM);
        setBottomCenterToPoint(bodyComponent.getCollisionBox(), spawn);
        bodyComponent.setPreProcess(delta -> bodyComponent.setVelocity(trajectory));
        // hitbox
        Fixture hitbox = new Fixture(this, DAMAGEABLE_BOX);
        hitbox.setSize(.75f * PPM, .75f * PPM);
        bodyComponent.addFixture(hitbox);
        // damagebox
        Fixture damagebox = new Fixture(this, DAMAGER_BOX);
        damagebox.setSize(.75f * PPM, .75f * PPM);
        bodyComponent.addFixture(damagebox);
        return bodyComponent;
    }

    private PathfindingComponent definePathfindingComponent() {
        PathfindingComponent pathfindingComponent = new PathfindingComponent(
                () -> getComponent(BodyComponent.class).getCenter(), () -> getMegaman().getFocus(),
                target -> {
                    Vector2 targetCenter = centerPoint(target);
                    Vector2 thisCenter = getComponent(BodyComponent.class).getCenter();
                    float angle = atan2(targetCenter.y - thisCenter.y, targetCenter.x - thisCenter.x);
                    trajectory.set(cos(angle), sin(angle)).scl(SPEED * PPM);
                },
                target -> getComponent(BodyComponent.class).getCollisionBox().overlaps(target));
        pathfindingComponent.setDoAcceptPredicate(node ->
                node.getObjects().stream().noneMatch(o -> o instanceof Block));
        pathfindingComponent.setDoAllowDiagonal(() -> false);
        return pathfindingComponent;
    }

    private SpriteComponent defineSpriteComponent() {
        Sprite sprite = new Sprite();
        sprite.setSize(1.5f * PPM, 1.5f * PPM);
        return new SpriteComponent(sprite, new StandardEnemySpriteAdapter() {

            @Override
            public boolean setPositioning(Wrapper<Rectangle> bounds, Wrapper<Position> position) {
                bounds.setData(getComponent(BodyComponent.class).getCollisionBox());
                position.setData(CENTER);
                return true;
            }

            @Override
            public boolean isFlipX() {
                return trajectory.x < 0f;
            }

        });
    }

    private AnimationComponent defineAnimationComponent() {
        TextureAtlas textureAtlas = gameContext.getAsset(ENEMIES_TEXTURE_ATLAS.getSrc(), TextureAtlas.class);
        return new AnimationComponent(new TimedAnimation(textureAtlas.findRegion("FloatingCan"), 4, .15f));
    }

}
