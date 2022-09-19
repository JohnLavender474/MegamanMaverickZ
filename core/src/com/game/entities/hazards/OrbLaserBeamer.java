package com.game.entities.hazards;

import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.math.*;
import com.game.core.Entity;
import com.game.core.GameContext2d;
import com.game.core.constants.RenderingGround;
import com.game.core.constants.ViewVals;
import com.game.entities.megaman.Megaman;
import com.game.sounds.SoundComponent;
import com.game.utils.UtilMethods;
import com.game.world.BodyComponent;
import com.game.world.BodyType;

import java.util.function.Supplier;

import static com.game.core.constants.ViewVals.*;

public class OrbLaserBeamer extends Entity {

    public OrbLaserBeamer(GameContext2d gameContext, Supplier<Megaman> megamanSupplier, Vector2 spawn) {
        super(gameContext);
        addComponent(bodyComponent(spawn));
        addComponent(soundComponent(gameContext.getViewport(RenderingGround.PLAYGROUND).getCamera()));
    }

    private BodyComponent bodyComponent(Vector2 spawn) {
        BodyComponent bodyComponent = new BodyComponent(BodyType.ABSTRACT);

        return bodyComponent;
    }

    private SoundComponent soundComponent(Camera camera) {
        SoundComponent soundComponent = new SoundComponent();
        soundComponent.setUpdatable(delta -> {
            Vector2 camCenter = new Vector2(camera.position.x, camera.position.y);
            Rectangle camBounds = new Rectangle(camCenter.x - (ViewVals.VIEW_WIDTH * PPM) / 2f,
                    camCenter.y - (ViewVals.VIEW_HEIGHT * PPM) / 2f, VIEW_WIDTH * PPM, VIEW_HEIGHT * PPM);
            // check if laser line overlaps cam bounds

        });
        return soundComponent;
    }

}
