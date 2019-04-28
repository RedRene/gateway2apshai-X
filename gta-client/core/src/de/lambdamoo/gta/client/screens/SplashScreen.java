package de.lambdamoo.gta.client.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import de.lambdamoo.gta.client.Core;
import de.lambdamoo.gta.client.screens.manager.AbstractScreen;
import de.lambdamoo.gta.client.util.SpriteImages;

public class SplashScreen extends AbstractScreen {
    private SpriteBatch batch;
    private Texture ttrSplash;
    private Core core = null;
    private SpriteImages spriteImages = null;

    public SplashScreen(Core core) {
        super();
        this.core = core;
        batch = new SpriteBatch();
        ttrSplash = new Texture("images/background/logo.png");
        spriteImages = new SpriteImages();
    }

    @Override
    public void show() {
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        batch.begin();

        batch.draw(ttrSplash, (Gdx.graphics.getWidth() - ttrSplash.getWidth()) / 2, (Gdx.graphics.getHeight() - ttrSplash.getHeight()) / 2);
        batch.end();

        if (spriteImages.isInitialized()) {
            this.spriteImages.init();
            core.initCore(this.spriteImages);
            core.showScreenMenu();
        } else {
        }
    }

    @Override
    public void resize(int width, int height) {
    }

    @Override
    public void dispose() {
        ttrSplash.dispose();
        batch.dispose();
    }

}
