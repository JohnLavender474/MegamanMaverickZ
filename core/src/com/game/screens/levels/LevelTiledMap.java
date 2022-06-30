package com.game.screens.levels;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.maps.tiled.tiles.AnimatedTiledMapTile;
import com.badlogic.gdx.utils.Disposable;
import com.game.utils.Drawable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Manager class for {@link TiledMap} and {@link OrthogonalTiledMapRenderer}.
 */
public class LevelTiledMap implements Disposable, Drawable {

    private final Map<String, List<RectangleMapObject>> objects = new HashMap<>();
    private final OrthogonalTiledMapRenderer tiledMapRenderer;
    private final OrthographicCamera camera;
    private final TiledMap tiledMap;

    /**
     * Instantiates a new Level tiled map.
     *
     * @param camera  the camera
     * @param tmxFile the tmx file
     */
    public LevelTiledMap(OrthographicCamera camera, String tmxFile) {
        this.camera = camera;
        tiledMap = new TmxMapLoader().load(tmxFile);
        tiledMapRenderer = new OrthogonalTiledMapRenderer(tiledMap) {
            @Override
            protected void beginRender() {
                AnimatedTiledMapTile.updateAnimationBaseTime();
            }

            @Override
            protected void endRender() {
            }
        };
        for (MapLayer mapLayer : tiledMap.getLayers()) {
            List<RectangleMapObject> mapLayerObjects = new ArrayList<>();
            for (MapObject mapObject : mapLayer.getObjects()) {
                if (mapObject instanceof RectangleMapObject) {
                    mapLayerObjects.add((RectangleMapObject) mapObject);
                }
            }
            objects.put(mapLayer.getName(), mapLayerObjects);
        }
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

    @Override
    public void draw(SpriteBatch spriteBatch) {
        tiledMapRenderer.setView(camera);
        boolean isDrawing = spriteBatch.isDrawing();
        if (!isDrawing) {
            spriteBatch.begin();
        }
        tiledMapRenderer.render();
        if (!isDrawing) {
            spriteBatch.end();
        }
    }

    @Override
    public void dispose() {
        tiledMap.dispose();
        tiledMapRenderer.dispose();
    }

}
