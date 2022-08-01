package com.game.tests.entities;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.game.Entity;
import com.game.animations.AnimationComponent;
import com.game.animations.TimedAnimation;
import com.game.core.IAssetLoader;
import com.game.cull.CullOnCamTransComponent;
import com.game.cull.CullOutOfCamBoundsComponent;
import com.game.debugging.DebugLinesComponent;
import com.game.damage.Damageable;
import com.game.damage.Damager;
import com.game.graph.GraphComponent;
import com.game.health.HealthComponent;
import com.game.pathfinding.PathfindingComponent;
import com.game.sprites.SpriteAdapter;
import com.game.sprites.SpriteComponent;
import com.game.utils.UtilMethods;
import com.game.utils.enums.Position;
import com.game.utils.objects.Timer;
import com.game.utils.objects.Wrapper;
import com.game.world.BodyComponent;
import com.game.world.BodyType;
import com.game.world.Fixture;
import com.game.world.FixtureType;
import lombok.Getter;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import static com.game.ConstVals.TextureAsset.ENEMIES_TEXTURE_ATLAS;
import static com.game.ConstVals.ViewVals.PPM;
import static com.game.utils.UtilMethods.centerPoint;
import static com.game.utils.UtilMethods.setBottomCenterToPoint;

@Getter
public class TestFloatingCan extends Entity implements Damager, Damageable {

    private static final float SPEED = 1.5f;

    private final Vector2 trajectory = new Vector2();
    private final Timer damageTimer = new Timer(.25f);
    private final Map<Class<? extends Damager>, Integer> damageNegotiations = new HashMap<>();

    public TestFloatingCan(IAssetLoader assetLoader, Supplier<TestPlayer> testPlayerSupplier, Vector2 spawn) {
        defineDamageNegotiations();
        addComponent(new CullOnCamTransComponent());
        addComponent(new CullOutOfCamBoundsComponent(() -> getComponent(BodyComponent.class).getCollisionBox(), 1.5f));
        addComponent(definePathfindingComponent(testPlayerSupplier));
        addComponent(defineAnimationComponent(assetLoader));
        addComponent(defineDebugLinesComponent());
        addComponent(defineBodyComponent(spawn));
        addComponent(defineSpriteComponent());
        addComponent(new HealthComponent(30));
        addComponent(defineGraphComponent());
    }

    private void defineDamageNegotiations() {
        damageNegotiations.put(TestBullet.class, 10);
        damageNegotiations.put(TestFireball.class, 30);
        damageNegotiations.put(TestChargedShot.class, 30);
    }

    @Override
    public void takeDamageFrom(Damager damager) {
        damageTimer.reset();
        getComponent(HealthComponent.class).sub(damageNegotiations.get(damager.getClass()));
        Gdx.audio.newSound(Gdx.files.internal("sounds/EnemyDamage.mp3")).play();
    }

    @Override
    public Set<Class<? extends Damager>> getDamagerMaskSet() {
        return damageNegotiations.keySet();
    }

    private GraphComponent defineGraphComponent() {
        return new GraphComponent(() -> getComponent(BodyComponent.class).getCollisionBox(), () -> List.of(this));
    }

    private BodyComponent defineBodyComponent(Vector2 spawn) {
        BodyComponent bodyComponent = new BodyComponent(BodyType.ABSTRACT);
        bodyComponent.setSize(.75f * PPM, .75f * PPM);
        setBottomCenterToPoint(bodyComponent.getCollisionBox(), spawn);
        bodyComponent.setPreProcess(delta -> bodyComponent.setVelocity(trajectory));
        // hitbox
        Fixture hitbox = new Fixture(this, FixtureType.DAMAGEABLE_BOX);
        hitbox.setSize(.75f * PPM, .75f * PPM);
        bodyComponent.addFixture(hitbox);
        // damagebox
        Fixture damagebox = new Fixture(this, FixtureType.DAMAGER_BOX);
        damagebox.setSize(.75f * PPM, .75f * PPM);
        bodyComponent.addFixture(damagebox);
        return bodyComponent;
    }

    private PathfindingComponent definePathfindingComponent(Supplier<TestPlayer> testPlayerSupplier) {
        PathfindingComponent pathfindingComponent = new PathfindingComponent(
                () -> getComponent(BodyComponent.class).getCenter(), () -> testPlayerSupplier.get().getFocus(),
                target -> {
                    Vector2 targetCenter = centerPoint(target);
                    Vector2 thisCenter = getComponent(BodyComponent.class).getCenter();
                    float angle = MathUtils.atan2(targetCenter.y - thisCenter.y, targetCenter.x - thisCenter.x);
                    trajectory.set(MathUtils.cos(angle), MathUtils.sin(angle)).scl(SPEED * PPM);
                },
                target -> getComponent(BodyComponent.class).getCollisionBox().overlaps(target));
        pathfindingComponent.setDoAcceptPredicate(node ->
                node.getObjects().stream().noneMatch(o -> o instanceof TestBlock));
        pathfindingComponent.setDoAllowDiagonal(() -> false);
        return pathfindingComponent;
    }

    private DebugLinesComponent defineDebugLinesComponent() {
        return new DebugLinesComponent(() -> getComponent(PathfindingComponent.class).getPathCpy().stream().map(
                UtilMethods::centerPoint).collect(Collectors.toList()), () -> Color.RED);
    }

    private SpriteComponent defineSpriteComponent() {
        Sprite sprite = new Sprite();
        sprite.setSize(1.5f * PPM, 1.5f * PPM);
        return new SpriteComponent(sprite, new SpriteAdapter() {

            @Override
            public boolean setPositioning(Wrapper<Rectangle> bounds, Wrapper<Position> position) {
                bounds.setData(getComponent(BodyComponent.class).getCollisionBox());
                position.setData(Position.CENTER);
                return true;
            }

            @Override
            public boolean isFlipX() {
                return trajectory.x < 0f;
            }

        });
    }

    private AnimationComponent defineAnimationComponent(IAssetLoader assetLoader) {
        TextureAtlas textureAtlas = assetLoader.getAsset(ENEMIES_TEXTURE_ATLAS.getSrc(), TextureAtlas.class);
        return new AnimationComponent(new TimedAnimation(textureAtlas.findRegion("FloatingCan"), 4, .15f));
    }

}
