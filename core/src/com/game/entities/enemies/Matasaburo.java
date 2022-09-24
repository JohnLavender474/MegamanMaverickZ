package com.game.entities.enemies;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.game.animations.AnimationComponent;
import com.game.animations.TimedAnimation;
import com.game.core.Entity;
import com.game.core.GameContext2d;
import com.game.core.IAssetLoader;
import com.game.damage.DamageNegotiation;
import com.game.damage.Damager;
import com.game.entities.contracts.Faceable;
import com.game.entities.contracts.Facing;
import com.game.entities.megaman.Megaman;
import com.game.entities.projectiles.Bullet;
import com.game.entities.projectiles.ChargedShot;
import com.game.entities.projectiles.ChargedShotDisintegration;
import com.game.entities.projectiles.Fireball;
import com.game.shapes.ShapeComponent;
import com.game.shapes.ShapeHandle;
import com.game.sprites.SpriteComponent;
import com.game.updatables.UpdatableComponent;
import com.game.world.BodyComponent;
import com.game.world.Fixture;
import lombok.Getter;
import lombok.Setter;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Supplier;

import static com.badlogic.gdx.graphics.Color.BLUE;
import static com.badlogic.gdx.graphics.Color.GREEN;
import static com.game.constants.MiscellaneousVals.FUNCTION;
import static com.game.constants.StatVals.MAX_HEALTH;
import static com.game.constants.TextureAsset.ENEMIES_1;
import static com.game.constants.ViewVals.PPM;
import static com.game.entities.contracts.Facing.F_LEFT;
import static com.game.entities.contracts.Facing.F_RIGHT;
import static com.game.utils.UtilMethods.setBottomCenterToPoint;
import static com.game.world.BodyType.DYNAMIC;
import static com.game.world.FixtureType.*;

@Setter
@Getter
public class Matasaburo extends AbstractEnemy implements Faceable {

    private static final float DAMAGE_DURATION = .35f;
    private static final Vector2 DEEP_BLOW_FORCE_LOL = new Vector2(.75f, 0f);

    private Facing facing;

    public Matasaburo(GameContext2d gameContext, Supplier<Megaman> megamanSupplier, Vector2 spawn) {
        super(gameContext, megamanSupplier, DAMAGE_DURATION);
        addComponent(bodyComponent(spawn));
        addComponent(updatableComponent());
        addComponent(spriteComponent());
        addComponent(animationComponent(gameContext));
        addComponent(shapeComponent());
    }

    @Override
    protected Map<Class<? extends Damager>, DamageNegotiation> defineDamageNegotiations() {
        return new HashMap<>() {{
            put(Bullet.class, new DamageNegotiation(10));
            put(Fireball.class, new DamageNegotiation(MAX_HEALTH));
            put(ChargedShot.class, new DamageNegotiation(damager ->
                    ((ChargedShot) damager).isFullyCharged() ? MAX_HEALTH : 10));
            put(ChargedShotDisintegration.class, new DamageNegotiation(damager ->
                    ((ChargedShotDisintegration) damager).isFullyCharged() ? 15 : 5));
        }};
    }

    private UpdatableComponent updatableComponent() {
        return new UpdatableComponent(new StandardEnemyUpdater() {
            @Override
            public void update(float delta) {
                super.update(delta);
                BodyComponent bodyComponent = getComponent(BodyComponent.class);
                BodyComponent megaBody = getMegaman().getComponent(BodyComponent.class);
                setFacing(megaBody.isLeftOf(bodyComponent) ? F_LEFT : F_RIGHT);
            }
        });
    }

    private BodyComponent bodyComponent(Vector2 spawn) {
        BodyComponent bodyComponent = new BodyComponent(DYNAMIC);
        bodyComponent.setSize(PPM, PPM);
        setBottomCenterToPoint(bodyComponent.getCollisionBox(), spawn);
        Fixture blowFixture = new Fixture(this, new Rectangle(0f, 0f, 10f * PPM, PPM), FORCE);
        blowFixture.putUserData(FUNCTION, (Function<Entity, Vector2>) e ->
            isFacing(F_LEFT) ? DEEP_BLOW_FORCE_LOL.cpy().scl(-PPM) : DEEP_BLOW_FORCE_LOL.cpy().scl(PPM));
        bodyComponent.addFixture(blowFixture);
        bodyComponent.addFixture(new Fixture(this, bodyComponent.getCollisionBox(), DAMAGER));
        bodyComponent.addFixture(new Fixture(this, bodyComponent.getCollisionBox(), DAMAGEABLE));
        bodyComponent.setPreProcess(delta -> {
            if (isFacing(F_LEFT)) {
                blowFixture.setOffset(-5f * PPM, 0f);
            } else {
                blowFixture.setOffset(5f * PPM, 0f);
            }
        });
        return bodyComponent;
    }

    private SpriteComponent spriteComponent() {
        Sprite sprite = new Sprite();
        sprite.setSize(1.5f * PPM, 1.5f * PPM);
        return new SpriteComponent(sprite, new StandardEnemySpriteProcessor() {

            @Override
            public boolean isFlipX() {
                return isFacing(F_LEFT);
            }

        });
    }

    private AnimationComponent animationComponent(IAssetLoader assetLoader) {
        TextureRegion region = assetLoader.getAsset(ENEMIES_1.getSrc(), TextureAtlas.class).findRegion("Matasaburo");
        TimedAnimation animation = new TimedAnimation(region, 6, .1f);
        return new AnimationComponent(animation);
    }

    private ShapeComponent shapeComponent() {
        BodyComponent bodyComponent = getComponent(BodyComponent.class);
        ShapeHandle shapeHandle1 = new ShapeHandle();
        shapeHandle1.setShapeSupplier(bodyComponent::getCollisionBox);
        shapeHandle1.setColorSupplier(() -> GREEN);
        ShapeHandle shapeHandle2 = new ShapeHandle();
        Fixture blowFixture = bodyComponent.getFirstMatchingFixture(FORCE).orElseThrow();
        shapeHandle2.setShapeSupplier(blowFixture::getFixtureShape);
        shapeHandle2.setColorSupplier(() -> BLUE);
        return new ShapeComponent(shapeHandle1, shapeHandle2);
    }

}
