package com.game.entities.interactive;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Shape2D;
import com.game.Entity;
import com.game.GameContext2d;
import com.game.entities.blocks.BlockFactory;
import com.game.entities.megaman.Megaman;
import com.game.events.Event;
import com.game.messages.Message;
import com.game.sprites.SpriteComponent;
import com.game.sprites.SpriteProcessor;
import com.game.updatables.UpdatableComponent;
import com.game.utils.ShapeUtils;
import com.game.utils.enums.Position;
import com.game.utils.interfaces.Resettable;
import com.game.utils.interfaces.Updatable;
import com.game.utils.objects.Timer;
import com.game.utils.objects.Wrapper;
import com.game.world.BodyComponent;
import com.game.world.Fixture;
import lombok.Getter;

import java.util.Map;
import java.util.function.Supplier;

import static com.game.GlobalKeys.NEXT;
import static com.game.ViewVals.PPM;
import static com.game.assets.TextureAsset.*;
import static com.game.events.EventType.*;
import static com.game.entities.interactive.Door.DoorState.*;
import static com.game.messages.MessageType.NEXT_GAME_ROOM_REQUEST;
import static com.game.utils.enums.Position.*;
import static com.game.world.FixtureType.*;

public class Door extends Entity implements Resettable {

    public enum DoorState {
        CLOSED_OPENABLE,
        OPENING,
        OPEN,
        CLOSING,
        CLOSED
    }

    private static final float DURATION = .25f;

    @Getter
    private final String nextGameRoom;
    private final Rectangle gateBounds;
    private final Supplier<Megaman> megamanSupplier;
    private final Timer timer = new Timer(DURATION, true);

    private boolean gameRoomTransFinished;
    @Getter
    private DoorState state = CLOSED_OPENABLE;

    public Door(GameContext2d gameContext, RectangleMapObject gateObj, Supplier<Megaman> megamanSupplier) {
        super(gameContext);
        this.megamanSupplier = megamanSupplier;
        this.gateBounds = gateObj.getRectangle();
        this.nextGameRoom = gateObj.getProperties().get("next", String.class);
        BlockFactory.create(gameContext, gateObj);
        addComponent(updatableComponent());
        TextureAtlas textureAtlas = gameContext.getAsset(DOORS.getSrc(), TextureAtlas.class);
        String regionKey = gateObj.getProperties().get("regionKey", String.class);
        TextureRegion textureRegion = textureAtlas.findRegion(regionKey);
        addComponent(spriteComponent(textureRegion));
    }

    @Override
    public void listenToEvent(Event event, float delta) {
        super.listenToEvent(event, delta);
        if (event.is(END_GAME_ROOM_TRANS)) {
            gameRoomTransFinished = true;
        }
    }

    @Override
    public void reset() {
        timer.reset();
        state = CLOSED_OPENABLE;
    }

    private Shape2D getListenerBounds() {
        Megaman megaman = megamanSupplier.get();
        if (megaman == null) {
            return null;
        }
        Fixture gateListener = megaman.getComponent(BodyComponent.class).getFirstMatchingFixture(GATE_LISTENER)
                .orElseThrow(() -> new IllegalStateException("Megaman doesn't have " + GATE_LISTENER + " fixture"));
        return gateListener.getFixtureShape();
    }

    private boolean overlappingGate() {
        Shape2D listenerBounds = getListenerBounds();
        return listenerBounds != null && ShapeUtils.overlap(gateBounds, listenerBounds);
    }

    private UpdatableComponent updatableComponent() {
        UpdatableComponent updatableComponent = new UpdatableComponent();
        // update for closed openable
        Updatable initOpeningUpdatable = delta -> {
            if (overlappingGate()) {
                timer.reset();
                state = OPENING;
                gameContext.addEvent(new Event(GATE_INIT_OPENING));
            }
        };
        updatableComponent.addUpdatable(initOpeningUpdatable, () -> state == CLOSED_OPENABLE);
        // update for opening
        Updatable openingUpdatable = delta -> {
            timer.update(delta);
            if (timer.isFinished()) {
                timer.reset();
                state = OPEN;
                gameContext.addEvent(new Event(GATE_FINISH_OPENING));
                gameContext.addMessage(new Message(this, NEXT_GAME_ROOM_REQUEST, Map.of(NEXT, nextGameRoom)));
            }
        };
        updatableComponent.addUpdatable(openingUpdatable, () -> state == OPENING);
        // update for open waiting
        Updatable openWaitingUpdatable = delta -> {
            if (gameRoomTransFinished) {
                gameRoomTransFinished = false;
                state = CLOSING;
                gameContext.addEvent(new Event(GATE_INIT_CLOSING));
            }
        };
        updatableComponent.addUpdatable(openWaitingUpdatable, () -> state == OPEN);
        // update for closing
        Updatable closingUpdatable = delta -> {
            timer.update(delta);
            if (timer.isFinished()) {
                timer.reset();
                state = CLOSED;
                gameContext.addEvent(new Event(GATE_FINISH_CLOSING));
            }
        };
        updatableComponent.addUpdatable(closingUpdatable, () -> state == CLOSING);
        return updatableComponent;
    }

    private SpriteComponent spriteComponent(TextureRegion textureRegion) {
        Sprite sprite = new Sprite();
        sprite.setSize(2f * PPM, 2f * PPM);
        return new SpriteComponent(sprite, new SpriteProcessor() {

            @Override
            public boolean setPositioning(Wrapper<Rectangle> bounds, Wrapper<Position> position) {
                bounds.setData(gateBounds);
                position.setData(BOTTOM_CENTER);
                return true;
            }

            @Override
            public boolean isHidden() {
                return state == OPEN;
            }

        });
    }

}
