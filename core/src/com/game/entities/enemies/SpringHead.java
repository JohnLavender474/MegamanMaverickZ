package com.game.entities.enemies;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.game.animations.AnimationComponent;
import com.game.core.GameContext2d;
import com.game.core.constants.MiscellaneousVals;
import com.game.damage.DamageNegotiation;
import com.game.damage.Damager;
import com.game.entities.contracts.Faceable;
import com.game.entities.contracts.Facing;
import com.game.entities.megaman.Megaman;
import com.game.shapes.ShapeComponent;
import com.game.shapes.ShapeHandle;
import com.game.sprites.SpriteAdapter;
import com.game.sprites.SpriteComponent;
import com.game.updatables.UpdatableComponent;
import com.game.utils.UtilMethods;
import com.game.utils.enums.Position;
import com.game.utils.enums.ProcessState;
import com.game.utils.objects.Process;
import com.game.utils.objects.Wrapper;
import com.game.world.BodyComponent;
import com.game.world.BodyType;
import com.game.world.Fixture;
import com.game.world.FixtureType;
import lombok.Getter;
import lombok.Setter;

import java.util.*;
import java.util.function.Supplier;

import static com.badlogic.gdx.graphics.Color.*;
import static com.game.core.constants.MiscellaneousVals.*;
import static com.game.core.constants.ViewVals.PPM;
import static com.game.entities.contracts.Facing.*;
import static com.game.utils.UtilMethods.*;
import static com.game.utils.enums.Position.*;
import static com.game.world.BodyType.*;
import static com.game.world.FixtureType.*;

@Getter
@Setter
public class SpringHead extends AbstractEnemy implements Faceable {

    private static final float DAMAGE_DURATION = .5f;

    private final Map<ProcessState, FixtureType> leftScannerMap = new EnumMap<>(ProcessState.class);

    private final Set<FixtureType> leftScannerCollection = new HashSet<>();
    private final Set<FixtureType> rightScannerCollection = new HashSet<>();

    private Facing facing;

    public SpringHead(GameContext2d gameContext, Supplier<Megaman> megamanSupplier, RectangleMapObject spawnObj) {
        super(gameContext, megamanSupplier, DAMAGE_DURATION);
        boolean isFacingRight = spawnObj.getProperties().get("right", Boolean.class);
        setFacing(isFacingRight ? F_RIGHT : F_LEFT);
        Vector2 spawn = bottomCenterPoint(spawnObj.getRectangle());
        addComponent(bodyComponent(spawn));
        addComponent(spriteComponent());
        addComponent(animationComponent());
        addComponent(shapeComponent());
    }

    @Override
    protected Map<Class<? extends Damager>, DamageNegotiation> defineDamageNegotiations() {
        return Map.of();
    }

    private UpdatableComponent updatableComponent() {
        return new UpdatableComponent(delta -> {

        });
    }

    private ShapeComponent shapeComponent() {
        List<ShapeHandle> shapeHandles = new ArrayList<>();
        getComponent(BodyComponent.class).getFixtures().forEach(f -> {
            ShapeHandle shapeHandle = new ShapeHandle();
            shapeHandle.setShapeSupplier(f::getFixtureShape);
            shapeHandle.setColorSupplier(() -> RED);
            shapeHandles.add(shapeHandle);
        });
        return new ShapeComponent(shapeHandles);
    }

    private BodyComponent bodyComponent(Vector2 spawn) {
        BodyComponent bodyComponent = new BodyComponent(DYNAMIC);
        bodyComponent.setSize(.25f * PPM, .25f * PPM);
        setBottomCenterToPoint(bodyComponent.getCollisionBox(), spawn);
        // hit circle
        Circle hitCircle = new Circle();
        hitCircle.setRadius(.125f * PPM);
        bodyComponent.addFixture(new Fixture(this, hitCircle, DAMAGER));
        bodyComponent.addFixture(new Fixture(this, hitCircle, DAMAGEABLE));
        // scanners
        Rectangle scannerModel = new Rectangle(0f, 0f, .1f * PPM, .1f * PPM);
        Fixture leftScanner = new Fixture(this, new Rectangle(scannerModel), SCANNER);
        leftScanner.putUserData(COLLECTION, leftScannerCollection);
        leftScanner.setOffset(-.75f * PPM, -.75f * PPM);
        bodyComponent.addFixture(leftScanner);
        Fixture rightScanner = new Fixture(this, new Rectangle(scannerModel), SCANNER);
        return bodyComponent;
    }
    
    private SpriteComponent spriteComponent() {
        Sprite sprite = new Sprite();
        sprite.setSize(1.5f * PPM, 1.5f * PPM);
        return new SpriteComponent(sprite, new SpriteAdapter() {

            @Override
            public boolean setPositioning(Wrapper<Rectangle> bounds, Wrapper<Position> position) {
                bounds.setData(getComponent(BodyComponent.class).getCollisionBox());
                position.setData(BOTTOM_CENTER);
                return true;
            }

            @Override
            public boolean isFlipX() {
                return isFacing(F_LEFT);
            }

        });
    }
    
    private AnimationComponent animationComponent() {
        return null;
    }

}
