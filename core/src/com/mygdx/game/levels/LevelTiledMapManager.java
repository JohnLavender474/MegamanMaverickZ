package com.mygdx.game.levels;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapRenderer;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.utils.Disposable;

import java.util.*;

/**
 * Manager class for {@link TiledMap} and {@link OrthogonalTiledMapRenderer}.
 */
public class LevelTiledMapManager implements Disposable {

    private TiledMap tiledMap;
    private OrthogonalTiledMapRenderer tiledMapRenderer;

    /**
     * Load new tile map and fetch all {@link RectangleMapObject} instances from map.
     *
     * @param tmxFile the tmx file
     * @return the map
     */
    public Map<String, List<RectangleMapObject>> loadTiledMap(String tmxFile) {
        dispose();
        tiledMap = new TmxMapLoader().load(tmxFile);
        tiledMapRenderer = new OrthogonalTiledMapRenderer(tiledMap);
        Map<String, List<RectangleMapObject>> mapObjects = new HashMap<>();
        for (MapLayer mapLayer : tiledMap.getLayers()) {
            List<RectangleMapObject> mapLayerObjects = new ArrayList<>();
            for (MapObject mapObject : mapLayer.getObjects()) {
                if (mapObject instanceof RectangleMapObject) {
                    mapLayerObjects.add((RectangleMapObject) mapObject);
                }
            }
            mapObjects.put(mapLayer.getName(), mapLayerObjects);
        }
        return mapObjects;
    }

    /**
     * If {@link #tiledMap} and {@link #tiledMapRenderer} are not null, then this method calls
     * {@link TiledMapRenderer#setView(OrthographicCamera)} and {@link TiledMapRenderer#render()}.
     *
     * @param camera the camera to set the tiled map renderer view to
     */
    public void render(OrthographicCamera camera) {
        if (tiledMap != null && tiledMapRenderer != null) {
            tiledMapRenderer.setView(camera);
            tiledMapRenderer.render();
        }
    }

    @Override
    public void dispose() {
        if (tiledMap != null) {
            tiledMap.dispose();
        }
        if (tiledMapRenderer != null) {
            tiledMapRenderer.dispose();
        }
    }

}
