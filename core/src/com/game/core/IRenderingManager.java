package com.game.core;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.game.core.ConstVals.RenderingGround;

public interface IRenderingManager {

    /**
     * Gets uiViewport.
     *
     * @param renderingGround the rendering ground
     * @return the uiViewport
     */
    Viewport getViewport(RenderingGround renderingGround);

    /**
     * Sets the sprite batch to the projection matrix of the specified viewport.
     *
     * @param renderingGround the rendering ground of the viewport to be used for the projection matrix
     */
    void setSpriteBatchProjectionMatrix(RenderingGround renderingGround);

    /**
     * Sets the shape renderer to the projection matrix of the specified viewport.
     *
     * @param renderingGround the rendering ground of the viewport to be used for the projection matrix
     */
    void setShapeRendererProjectionMatrix(RenderingGround renderingGround);

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
