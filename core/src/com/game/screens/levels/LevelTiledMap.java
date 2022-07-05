package com.game.screens.levels;

import com.badlogic.gdx.graphics.OrthographicCamera;
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
    private final OrthogonalTiledMapRenderer tiledMapRenderer;
    private final OrthographicCamera camera;
    private final SpriteBatch spriteBatch;
    private final TiledMap tiledMap;

    /**
     * Instantiates a new Level tiled map.
     *
     * @param camera  the camera
     * @param tmxFile the tmx file
     */
    public LevelTiledMap(OrthographicCamera camera, SpriteBatch spriteBatch, String tmxFile) {
        this.camera = camera;
        this.spriteBatch = spriteBatch;
        tiledMap = new TmxMapLoader().load(tmxFile);
        tiledMapRenderer = new OrthogonalTiledMapRenderer(tiledMap, spriteBatch) {

            private boolean isDrawing;

            @Override
            protected void beginRender() {
                AnimatedTiledMapTile.updateAnimationBaseTime();
                setView(camera);
                isDrawing = spriteBatch.isDrawing();
                if (!isDrawing) {
                    spriteBatch.begin();
                }
            }

            @Override
            protected void endRender() {
                if (!isDrawing) {
                    spriteBatch.end();
                }
            }
        };
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
     * Gets objects of layer.
     *
     * @param layer the layer
     * @return the objects of layer
     */
    public List<RectangleMapObject> getObjectsOfLayer(String layer) {
        return objects.get(layer);
    }

    public void draw() {
        tiledMapRenderer.render();
    }

    @Override
    public void dispose() {
        tiledMap.dispose();
        tiledMapRenderer.dispose();
    }

}
