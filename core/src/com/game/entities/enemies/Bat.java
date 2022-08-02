package com.game.entities.enemies;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.game.GameContext2d;
import com.game.animations.AnimationComponent;
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
import lombok.Getter;
import lombok.Setter;

import java.util.*;
import java.util.function.Supplier;

import static com.badlogic.gdx.math.MathUtils.*;
import static com.game.ConstVals.TextureAsset.*;
import static com.game.ConstVals.ViewVals.PPM;
import static com.game.entities.enemies.Bat.BatStatus.*;
import static com.game.utils.UtilMethods.*;
import static com.game.utils.UtilMethods.centerPoint;
import static com.game.utils.UtilMethods.setBottomCenterToPoint;
import static com.game.utils.enums.Position.CENTER;
import static com.game.world.BodyType.*;
import static com.game.world.FixtureType.DAMAGEABLE_BOX;
import static com.game.world.FixtureType.DAMAGER_BOX;

@Getter
@Setter
public class Bat extends AbstractEnemy {

    public enum BatStatus {
        HANGING, OPEN_EYES, OPEN_WINGS, FLYING
    }

    private static final float SPEED = 3f;

    private final Vector2 trajectory = new Vector2();
    private final Rectangle scannerBox = new Rectangle();
    private final Timer releaseFromPerchTimer = new Timer(.25f);

    private BatStatus currentStatus = HANGING;

    public Bat(GameContext2d gameContext, Supplier<Megaman> megamanSupplier, Vector2 spawn) {
        super(gameContext, megamanSupplier, .05f);
        scannerBox.setSize(3f * PPM, 3f * PPM);
        addComponent(defineSpriteComponent());
        addComponent(defineAnimationComponent());
        addComponent(defineUpdatableComponent());
        addComponent(defineBodyComponent(spawn));
        addComponent(definePathfindingComponent());
    }

    private UpdatableComponent defineUpdatableComponent() {
        return new UpdatableComponent(new StandardEnemyUpdater() {
            @Override
            public void update(float delta) {
                super.update(delta);
                if (getCurrentStatus().equals(FLYING)) {
                    return;
                }
                if (getCurrentStatus().equals(HANGING)) {
                    scannerBox.setCenter(getComponent(BodyComponent.class).getCenter());
                    if (scannerBox.contains(getMegaman().getFocus())) {
                        setCurrentStatus(OPEN_EYES);
                    }
                }
                if (equalsAny(getCurrentStatus(), OPEN_EYES, OPEN_WINGS)) {
                    releaseFromPerchTimer.update(delta);
                    if (releaseFromPerchTimer.isFinished()) {
                        if (getCurrentStatus().equals(OPEN_EYES)) {
                            setCurrentStatus(OPEN_WINGS);
                            releaseFromPerchTimer.reset();
                        } else {
                            setCurrentStatus(FLYING);
                        }
                    }
                }
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
        Supplier<String> keySupplier = () -> currentStatus.toString();
        Map<String, TimedAnimation> timedAnimations = Map.of(
                HANGING.toString(), new TimedAnimation(textureAtlas.findRegion("BatHang")),
                FLYING.toString(), new TimedAnimation(textureAtlas.findRegion("BatFly"), 2, .1f),
                OPEN_EYES.toString(), new TimedAnimation(textureAtlas.findRegion("BatOpenEyes")),
                OPEN_WINGS.toString(), new TimedAnimation(textureAtlas.findRegion("BatOpenWings")));
        return new AnimationComponent(keySupplier, timedAnimations::get);
    }

    private BodyComponent defineBodyComponent(Vector2 spawn) {
        BodyComponent bodyComponent = new BodyComponent(DYNAMIC);
        bodyComponent.setSize(.75f * PPM, .75f * PPM);
        setBottomCenterToPoint(bodyComponent.getCollisionBox(), spawn);
        bodyComponent.setPreProcess(delta -> {
            if (getCurrentStatus().equals(FLYING)) {
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
        pathfindingComponent.setDoUpdatePredicate(delta -> getCurrentStatus().equals(FLYING));
        pathfindingComponent.setDoAcceptPredicate(node ->
                node.getObjects().stream().noneMatch(o -> o instanceof Block));
        pathfindingComponent.setDoAllowDiagonal(() -> false);
        return pathfindingComponent;
    }

}
