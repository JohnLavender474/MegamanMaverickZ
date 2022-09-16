package com.game.levels;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.objects.CircleMapObject;
import com.badlogic.gdx.maps.objects.PolylineMapObject;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.maps.tiled.tiles.AnimatedTiledMapTile;
import com.badlogic.gdx.math.Polygon;
import com.badlogic.gdx.math.Polyline;
import com.badlogic.gdx.math.Shape2D;
import com.badlogic.gdx.utils.Disposable;
import com.game.shapes.custom.Triangle;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Manager class for {@link TiledMap} and {@link OrthogonalTiledMapRenderer}.
 */
public class LevelTiledMap implements Disposable {

    private final OrthographicCamera camera;
    private final SpriteBatch spriteBatch;

    private final Map<String, List<RectangleMapObject>> mapRectObjs = new HashMap<>();
    private final Map<String, List<CircleMapObject>> mapCircleObjs = new HashMap<>();
    private final Map<String, List<PolylineMapObject>> mapLineObjs = new HashMap<>();
    private final CustomOrthoTiledMapRenderer tiledMapRenderer;
    private final TiledMap tiledMap;

    /**
     * Constructs a new renderable tiled map using the seed file
     *
     * @param tmxFile the seed file
     */
    public LevelTiledMap(OrthographicCamera camera, SpriteBatch spriteBatch, String tmxFile) {
        this.camera = camera;
        this.spriteBatch = spriteBatch;
        tiledMap = new TmxMapLoader().load(tmxFile);
        tiledMapRenderer = new CustomOrthoTiledMapRenderer(tiledMap);
        tiledMap.getLayers().forEach(mapLayer -> {
            mapRectObjs.put(mapLayer.getName(), new ArrayList<>());
            mapLineObjs.put(mapLayer.getName(), new ArrayList<>());
            mapCircleObjs.put(mapLayer.getName(), new ArrayList<>());
            for (MapObject mapObj : mapLayer.getObjects()) {
                if (mapObj instanceof RectangleMapObject rectMapObj) {
                    mapRectObjs.get(mapLayer.getName()).add(rectMapObj);
                } else if (mapObj instanceof CircleMapObject circleMapObj) {
                    mapCircleObjs.get(mapLayer.getName()).add(circleMapObj);
                } else if (mapObj instanceof PolylineMapObject lineMapObj) {
                    mapLineObjs.get(mapLayer.getName()).add(lineMapObj);
                }
            }
        });
    }

    /**
     * Returns the shape of the map object. Can be rectangle, circle, polyline, triangle, or polygon.
     * Returns polyline if there are four vertices, triangle if there are six, and polygon if there are any
     * other number of vertices.
     *
     * @param mapObj the map object
     * @return the shape of the map object (rectangle, circle, polyline, triangle, or polygon)
     */
    public static Shape2D getMapObjShape(MapObject mapObj) {
        if (mapObj instanceof RectangleMapObject rectMapObj) {
            return rectMapObj.getRectangle();
        } else if (mapObj instanceof CircleMapObject circleMapObj) {
            return circleMapObj.getCircle();
        } else if (mapObj instanceof PolylineMapObject polyMapObj) {
            Polyline polyline = polyMapObj.getPolyline();
            if (polyline.getVertices().length == 4) {
                return polyline;
            } else if (polyline.getVertices().length == 6) {
                return new Triangle(polyline.getVertices());
            } else {
                return new Polygon(polyline.getVertices());
            }
        }
        return null;
    }

    /**
     * Get the width pairOf the tiled map in terms pairOf number pairOf tiles.
     *
     * @return the number pairOf tiles comprising the width pairOf the tiled map
     */
    public int getWidthInTiles() {
        return tiledMap.getProperties().get("width", Integer.class);
    }

    /**
     * Get the height pairOf the tiled map in terms pairOf number pairOf tiles.
     *
     * @return the number pairOf tiles comprising the height pairOf the tiled map
     */
    public int getHeightInTiles() {
        return tiledMap.getProperties().get("height", Integer.class);
    }

    /**
     * Get rectangle objects pairOf the layer.
     *
     * @param layer the layer
     * @return the rectangle objects pairOf the layer
     */
    public List<RectangleMapObject> getRectObjsOfLayer(String layer) {
        return mapRectObjs.get(layer);
    }

    /**
     * Get circle objects pairOf the layer.
     *
     * @param layer the layer
     * @return the circle objects pairOf the layer
     */
    public List<CircleMapObject> getCircleObjsOfLayer(String layer) {
        return mapCircleObjs.get(layer);
    }

    /**
     * Get line objects pairOf the layer.
     *
     * @param layer the layer
     * @return the line objects pairOf the layer
     */
    public List<PolylineMapObject> getLineObjsOfLayer(String layer) {
        return mapLineObjs.get(layer);
    }

    /**
     * Draws the tile layers pairOf the tiled map using the provided camera and sprite batch.
     */
    public void draw() {
        tiledMapRenderer.render(camera, spriteBatch);
    }

    @Override
    public void dispose() {
        tiledMap.dispose();
        tiledMapRenderer.dispose();
    }

    /**
     * Custom implementation pairOf {@link OrthogonalTiledMapRenderer} that allows sprite batch and camera to be reset
     * during runtime by calling {@link #render(OrthographicCamera, Batch)} instead pairOf {@link #render()}.
     */
    private static final class CustomOrthoTiledMapRenderer extends OrthogonalTiledMapRenderer {

        private CustomOrthoTiledMapRenderer(TiledMap map) {
            super(map);
        }

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
        }

        @Override
        protected void endRender() {}

        @Override
        public void dispose() {}

    }

}
