package com.game;

import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.game.entities.Entity;
import com.game.messages.Message;
import com.game.levels.LevelStatus;
import com.game.sprites.RenderingGround;
import com.game.controllers.ControllerButton;
import com.game.messages.MessageListener;

import java.util.Collection;

import static java.util.Arrays.stream;

/**
 * Represents the essentials for a 2D game, including game state management, entity and systems management, controller
 * listening, asset loading, sprite batch, screen management, and blackboard.
 */
public interface GameContext2d {

    /**
     * Add system.
     *
     * @param system the system
     */
    void addSystem(System system);

    /**
     * Get {@link System}.
     *
     * @param <S>    the type parameter pairOf the system
     * @param sClass the system class
     * @return the system
     */
    <S extends System> S getSystem(Class<S> sClass);

    /**
     * Get systems.
     *
     * @return systems
     */
    Collection<System> getSystems();

    /**
     * Update systems.
     *
     * @param delta the delta
     */
    void updateSystems(float delta);

    /**
     * Add {@link Entity}. To remove the entity, {@link Entity#isDead()} ishould be setBounds to true.
     * The entity should be purged from all {@link System} instances on the following update cycle.
     *
     * @param entity the entity to be added
     */
    void addEntity(Entity entity);

    /**
     * Adds each entity. See {@link #addEntity(Entity)}.
     *
     * @param entities the entities to be added
     */
    default void addEntities(Collection<? extends Entity> entities) {
        entities.forEach(this::addEntity);
    }

    /**
     * View of entities collection.
     *
     * @return the collection
     */
    Collection<Entity> getEntities();

    /**
     * Should be called when leaving a level screen and all entities need to be disposed of.
     */
    void purgeAllEntities();

    /**
     * Removes the entity.
     *
     * @param entity the entity
     */
    void removeEntity(Entity entity);


    /**
     * Put blackboard object.
     *
     * @param key    the key
     * @param object the object
     */
    void putBlackboardObject(String key, Object object);

    /**
     * Get blackboard object.
     *
     * @param <T>    the type parameter pairOf the object
     * @param key    the key
     * @param tClass the class to cast the object to
     * @return the blackboard object
     */
    <T> T getBlackboardObject(String key, Class<T> tClass);

    /**
     * Set screen. Previous screen should be disposed pairOf.
     *
     * @param gameScreen the game screen
     */
    void setScreen(GameScreen gameScreen);

    /**
     * Gets the screen mapped to the game screen key
     *
     * @param gameScreen the game screen key
     * @return  the screen
     */
    Screen getScreen(GameScreen gameScreen);

    /**
     * Overlays a screen onto the current screen.
     *
     * @param gameScreen the game screen
     */
    void putOverlayScreen(GameScreen gameScreen);

    /**
     * Removes the overlay screen.
     */
    void popOverlayScreen();

    /**
     * Get level status.
     *
     * @return the level status
     */
    LevelStatus getLevelStatus();

    /**
     * If the level status is equal to the param.
     *
     * @param levelStatus the level status
     * @return if the level status is equal to the param
     */
    default boolean isLevelStatus(LevelStatus levelStatus) {
        return getLevelStatus() == levelStatus;
    }

    /**
     * Sets the level status
     *
     * @param levelStatus the level status
     */
    void setLevelStatus(LevelStatus levelStatus);

    /**
     * Gets uiViewport.
     *
     * @param renderingGround the rendering ground
     * @return the uiViewport
     */
    Viewport getViewport(RenderingGround renderingGround);

    /**
     * Sets the sprite batch to the projection matrix pairOf the specified viewport.
     *
     * @param renderingGround the rendering ground pairOf the viewport to be used for the projection matrix
     */
    void setSpriteBatchProjectionMatrix(RenderingGround renderingGround);

    /**
     * Sets the shape renderer to the projection matrix pairOf the specified viewport.
     *
     * @param renderingGround the rendering ground pairOf the viewport to be used for the projection matrix
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

    /**
     * Get asset such as music or sound effect object.
     *
     * @param <T>    the type parameter pairOf the object
     * @param key    the key
     * @param tClass the class to cast the object to
     * @return the asset
     */
    <T> T getAsset(String key, Class<T> tClass);

    /**
     * Add message listener.
     *
     * @param messageListener the message listener
     */
    void addMessageListener(MessageListener messageListener);

    /**
     * Remove message listener.
     *
     * @param messageListener the message listener
     */
    void removeMessageListener(MessageListener messageListener);

    /**
     * Add message
     *
     * @param message the message
     */
    void sendMessage(Message message);

    /**
     * Set sound effects volume.
     *
     * @param volume the volume
     */
    void setSoundEffectsVolume(int volume);

    /**
     * Get sound effects volume.
     *
     * @return the volume
     */
    int getSoundEffectsVolume();

    /**
     * Set music volume.
     *
     * @param volume the volumep
     */
    void setMusicVolume(int volume);

    /**
     * Get music volume.
     *
     * @return music volume
     */
    int getMusicVolume();

    /**
     * Plays the sound.
     *
     * @param sound the sound
     */
    default void playSound(Sound sound) {
        sound.play(getSoundEffectsVolume() / 10f);
    }

    /**
     * Loops the sound
     *
     * @param sound the sound
     */
    default void loopSound(Sound sound) {
        sound.loop(getSoundEffectsVolume() / 10f);
    }

    /**
     * Stops the sound.
     *
     * @param sound the sound
     */
    default void stopSound(Sound sound) {
        sound.stop();
    }

    /**
     * Plays the music.
     *
     * @param music the music
     * @param loop if loop
     */
    default void playMusic(Music music, boolean loop) {
        music.setLooping(loop);
        music.setVolume(getMusicVolume() / 10f);
        music.play();
    }

    /**
     * Stops the music.
     *
     * @param music the music to be stopped
     */
    default void stopMusic(Music music) {
        music.stop();
    }

    /**
     * If any controller button is pressed.
     *
     * @return if any controller button is pressed
     */
    default boolean isAnyControllerButtonPressed() {
        return stream(ControllerButton.values()).anyMatch(this::isControllerButtonPressed);
    }

    /**
     * If any controller button is just pressed.
     *
     * @return if any controller button is just pressed
     */
    default boolean isAnyControllerButtonJustPressed() {
        return stream(ControllerButton.values()).anyMatch(this::isControllerButtonJustPressed);
    }

    /**
     * If any controller button is just released.
     *
     * @return if any controller button is just released
     */
    default boolean isAnyControllerButtonJustReleased() {
        return stream(ControllerButton.values()).anyMatch(this::isControllerButtonJustReleased);
    }

    /**
     * If controller button is just pressed.
     *
     * @param controllerButton the controller button
     * @return if controller button is just pressed
     */
    boolean isControllerButtonJustPressed(ControllerButton controllerButton);

    /**
     * If controller button is pressed. Include if just pressed.
     *
     * @param controllerButton the controller button
     * @return if the controller button is pressed or just pressed
     */
    boolean isControllerButtonPressed(ControllerButton controllerButton);

    /**
     * If controller button is just released.
     *
     * @param controllerButton the controller button
     * @return if the controller button is just released
     */
    boolean isControllerButtonJustReleased(ControllerButton controllerButton);

    /** Update controller. */
    void updateController();

    /**
     * Returns if the controller should be updated.
     *
     * @return if the controller should be updated
     */
    boolean doUpdateController();

    /**
     * Set if the controller should be updated.
     *
     * @param doUpdateController if the controller should be updated
     */
    void setDoUpdateController(boolean doUpdateController);


}
