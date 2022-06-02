package com.mygdx.game.controllers;

import com.badlogic.gdx.Input.Keys;

import static com.mygdx.game.controllers.ControllerUtils.*;

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
        public Integer getDefaultKeyboardBindingCode() {
            return Keys.SPACE;
        }

        @Override
        public Integer getDefaultControllerBindingCode() {
            return isControllerConnected() ? getController().getMapping().buttonStart : null;
        }
    },

    UP {
        @Override
        public Integer getDefaultKeyboardBindingCode() {
            return Keys.W;
        }

        @Override
        public Integer getDefaultControllerBindingCode() {
            return isControllerConnected() ? getController().getMapping().buttonDpadUp : null;
        }
    },

    DOWN {
        @Override
        public Integer getDefaultKeyboardBindingCode() {
            return Keys.S;
        }

        @Override
        public Integer getDefaultControllerBindingCode() {
            return isControllerConnected() ? getController().getMapping().buttonDpadDown : null;
        }
    },

    LEFT {
        @Override
        public Integer getDefaultKeyboardBindingCode() {
            return Keys.A;
        }

        @Override
        public Integer getDefaultControllerBindingCode() {
            return isControllerConnected() ? getController().getMapping().buttonDpadLeft : null;
        }
    },

    RIGHT {
        @Override
        public Integer getDefaultKeyboardBindingCode() {
            return Keys.D;
        }

        @Override
        public Integer getDefaultControllerBindingCode() {
            return isControllerConnected() ? getController().getMapping().buttonDpadRight : null;
        }
    },

    A {
        @Override
        public Integer getDefaultKeyboardBindingCode() {
            return Keys.K;
        }

        @Override
        public Integer getDefaultControllerBindingCode() {
            return isControllerConnected() ? getController().getMapping().buttonA : null;
        }
    },

    X {
        @Override
        public Integer getDefaultKeyboardBindingCode() {
            return Keys.J;
        }

        @Override
        public Integer getDefaultControllerBindingCode() {
            return isControllerConnected() ? getController().getMapping().buttonX : null;
        }
    };

    /**
     * Returns the default keyboard binding code for the controller button value.
     *
     * @return the default keyboard binding code
     */
    public abstract Integer getDefaultKeyboardBindingCode();

    /**
     * Returns the default controller binding code for the controller button value. Returns null if no
     * controller is connected.
     *
     * @return the default controller binding code
     */
    public abstract Integer getDefaultControllerBindingCode();

}
