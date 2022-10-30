package com.game.controllers;

import com.badlogic.gdx.Input;

import java.util.EnumMap;
import java.util.Map;
import java.util.function.Supplier;

import static com.badlogic.gdx.Input.*;
import static com.game.controllers.ControllerButton.*;
import static com.game.controllers.ControllerUtils.getController;

public class ControllerActuator {

    private static final Map<ControllerButton, Supplier<Integer>> defaultControllerCodes = Map.of(
            START, () -> getController().getMapping().buttonStart,
            DPAD_UP, () -> getController().getMapping().buttonDpadUp,
            DPAD_DOWN, () -> getController().getMapping().buttonDpadLeft,
            DPAD_RIGHT, () -> getController().getMapping().buttonDpadRight,
            A, () -> getController().getMapping().buttonA,
            X, () -> getController().getMapping().buttonX
    );

    private static final Map<ControllerButton, KeyboardEntry> defaultKeyboardCodes = Map.of(
            START, new KeyboardEntry(Keys.X, "X"),
            DPAD_UP, new KeyboardEntry(Keys.UP, "Up"),
            DPAD_DOWN, new KeyboardEntry(Keys.DOWN, "Down"),
            DPAD_LEFT, new KeyboardEntry(Keys.LEFT, "Left"),
            DPAD_RIGHT, new KeyboardEntry(Keys.RIGHT, "Right"),
            A, new KeyboardEntry(Keys.W, "W"),
            X, new KeyboardEntry(Keys.D, "D")
    );

    private final Map<ControllerButton, Supplier<Integer>> controllerCodes = new EnumMap<>(ControllerButton.class);
    private final Map<ControllerButton, KeyboardEntry> keyboardCodes = new EnumMap<>(ControllerButton.class);

    public ControllerActuator() {
        controllerCodes.putAll(defaultControllerCodes);
        keyboardCodes.putAll(defaultKeyboardCodes);
    }

    public void setControllerCodesToDefault() {
        controllerCodes.putAll(defaultControllerCodes);
    }

    public void setControllerCodeToDefault(ControllerButton button) {
        controllerCodes.put(button, defaultControllerCodes.get(button));
    }

    public void setControllerCode(ControllerButton button, int code) {
        controllerCodes.put(button, () -> code);
    }

    public int getControllerCode(ControllerButton button) {
        return controllerCodes.get(button).get();
    }

    public void setKeyboardCodesToDefault() {
        keyboardCodes.putAll(defaultKeyboardCodes);
    }

    public void setKeyboardCodeToDefault(ControllerButton button) {
        keyboardCodes.put(button, defaultKeyboardCodes.get(button));
    }

    public void setKeyboardCode(ControllerButton button, int key) {
        String name = Input.Keys.toString(key);
        if (name == null) {
            throw new IllegalStateException();
        }
        keyboardCodes.put(button, new KeyboardEntry(key, name));
    }

    public KeyboardEntry getKeyboardEntry(ControllerButton button) {
        return keyboardCodes.get(button);
    }

}
