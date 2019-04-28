package de.lambdamoo.gta.client.screens.manager;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.actions.SequenceAction;

import de.lambdamoo.gta.client.screens.MapDungeonScreen;

public class ScreenManager {
    private AbstractScreen currentScreen;
    private AbstractScreen nextScreen;
    private FrameBuffer currFbo;
    private FrameBuffer nextFbo;
    private SpriteBatch batch;
    private boolean init;
    private float transitionRunTime;
    private ScreenTransition screenTransition;

    private void createScreenInAction(MapDungeonScreen screen) {
        Actor actor = screen.getStage().getRoot();
        actor.setOrigin(ScreenResolution.getResolution().getWidth() / 2, ScreenResolution.getResolution().getHeight() / 2);
        actor.getColor().a = 0;
        SequenceAction sequenceAction = new SequenceAction();
        sequenceAction.addAction(Actions.scaleTo(1.5f, 1.5f, 0));
        sequenceAction.addAction(Actions.parallel(Actions.alpha(1, 1), Actions.scaleTo(1.0f, 1.0f, 1, Interpolation.exp5)));
        actor.addAction(sequenceAction);

    }

    public void resize(int width, int height) {
        if (currentScreen != null) {
            currentScreen.resize(width, height);
        }
    }

    /**
     * This method sets the screen
     *
     * @param screen screen to set
     */
    public void setScreen(AbstractScreen screen) {
        Gdx.input.setInputProcessor(null);
        if (this.currentScreen != null) {
            this.currentScreen.hide();
            this.currentScreen.dispose();
        }
        this.currentScreen = screen;
        if (this.currentScreen != null) {
            this.currentScreen.show();
            this.currentScreen.resize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        }
        Gdx.input.setInputProcessor(currentScreen.getInputProcessor());
    }

    /**
     * Delegates the render call to the current screen
     */
    public void render() {
        // if (currentScreen != null) {
        //   Gdx.gl.glClearColor(0, 0, 0, 1);
        // Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT |
        //       GL20.GL_DEPTH_BUFFER_BIT);
        //currentScreen.render(Gdx.graphics.getDeltaTime());
        //}

        // get delta time and ensure an upper limit of one 60th second
        float deltaTime = Gdx.graphics.getDeltaTime();
        if (nextScreen == null) {
            // no ongoing transition
            if (currentScreen != null) {
                currentScreen.render(deltaTime);
            }
        } else {
            // ongoing transition
            float duration = 0;
            if (screenTransition != null) {
                duration = screenTransition.getDuration();
            }
            // update progress of ongoing transition
            transitionRunTime = Math.min(transitionRunTime + deltaTime, duration);
            if (screenTransition == null || transitionRunTime >= duration) {
                // transition has just finished
                if (currentScreen != null) {
                    currentScreen.hide();
                }
                nextScreen.resume();
                // enable input for next screen
                // switch screens
                currentScreen = nextScreen;
                nextScreen = null;
                screenTransition = null;
                currentScreen.resize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
                Gdx.input.setInputProcessor(currentScreen.getInputProcessor());
            } else {
                // render screens to FBOs
                currFbo.begin();
                if (currentScreen != null) {
                    currentScreen.resize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
                    currentScreen.render(deltaTime);
                }
                currFbo.end();
                nextFbo.begin();
                nextScreen.resize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
                nextScreen.render(deltaTime);
                nextFbo.end();
                // render transition effect to screen
                float alpha = transitionRunTime / duration;
                screenTransition.render(batch, currFbo.getColorBufferTexture(), nextFbo.getColorBufferTexture(), alpha);
            }
        }
    }

    public void setScreen(AbstractScreen screen, ScreenTransition screenTransition) {
        int w = Gdx.graphics.getWidth();
        int h = Gdx.graphics.getHeight();
        if (!init) {
            currFbo = new FrameBuffer(Pixmap.Format.RGB888, w, h, false);
            nextFbo = new FrameBuffer(Pixmap.Format.RGB888, w, h, false);
            batch = new SpriteBatch();
            init = true;
        }
        // start new transition
        nextScreen = screen;
        nextScreen.show(); // activate next screen
        nextScreen.resize(w, h);
        nextScreen.render(0); // let screen update() once
        if (currentScreen != null) {
            currentScreen.pause();
        }
        nextScreen.pause();
        Gdx.input.setInputProcessor(null); // disable input
        this.screenTransition = screenTransition;
        this.transitionRunTime = 0;
    }

}
