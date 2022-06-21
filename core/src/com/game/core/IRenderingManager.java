package com.game.core;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.game.ConstVals.RenderingGround;

public interface IRenderingManager {

    /**
     * Gets uiViewport.
     *
     * @param renderingGround the rendering ground
     * @return the uiViewport
     */
    Viewport getViewport(RenderingGround renderingGround);

    /**
     * Get sprite batch.
     *
     * @return the sprite batch
     */
    SpriteBatch getSpriteBatch();

    /**
     * Get shape renderer.
     *
     * @return the shape renderer
     */
    ShapeRenderer getShapeRenderer();

}
