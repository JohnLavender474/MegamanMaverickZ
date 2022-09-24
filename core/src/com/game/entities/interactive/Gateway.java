package com.game.entities.interactive;

import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Shape2D;
import com.game.Entity;
import com.game.GameContext2d;
import com.game.entities.blocks.BlockFactory;
import com.game.entities.megaman.Megaman;
import com.game.events.Event;
import com.game.messages.Message;
import com.game.updatables.UpdatableComponent;
import com.game.utils.ShapeUtils;
import com.game.utils.interfaces.Resettable;
import com.game.utils.interfaces.Updatable;
import com.game.utils.objects.Timer;
import com.game.world.BodyComponent;
import com.game.world.Fixture;
import lombok.Getter;

import java.util.Map;
import java.util.function.Supplier;

import static com.game.GlobalKeys.ENTITY;
import static com.game.GlobalKeys.NEXT;
import static com.game.events.EventType.*;
import static com.game.entities.interactive.Gateway.GatewayState.*;
import static com.game.messages.MessageType.NEXT_GAME_ROOM_REQUEST;
import static com.game.world.FixtureType.*;

public class Gateway extends Entity implements Resettable {

    public enum GatewayState {
        CLOSED_OPENABLE,
        OPEN_WAITING,
        OPENING,
        CLOSING,
        SEALED
    }

    private static final float DURATION = 1f;

    @Getter
    private final String nextGameRoom;
    private final Rectangle gateBounds;
    private final Timer timer = new Timer(DURATION);
    private final Supplier<Megaman> megamanSupplier;

    @Getter
    private GatewayState state = CLOSED_OPENABLE;
    private boolean gameRoomTrans;

    public Gateway(GameContext2d gameContext, RectangleMapObject gateObj, Supplier<Megaman> megamanSupplier) {
        super(gameContext);
        this.megamanSupplier = megamanSupplier;
        this.gateBounds = gateObj.getRectangle();
        this.nextGameRoom = gateObj.getProperties().get("next", String.class);
        BlockFactory.create(gameContext, gateObj);
        addComponent(updatableComponent());
    }

    @Override
    public void listenToEvent(Event event, float delta) {
        super.listenToEvent(event, delta);
        if (event.is(BEGIN_GAME_ROOM_TRANS) || event.is(CONTINUE_GAME_ROOM_TRANS)) {
            gameRoomTrans = true;
        } else if (event.is(END_GAME_ROOM_TRANS)) {
            gameRoomTrans = false;
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
                gameContext.addEvent(new Event(GATE_INIT_OPENING, ENTITY, this));
            }
        };
        updatableComponent.addUpdatable(initOpeningUpdatable, () -> state == CLOSED_OPENABLE);
        // update for opening
        Updatable openingUpdatable = delta -> {
            timer.update(delta);
            if (timer.isFinished()) {
                timer.reset();
                state = OPEN_WAITING;
                gameContext.addEvent(new Event(GATE_FINISH_OPENING));
                gameContext.addMessage(new Message(this, NEXT_GAME_ROOM_REQUEST, Map.of(NEXT, nextGameRoom)));
            }
        };
        updatableComponent.addUpdatable(openingUpdatable, () -> state == OPENING);
        // update for open waiting
        Updatable openWaitingUpdatable = delta -> {
            if (!gameRoomTrans) {
                state = CLOSING;
                gameContext.addEvent(new Event(GATE_INIT_CLOSING));
            }
        };
        updatableComponent.addUpdatable(openWaitingUpdatable, () -> state == OPEN_WAITING);
        // update for closing
        Updatable closingUpdatable = delta -> {
            timer.update(delta);
            if (timer.isFinished()) {
                timer.reset();
                state = SEALED;
                gameContext.addEvent(new Event(GATE_FINISH_CLOSING));
            }
        };
        updatableComponent.addUpdatable(closingUpdatable, () -> state == CLOSING);
        return updatableComponent;
    }

}
