package com.game.cull;

import com.badlogic.gdx.math.Rectangle;
import com.game.core.Component;
import com.game.utils.objects.Timer;
import lombok.Getter;

import java.util.function.Supplier;

@Getter
public class CullOutOfCamBoundsComponent extends Component {

    private final Timer cullTimer;
    private final Supplier<Rectangle> boundsSupplier;

    public CullOutOfCamBoundsComponent(Supplier<Rectangle> boundsSupplier, float graceDuration) {
        this.boundsSupplier = boundsSupplier;
        this.cullTimer = new Timer(graceDuration);
    }

    public Rectangle getBounds() {
        return boundsSupplier.get();
    }

    public void updateCullTimer(float delta) {
        cullTimer.update(delta);
    }

    public void resetCullTimer() {
        cullTimer.reset();
    }

    public boolean isCullTimerFinished() {
        return cullTimer.isFinished();
    }

}
