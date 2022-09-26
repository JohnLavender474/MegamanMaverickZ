package com.game.entities.interactive;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.math.Rectangle;
import com.game.entities.Entity;
import com.game.GameContext2d;
import com.game.animations.AnimationComponent;
import com.game.animations.TimedAnimation;
import com.game.entities.megaman.Megaman;
import com.game.messages.Message;
import com.game.sounds.SoundComponent;
import com.game.sprites.SpriteComponent;
import com.game.sprites.SpriteProcessor;
import com.game.updatables.UpdatableComponent;
import com.game.utils.enums.Position;
import com.game.utils.interfaces.Resettable;
import com.game.utils.interfaces.Updatable;
import com.game.utils.objects.Timer;
import com.game.utils.objects.Wrapper;
import com.game.world.BodyComponent;
import com.game.world.Fixture;
import lombok.Getter;

import java.util.Collection;
import java.util.Map;
import java.util.function.Supplier;

import static com.game.GlobalKeys.NEXT;
import static com.game.ViewVals.PPM;
import static com.game.assets.SoundAsset.*;
import static com.game.assets.TextureAsset.*;
import static com.game.messages.MessageType.*;
import static com.game.entities.interactive.Door.DoorState.*;
import static com.game.messages.MessageType.NEXT_GAME_ROOM_REQUEST;
import static com.game.utils.ShapeUtils.*;
import static com.game.utils.UtilMethods.*;
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

    private static final float DURATION = .5f;

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
        addComponent(updatableComponent());
        String color = gateObj.getProperties().get("color", String.class);
        addComponent(animationComponent(color));
        addComponent(spriteComponent());
        addComponent(new SoundComponent());
    }

    @Override
    public void listenToMessage(Message message) {
        super.listenToMessage(message);
        switch (message.getMessageType()) {
            case PLAYER_SPAWN -> reset();
            case END_GAME_ROOM_TRANS -> gameRoomTransFinished = true;
        }
    }

    @Override
    public void reset() {
        timer.reset();
        state = CLOSED_OPENABLE;
    }

    private boolean overlappingGate() {
        Megaman megaman = megamanSupplier.get();
        if (megaman == null) {
            return false;
        }
        Collection<Fixture> gateListeners = megaman.getComponent(BodyComponent.class).getFixturesOfType(GATE_LISTENER);
        return gateListeners.stream().map(Fixture::getFixtureShape).anyMatch(bounds -> overlap(gateBounds, bounds));
    }

    private UpdatableComponent updatableComponent() {
        UpdatableComponent updatableComponent = new UpdatableComponent();
        // update for closed openable
        Updatable initOpeningUpdatable = delta -> {
            if (overlappingGate()) {
                timer.reset();
                state = OPENING;
                gameContext.sendMessage(new Message(GATE_INIT_OPENING));
                getComponent(SoundComponent.class).requestSound(BOSS_DOOR);
            }
        };
        updatableComponent.addUpdatable(initOpeningUpdatable, () -> state == CLOSED_OPENABLE);
        // update for opening
        Updatable openingUpdatable = delta -> {
            timer.update(delta);
            if (timer.isFinished()) {
                timer.reset();
                state = OPEN;
                gameContext.sendMessage(new Message(GATE_FINISH_OPENING));
                gameContext.sendMessage(new Message(NEXT_GAME_ROOM_REQUEST, NEXT, nextGameRoom));
            }
        };
        updatableComponent.addUpdatable(openingUpdatable, () -> state == OPENING);
        // update for open waiting
        Updatable openWaitingUpdatable = delta -> {
            if (gameRoomTransFinished) {
                gameRoomTransFinished = false;
                state = CLOSING;
                gameContext.sendMessage(new Message(GATE_INIT_CLOSING));
                getComponent(SoundComponent.class).requestSound(BOSS_DOOR);
            }
        };
        updatableComponent.addUpdatable(openWaitingUpdatable, () -> state == OPEN);
        // update for closing
        Updatable closingUpdatable = delta -> {
            timer.update(delta);
            if (timer.isFinished()) {
                timer.reset();
                state = CLOSED;
                gameContext.sendMessage(new Message(GATE_FINISH_CLOSING));
            }
        };
        updatableComponent.addUpdatable(closingUpdatable, () -> state == CLOSING);
        return updatableComponent;
    }

    private SpriteComponent spriteComponent() {
        Sprite sprite = new Sprite();
        sprite.setSize(4f * PPM, 3f * PPM);
        return new SpriteComponent(sprite, new SpriteProcessor() {

            @Override
            public boolean setPositioning(Wrapper<Rectangle> bounds, Wrapper<Position> position) {
                bounds.setData(gateBounds);
                if (equalsAny(state, CLOSED_OPENABLE, OPENING, OPEN)) {
                    position.setData(BOTTOM_LEFT);
                } else {
                    position.setData(BOTTOM_RIGHT);
                }
                return true;
            }

            @Override
            public boolean isHidden() {
                return state == OPEN;
            }

            @Override
            public boolean isFlipX() {
                return equalsAny(state, CLOSING, CLOSED);
            }

        });
    }

    private AnimationComponent animationComponent(String color) {
        TextureAtlas textureAtlas = gameContext.getAsset(DOORS.getSrc(), TextureAtlas.class);
        Supplier<String> keySupplier = () -> {
            switch (state) {
                case CLOSED_OPENABLE, CLOSED -> {
                    return "closed";
                }
                case OPENING -> {
                    return "opening";
                }
                case CLOSING -> {
                    return "closing";
                }
            }
            return null;
        };
        TimedAnimation closed = new TimedAnimation(textureAtlas.findRegion(color + "_closed"));
        TimedAnimation opening = new TimedAnimation(textureAtlas.findRegion(color + "_opening"), 4, .125f, false);
        TimedAnimation closing = new TimedAnimation(opening, true);
        Map<String, TimedAnimation> animationMap = Map.of("closed", closed, "opening", opening, "closing", closing);
        return new AnimationComponent(keySupplier, animationMap::get);
    }

}
