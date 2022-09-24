package com.game.entities.interactives;

import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Shape2D;
import com.game.core.Entity;
import com.game.core.GameContext2d;
import com.game.entities.blocks.BlockFactory;
import com.game.entities.megaman.Megaman;
import com.game.messages.Message;
import com.game.updatables.UpdatableComponent;
import com.game.utils.ShapeUtils;
import com.game.utils.enums.ProcessState;
import com.game.utils.interfaces.Resettable;
import com.game.utils.interfaces.Updatable;
import com.game.utils.objects.Timer;
import com.game.world.BodyComponent;
import com.game.world.Fixture;
import lombok.Getter;

import java.util.function.Supplier;

import static com.game.constants.Events.*;
import static com.game.constants.MiscellaneousVals.NEXT;
import static com.game.entities.interactives.Gateway.GatewayState.*;
import static com.game.utils.enums.ProcessState.*;
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
    private final Supplier<ProcessState> transSupplier;
    private final Supplier<Shape2D> gateListenerSupplier;

    private boolean wasOverlappingGate;
    @Getter
    private GatewayState state = CLOSED_OPENABLE;

    public Gateway(GameContext2d gameContext, RectangleMapObject gateObj, Supplier<Megaman> megamanSupplier,
                   Supplier<ProcessState> transSupplier) {
        super(gameContext);
        this.transSupplier = transSupplier;
        this.gateBounds = gateObj.getRectangle();
        BlockFactory.create(gameContext, gateObj);
        this.nextGameRoom = gateObj.getProperties().get("next", String.class);
        gateListenerSupplier = () -> {
            Megaman megaman = megamanSupplier.get();
            if (megaman == null) {
                return null;
            }
            Fixture gateListener = megaman.getComponent(BodyComponent.class)
                    .getFirstMatchingFixture(GATE_LISTENER).orElse(null);
            if (gateListener == null) {
                throw new IllegalStateException("Megaman does not have " + GATE_LISTENER + " fixture");
            }
            return gateListener.getFixtureShape();
        };
        addComponent(updatableComponent());
    }

    @Override
    public void reset() {
        timer.reset();
        state = CLOSED_OPENABLE;
    }

    private boolean overlappingGateNow() {
        Shape2D gateListener = gateListenerSupplier.get();
        return gateListener != null && ShapeUtils.overlap(gateBounds, gateListener);
    }

    private ProcessState getTransState() {
        return transSupplier.get();
    }

    private UpdatableComponent updatableComponent() {
        UpdatableComponent updatableComponent = new UpdatableComponent();
        // update for closed openable
        Updatable initOpeningUpdatable = delta -> {
            boolean overlappingGateNow = overlappingGateNow();
            if (!wasOverlappingGate && overlappingGateNow) {
                timer.reset();
                state = OPENING;
                gameContext.addMessage(new Message(this, GATE_INIT_OPENING));
            }
            wasOverlappingGate = overlappingGateNow;
        };
        updatableComponent.addUpdatable(initOpeningUpdatable, () -> state == CLOSED_OPENABLE);
        // update for opening
        Updatable openingUpdatable = delta -> {
            timer.update(delta);
            if (timer.isFinished()) {
                timer.reset();
                state = OPEN_WAITING;
                gameContext.addMessage(new Message(this, NEXT, nextGameRoom));
                gameContext.addMessage(new Message(this, GATE_FINISH_OPENING));
            }
        };
        updatableComponent.addUpdatable(openingUpdatable, () -> state == OPENING && !timer.isFinished());
        // update for open waiting
        Updatable openWaitingUpdatable = delta -> {
            if (getTransState() == END) {
                state = CLOSING;
                gameContext.addMessage(new Message(this, GATE_INIT_CLOSING));
            }
        };
        updatableComponent.addUpdatable(openWaitingUpdatable, () -> state == OPEN_WAITING && getTransState() != null);
        // update for closing
        Updatable closingUpdatable = delta -> {
            timer.update(delta);
            if (timer.isFinished()) {
                timer.reset();
                state = SEALED;
                gameContext.addMessage(new Message(this, GATE_FINISH_CLOSING));
            }
        };
        updatableComponent.addUpdatable(closingUpdatable, () -> state == CLOSING && !timer.isFinished());
        return updatableComponent;
    }

}
