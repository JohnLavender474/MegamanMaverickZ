package com.game.controllers;

import com.badlogic.gdx.Input.Keys;

import static com.game.controllers.ControllerUtils.*;

/**
 * The accepted buttons on a controller.
 * <p>
 * The default equivalent keyboard bindings:
 * {@link #START}: Space bar
 * {@link #UP}: W
 * {@link #DOWN}: S
 * {@link #LEFT}: A
 * {@link #RIGHT}: D
 * {@link #A}: K
 * {@link #X}: J
 */
@SuppressWarnings("ConstantConditions")
public enum ControllerButton {

    START {
        @Override
        public Integer getKeyboardBindingCode() {
            return Keys.SPACE;
        }

        @Override
        public Integer getControllerBindingCode() {
            return isControllerConnected() ? getController().getMapping().buttonStart : null;
        }
    },

    UP {
        @Override
        public Integer getKeyboardBindingCode() {
            return Keys.UP;
        }

        @Override
        public Integer getControllerBindingCode() {
            return isControllerConnected() ? getController().getMapping().buttonDpadUp : null;
        }
    },

    DOWN {
        @Override
        public Integer getKeyboardBindingCode() {
            return Keys.DOWN;
        }

        @Override
        public Integer getControllerBindingCode() {
            return isControllerConnected() ? getController().getMapping().buttonDpadDown : null;
        }
    },

    LEFT {
        @Override
        public Integer getKeyboardBindingCode() {
            return Keys.LEFT;
        }

        @Override
        public Integer getControllerBindingCode() {
            return isControllerConnected() ? getController().getMapping().buttonDpadLeft : null;
        }
    },

    RIGHT {
        @Override
        public Integer getKeyboardBindingCode() {
            return Keys.RIGHT;
        }

        @Override
        public Integer getControllerBindingCode() {
            return isControllerConnected() ? getController().getMapping().buttonDpadRight : null;
        }
    },

    A {
        @Override
        public Integer getKeyboardBindingCode() {
            return Keys.W;
        }

        @Override
        public Integer getControllerBindingCode() {
            return isControllerConnected() ? getController().getMapping().buttonA : null;
        }
    },

    X {
        @Override
        public Integer getKeyboardBindingCode() {
            return Keys.D;
        }

        @Override
        public Integer getControllerBindingCode() {
            return isControllerConnected() ? getController().getMapping().buttonX : null;
        }
    };

    /**
     * Returns the default keyboard binding code for the controller button value.
     *
     * @return the default keyboard binding code
     */
    public abstract Integer getKeyboardBindingCode();

    /**
     * Returns the default controller binding code for the controller button value. Returns null if no
     * controller is connected.
     *
     * @return the default controller binding code
     */
    public abstract Integer getControllerBindingCode();

}
