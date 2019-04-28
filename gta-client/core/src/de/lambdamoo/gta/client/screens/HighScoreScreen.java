package de.lambdamoo.gta.client.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Scaling;
import com.badlogic.gdx.utils.viewport.FitViewport;

import java.text.NumberFormat;
import java.util.List;

import de.lambdamoo.gta.client.Core;
import de.lambdamoo.gta.client.screens.manager.AbstractScreen;
import de.lambdamoo.gta.client.screens.manager.ScreenResolution;
import de.lambdamoo.gta.client.util.HighScoreEntry;
import de.lambdamoo.gta.client.util.MyLogger;
import de.lambdamoo.gta.client.util.PlayServiceListener;
import de.lambdamoo.gta.client.util.SpriteImages;

public class HighScoreScreen extends AbstractScreen {

    private Core core = null;
    private Table scores = null;
    private Button btnBack = null;
    private NumberFormat numberFormater = NumberFormat.getIntegerInstance();
    PlayServiceListener listenerLeaderboard = new PlayServiceListener() {
        @Override
        public void onComplete() {
            List<HighScoreEntry> list = core.getPlayServices().getListScore();
            Label.LabelStyle labelStyle = core.getSpriteImages().getLabelStyleDefault();
            scores.clearChildren();
            if (list != null) {
                for (int i = 0; i < list.size(); i++) {
                    HighScoreEntry entry = list.get(i);
                    MyLogger.getInstance().log("HighScoreScreen", i + ". " + entry);
                    if (entry != null) {
                        String name = entry.getName();
                        long score = entry.getScore();
                        if (name != null && score >= 0) {
                            scores.add(new Label((i + 1) + ". " + name, labelStyle)).width(500);
                            scores.add(new Label(numberFormater.format(score), labelStyle)).width(200);
                            scores.row();
                        }
                    }
                }
            }
        }
    };

    public HighScoreScreen(Core core) {
        this.core = core;
    }

    @Override
    public void show() {
        initStage();
        buildStage();
    }

    private void buildStage() {
        stage = new Stage(new FitViewport(ScreenResolution.getResolution().getWidth(), ScreenResolution.getResolution().getHeight()));

        btnBack = new TextButton("Back", core.getSpriteImages().getTextButtonStyle());
        btnBack.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                core.showScreenMenu();
            }
        });

        Table root = new Table();
        root.setFillParent(true);
        root.align(Align.center);
        Image img = new Image(core.getSpriteImages().getBackground(SpriteImages.MapBackground.TitleText));
        img.setScaling(Scaling.fill);
        root.add(img).padBottom(50).minHeight(img.getHeight());
        root.row();

        scores = new Table();
        ScrollPane scroll = new ScrollPane(scores);
        root.add(scroll).expandX().fillX();
        root.row();
        root.add(btnBack).width(500).height(80).padTop(50);
        root.row();
        stage.addActor(root);
    }

    private void initStage() {
    }

    public void reloadHighScores() {
        core.getPlayServices().loadLeaderboard(listenerLeaderboard);
    }

    @Override
    public void render(float deltaTime) {
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);
        if (stage != null) {
            stage.act(deltaTime);
            stage.draw();
        }
    }

    @Override
    public void resize(int width, int height) {
        MyLogger.getInstance().log("HighScoreScreen.resize()", "width=" + width + ", heightX=" + height);
        stage.getViewport().update(width, height, true);
    }
}
