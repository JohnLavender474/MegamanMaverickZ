package com.game.entities.megaman.behaviors;

import com.game.GameContext2d;
import com.game.behaviors.Behavior;
import com.game.behaviors.BehaviorComponent;
import com.game.behaviors.BehaviorType;
import com.game.controllers.ControllerButton;
import com.game.entities.megaman.Megaman;
import com.game.contracts.Facing;
import com.game.utils.Timer;
import com.game.world.BodyComponent;
import com.game.world.BodySense;

import static com.game.ConstVals.ViewVals.PPM;

public class MegamanGroundSlide extends Behavior {

    public static final float GROUND_SLIDE_SPEED = 15f;
    public static final float GROUND_SLIDE_DURATION = 0.3f;

    private final Megaman megaman;
    private final GameContext2d gameContext;
    private final BodyComponent bodyComponent;
    private final BehaviorComponent behaviorComponent;
    private final Timer groundSlideTimer = new Timer(GROUND_SLIDE_DURATION);

    public MegamanGroundSlide(Megaman megaman, GameContext2d gameContext) {
        this.megaman = megaman;
        this.gameContext = gameContext;
        this.bodyComponent = megaman.getComponent(BodyComponent.class);
        this.behaviorComponent = megaman.getComponent(BehaviorComponent.class);
        addOverride(() -> behaviorComponent.is(BehaviorType.DAMAGED));
        addOverride(() -> behaviorComponent.is(BehaviorType.CLIMBING));
        addOverride(() -> behaviorComponent.is(BehaviorType.AIR_DASHING));
    }

    @Override
    protected boolean evaluate(float delta) {
        // If facing left and touching left wall, stop
        if (megaman.isFacing(Facing.LEFT) && bodyComponent.is(BodySense.TOUCHING_BLOCK_LEFT)) {
            return false;
        }
        // If facing right and touching right wall, stop
        if (megaman.isFacing(Facing.RIGHT) && bodyComponent.is(BodySense.TOUCHING_BLOCK_RIGHT)) {
            return false;
        }
        // If already ground sliding and head is touching block, then continue ground sliding even if
        // timer is depleted or down-and-A button combo is not pressed
        if (behaviorComponent.is(BehaviorType.GROUND_SLIDING) &&
                bodyComponent.is(BodySense.HEAD_TOUCHING_BLOCK)) {
            return true;
        }
        // Down-and-A button combo must be pressed and ground slide timer must not be depleted
        return gameContext.isPressed(ControllerButton.DOWN) &&
                gameContext.isPressed(ControllerButton.A) &&
                bodyComponent.is(BodySense.FEET_ON_GROUND) &&
                !groundSlideTimer.isFinished();
    }

    @Override
    protected void init() {
        behaviorComponent.setIs(BehaviorType.GROUND_SLIDING);
        groundSlideTimer.reset();
    }

    @Override
    protected void act(float delta) {
        groundSlideTimer.update(delta);
        float x = GROUND_SLIDE_SPEED * PPM;
        if (megaman.isFacing(Facing.LEFT)) {
            x *= -1f;
        }
        bodyComponent.applyImpulse(x, 0f);
    }

    @Override
    protected void end() {
        behaviorComponent.setIsNot(BehaviorType.GROUND_SLIDING);
    }

}
