package com.game.screens.other;

import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.game.core.GameContext2d;
import com.game.core.constants.RenderingGround;
import com.game.dialogue.Dialogue;

import java.util.Collection;
import java.util.function.Supplier;

public class DialogueOverlayScreen extends ScreenAdapter {

    private final GameContext2d gameContext;

    private Dialogue dialogue;

    public DialogueOverlayScreen(GameContext2d gameContext) {
        this.gameContext = gameContext;
    }

    public void set(Collection<String> lines, Supplier<Boolean> finisher, Supplier<Boolean> speedUp) {
        this.dialogue = new Dialogue(gameContext, lines, finisher, speedUp);
    }

    public boolean isFinished() {
        return dialogue.isFinished();
    }

    @Override
    public void show() {
        if (dialogue == null) {
            throw new IllegalStateException("Dialogue has not yet been setVertices");
        }
    }

    @Override
    public void render(float delta) {
        dialogue.update(delta);
        SpriteBatch spriteBatch = gameContext.getSpriteBatch();
        gameContext.setSpriteBatchProjectionMatrix(RenderingGround.UI);
        boolean drawing = spriteBatch.isDrawing();
        if (!drawing) {
            spriteBatch.begin();
        }
        dialogue.draw(spriteBatch);
        spriteBatch.end();
    }

    @Override
    public void dispose() {
        dialogue = null;
    }

}
