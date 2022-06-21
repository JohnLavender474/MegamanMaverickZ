package com.game.entities.megaman.behaviors;

import com.game.core.GameContext2d;
import com.game.behaviors.Behavior;
import com.game.behaviors.BehaviorComponent;
import com.game.behaviors.BehaviorType;
import com.game.entities.megaman.Megaman;
import com.game.utils.Timer;
import com.game.world.BodyComponent;
import com.game.world.BodySense;

import static com.game.entities.megaman.behaviors.MegamanJump.JumpType.GROUND_JUMP;
import static com.game.entities.megaman.behaviors.MegamanJump.JumpType.WALL_JUMP;

public class MegamanJump extends Behavior {

    public enum JumpType {
        GROUND_JUMP,
        WALL_JUMP
    }

    // Jump speed in world units, is counteracted by gravity force that increases each frame not grounded,
    // see body pre-process updater initialized in Megaman.java defineBodyComponent().
    public static final float JUMP_SPEED = 7f;
    // Wall jump horizontal bump init speed
    public static final float WALL_JUMP_BUMP_INIT_SPEED = 3f;
    // Wall jump horizontal bump duration
    public static final float WALL_JUMP_BUMP_DURATION = 0.15f;

    private final GameContext2d gameContext;
    private final BodyComponent bodyComponent;
    private final BehaviorComponent behaviorComponent;
    private final Timer wallJumpBumpTimer = new Timer(WALL_JUMP_BUMP_DURATION);

    private JumpType jumpType;
    private boolean wallJumpBumpLeft;

    public MegamanJump(Megaman megaman, GameContext2d gameContext) {
        this.gameContext = gameContext;
        this.bodyComponent = megaman.getComponent(BodyComponent.class);
        this.behaviorComponent = megaman.getComponent(BehaviorComponent.class);
        addOverride(() -> bodyComponent.is(BodySense.HEAD_TOUCHING_BLOCK));
        addOverride(() -> behaviorComponent.is(BehaviorType.DAMAGED));
        addOverride(() -> behaviorComponent.is(BehaviorType.CLIMBING));
        addOverride(() -> behaviorComponent.is(BehaviorType.AIR_DASHING));
        addOverride(() -> behaviorComponent.is(BehaviorType.GROUND_SLIDING));
    }

    @Override
    protected boolean evaluate(float delta) {
        // If already jumping, then continue if jump button still pressed, not begun wall sliding, and not falling,
        // otherwise return true if jump button is just pressed and is grounded or wall sliding
        /*
        return behaviorComponent.is(BehaviorType.JUMPING) ?
                // case 1
                bodyComponent.getImpulse().y >= 0f &&
                        !bodyComponent.isColliding(Direction.DOWN) &&
                        gameContext.isPressed(ControllerButton.A) &&
                        !behaviorComponent.is(BehaviorType.WALL_SLIDING) :
                // case 2
                gameContext.isJustPressed(ControllerButton.A) &&
                        (bodyComponent.is(BodySense.FEET_ON_GROUND) ||
                                behaviorComponent.is(BehaviorType.WALL_SLIDING));
         */
        return false;
    }

    @Override
    protected void init() {
        behaviorComponent.setIs(BehaviorType.JUMPING);
        if (behaviorComponent.is(BehaviorType.WALL_SLIDING)) {
            jumpType = WALL_JUMP;
            wallJumpBumpTimer.reset();
            behaviorComponent.setIsNot(BehaviorType.WALL_SLIDING);
            wallJumpBumpLeft = bodyComponent.is(BodySense.TOUCHING_BLOCK_RIGHT);
        } else {
            jumpType = GROUND_JUMP;
        }
    }

    @Override
    protected void act(float delta) {
        /*
        bodyComponent.getImpulse().y += JUMP_SPEED * PPM;
        if (jumpType == WALL_JUMP && !wallJumpBumpTimer.isFinished()) {
            wallJumpBumpTimer.update(delta);
            float x = UtilMethods.interpolate(
                    WALL_JUMP_BUMP_INIT_SPEED, 0f, wallJumpBumpTimer.getRatio());
            bodyComponent.getImpulse().x += wallJumpBumpLeft ? -x : x;
        }
         */
    }

    @Override
    protected void end() {
        behaviorComponent.setIsNot(BehaviorType.JUMPING);
    }

}
