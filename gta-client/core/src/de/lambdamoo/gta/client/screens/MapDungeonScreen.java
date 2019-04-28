package de.lambdamoo.gta.client.screens;

import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.viewport.FitViewport;

import de.lambdamoo.gta.client.Core;
import de.lambdamoo.gta.client.screens.manager.AbstractScreen;
import de.lambdamoo.gta.client.screens.manager.ScreenResolution;

public class MapDungeonScreen extends AbstractScreen {
    private Core core = null;

    public MapDungeonScreen(Core core) {
        this.core = core;
        stage = new Stage(new FitViewport(ScreenResolution.getResolution().getWidth(), ScreenResolution.getResolution().getHeight()));
    }

    @Override
    public void show() {
    }

    @Override
    public void render(float deltaTime) {
        this.core.getGameWorld().process(deltaTime);
    }

    @Override
    public void resize(int width, int height) {
        this.core.getGameWorld().resize(width, height);
    }

}
