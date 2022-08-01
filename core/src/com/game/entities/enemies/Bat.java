package com.game.entities.enemies;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.game.GameContext2d;
import com.game.animations.AnimationComponent;
import com.game.animations.TimeMarkedRunnable;
import com.game.animations.TimedAnimation;
import com.game.entities.blocks.Block;
import com.game.entities.megaman.Megaman;
import com.game.pathfinding.PathfindingComponent;
import com.game.sprites.SpriteComponent;
import com.game.updatables.UpdatableComponent;
import com.game.utils.enums.Position;
import com.game.utils.objects.Timer;
import com.game.utils.objects.Wrapper;
import com.game.world.BodyComponent;
import com.game.world.Fixture;

import java.util.Map;
import java.util.function.Supplier;

import static com.badlogic.gdx.math.MathUtils.*;
import static com.game.ConstVals.TextureAsset.*;
import static com.game.ConstVals.ViewVals.PPM;
import static com.game.utils.UtilMethods.centerPoint;
import static com.game.utils.UtilMethods.setBottomCenterToPoint;
import static com.game.utils.enums.Position.CENTER;
import static com.game.world.BodyType.*;
import static com.game.world.FixtureType.DAMAGEABLE_BOX;
import static com.game.world.FixtureType.DAMAGER_BOX;

public class Bat extends AbstractEnemy {

    private enum BatStatus {
        FLYING, PERCHED, OPEN_EYES, OPEN_WINGS
    }

    private static final float SPEED = 3f;

    private final Vector2 trajectory = new Vector2();
    private final Timer perchTimer = new Timer(1.5f,
            new TimeMarkedRunnable(1f, () -> batStatus = BatStatus.OPEN_EYES),
            new TimeMarkedRunnable(1.25f, () -> batStatus = BatStatus.OPEN_WINGS));

    private BatStatus batStatus = BatStatus.PERCHED;

    public Bat(GameContext2d gameContext, Supplier<Megaman> megamanSupplier, Vector2 spawn) {
        super(gameContext, megamanSupplier, .05f);
        addComponent(defineSpriteComponent());
        addComponent(defineUpdatableComponent());
        addComponent(defineBodyComponent(spawn));
        addComponent(definePathfindingComponent());
    }

    public boolean isPerched() {
        return !perchTimer.isFinished();
    }

    private UpdatableComponent defineUpdatableComponent() {
        return new UpdatableComponent(new StandardEnemyUpdater() {
            @Override
            public void update(float delta) {
                super.update(delta);
                perchTimer.update(delta);
            }
        });
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
        });
    }

    private AnimationComponent defineAnimationComponent() {
        TextureAtlas textureAtlas = gameContext.getAsset(ENEMIES_TEXTURE_ATLAS.getSrc(), TextureAtlas.class);
        Supplier<String> keySupplier = () -> batStatus.toString();
        Map<String, TimedAnimation> timedAnimations = Map.of();
        return new AnimationComponent(keySupplier, timedAnimations::get);
    }

    private BodyComponent defineBodyComponent(Vector2 spawn) {
        BodyComponent bodyComponent = new BodyComponent(DYNAMIC);
        bodyComponent.setSize(.75f * PPM, .75f * PPM);
        setBottomCenterToPoint(bodyComponent.getCollisionBox(), spawn);
        bodyComponent.setPreProcess(delta -> {
            if (!isPerched()) {
                bodyComponent.setVelocity(trajectory);
            }
        });
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

}
