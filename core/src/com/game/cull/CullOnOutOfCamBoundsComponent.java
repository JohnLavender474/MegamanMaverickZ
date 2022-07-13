package com.game.cull;

import com.badlogic.gdx.math.Rectangle;
import com.game.Component;
import com.game.utils.Timer;
import lombok.Getter;

import java.util.function.Supplier;

@Getter
public class CullOnOutOfCamBoundsComponent implements Component {

    private final Timer cullTimer;
    private final Supplier<Rectangle> boundsSupplier;

    public CullOnOutOfCamBoundsComponent(Supplier<Rectangle> boundsSupplier, float graceDuration) {
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
