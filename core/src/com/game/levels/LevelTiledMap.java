package com.game.levels;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.maps.tiled.tiles.AnimatedTiledMapTile;
import com.badlogic.gdx.utils.Disposable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Manager class for {@link TiledMap} and {@link OrthogonalTiledMapRenderer}.
 */
public class LevelTiledMap implements Disposable {

    private final Map<String, List<RectangleMapObject>> objects = new HashMap<>();
    private final CustomOrthoTiledMapRenderer tiledMapRenderer;
    private final TiledMap tiledMap;

    /**
     * Constructs a new renderable tiled map using the seed file
     *
     * @param tmxFile the seed file
     */
    public LevelTiledMap(String tmxFile) {
        tiledMap = new TmxMapLoader().load(tmxFile);
        tiledMapRenderer = new CustomOrthoTiledMapRenderer(tiledMap);
        tiledMap.getLayers().forEach(mapLayer -> {
            List<RectangleMapObject> mapLayerObjects = new ArrayList<>();
            for (MapObject mapObject : mapLayer.getObjects()) {
                if (mapObject instanceof RectangleMapObject) {
                    mapLayerObjects.add((RectangleMapObject) mapObject);
                }
            }
            objects.put(mapLayer.getName(), mapLayerObjects);
        });
    }

    /**
     * Get the width of the tiled map in terms of number of tiles.
     *
     * @return the number of tiles comprising the width of the tiled map
     */
    public int getWidthInTiles() {
        return tiledMap.getProperties().get("width", Integer.class);
    }

    /**
     * Get the height of the tiled map in terms of number of tiles.
     *
     * @return the number of tiles comprising the height of the tiled map
     */
    public int getHeightInTiles() {
        return tiledMap.getProperties().get("height", Integer.class);
    }

    /**
     * Gets objects of layer.
     *
     * @param layer the layer
     * @return the objects of layer
     */
    public List<RectangleMapObject> getObjectsOfLayer(String layer) {
        return objects.get(layer);
    }

    /**
     * Draws the tile layers of the tiled map using the provided camera and sprite batch.
     *
     * @param camera the camera
     * @param spriteBatch the sprite batch
     */
    public void draw(OrthographicCamera camera, SpriteBatch spriteBatch) {
        tiledMapRenderer.render(camera, spriteBatch);
    }

    @Override
    public void dispose() {
        tiledMap.dispose();
        tiledMapRenderer.dispose();
    }

    /**
     * Custom implementation of {@link OrthogonalTiledMapRenderer} that allows sprite batch and camera to be reset
     * during runtime by calling {@link #render(OrthographicCamera, Batch)} instead of {@link #render()}.
     */
    private static final class CustomOrthoTiledMapRenderer extends OrthogonalTiledMapRenderer {

        private CustomOrthoTiledMapRenderer(TiledMap map) {
            super(map);
        }

        private boolean isDrawing;

        /**
         * Render using the camera and batch supplied during runtime.
         *
         * @param camera the camera
         * @param batch the batch
         */
        public void render(OrthographicCamera camera, Batch batch) {
            this.batch = batch;
            setView(camera);
            super.render();
        }

        @Override
        protected void beginRender() {
            AnimatedTiledMapTile.updateAnimationBaseTime();
            isDrawing = batch.isDrawing();
            if (!isDrawing) {
                batch.begin();
            }
        }

        @Override
        protected void endRender() {
            if (!isDrawing) {
                batch.end();
            }
        }

    }

}
