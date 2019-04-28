package de.lambdamoo.gta.client.screens.manager;

import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.scenes.scene2d.Stage;

public abstract class AbstractScreen implements Screen {
    protected Stage stage;

    public Stage getStage() {
        return stage;
    }

    public InputProcessor getInputProcessor() {
        return stage;
    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {
    }

    @Override
    public void dispose() {
    }
}
