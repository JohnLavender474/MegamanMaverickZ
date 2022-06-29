package com.game.megaman.behaviors;

import com.game.GameContext2d;
import com.game.behaviors.Behavior;
import com.game.behaviors.BehaviorComponent;
import com.game.behaviors.BehaviorType;
import com.game.controllers.ControllerButton;
import com.game.megaman.Megaman;
import com.game.utils.Direction;
import com.game.contracts.Facing;
import com.game.world.BodyComponent;
import com.game.world.BodySense;

import static com.game.ConstVals.ViewVals.PPM;

public class MegamanRun extends Behavior {

    public static final float RUN_SPEED = 4f;

    private final Megaman megaman;
    private final GameContext2d gameContext;
    private final BodyComponent bodyComponent;
    private final BehaviorComponent behaviorComponent;

    public MegamanRun(Megaman megaman, GameContext2d gameContext) {
        this.megaman = megaman;
        this.gameContext = gameContext;
        this.bodyComponent = megaman.getComponent(BodyComponent.class);
        this.behaviorComponent = megaman.getComponent(BehaviorComponent.class);
        addOverride(() -> behaviorComponent.is(BehaviorType.DAMAGED));
        addOverride(() -> behaviorComponent.is(BehaviorType.CLIMBING));
        addOverride(() -> behaviorComponent.is(BehaviorType.AIR_DASHING));
        addOverride(() -> behaviorComponent.is(BehaviorType.GROUND_SLIDING));
    }

    @Override
    protected boolean evaluate(float delta) {
        if (gameContext.isPressed(ControllerButton.LEFT)) {
            return !bodyComponent.is(BodySense.TOUCHING_BLOCK_LEFT);
        } else if (gameContext.isPressed(ControllerButton.RIGHT)) {
            return !bodyComponent.is(BodySense.TOUCHING_BLOCK_RIGHT);
        }
        return false;
    }

    @Override
    protected void init() {
        behaviorComponent.setIs(BehaviorType.RUNNING);
    }

    @Override
    protected void act(float delta) {
        float x = RUN_SPEED * PPM;
        if (megaman.isFacing(Facing.LEFT)) {
            x *= -1f;
        }
        bodyComponent.applyImpulse(x, 0f);
    }

    @Override
    protected void end() {
        behaviorComponent.setIsNot(BehaviorType.RUNNING);
    }

}
