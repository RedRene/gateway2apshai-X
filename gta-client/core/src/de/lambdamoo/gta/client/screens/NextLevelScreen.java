package de.lambdamoo.gta.client.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.FitViewport;

import java.util.Iterator;
import java.util.List;

import de.lambdamoo.gta.client.Core;
import de.lambdamoo.gta.client.screens.manager.AbstractScreen;
import de.lambdamoo.gta.client.screens.manager.ScreenResolution;
import de.lambdamoo.gta.client.screens.util.FormatUtil;
import de.lambdamoo.gta.client.util.MyLogger;
import de.lambdamoo.gta.client.util.SpriteImages;

public class NextLevelScreen extends AbstractScreen {

    public ScreenListener continueListener = null;
    private Core core = null;
    private List<String> messages = null;

    public NextLevelScreen(Core core) {
        this.core = core;
    }

    public void setContinueListener(ScreenListener continueListener) {
        this.continueListener = continueListener;
    }

    public void addMessages(List<String> messages) {
        this.messages = messages;
    }

    @Override
    public void show() {
        initStage();
        buildStage();
    }

    private void buildStage() {
        stage = new Stage(new FitViewport(ScreenResolution.getResolution().getWidth(), ScreenResolution.getResolution().getHeight()));

        Button btnContinue = new TextButton("Continue", core.getSpriteImages().getTextButtonStyle());
        btnContinue.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (continueListener != null) {
                    continueListener.returnFromScreen();
                }
            }
        });

        Label.LabelStyle labelStyle = core.getSpriteImages().getLabelStyleDefault();
        String text1 = "";
        if (messages != null && messages.size() > 0) {
            if (messages.size() == 3) {
                text1 = "You have earned full bonus!";
            } else {
                text1 = "You haven't found all treasures, but still earned a bonus.";
            }
        } else {
            text1 = "Unfortunately you have not earned a bonus!";
        }
        Table messageTable = new Table();
        messageTable.setBackground(core.getSpriteImages().getNinePatchOpaqueBackgroundStrong());
        messageTable.align(Align.center);
        messageTable.add(new Label(text1, core.getSpriteImages().getLabelStyleDefault())).padTop(50);
        messageTable.row();

        int topPadding = 250;

        Iterator<String> iter = messages.iterator();
        while (iter.hasNext()) {
            messageTable.add(new Label(iter.next(), labelStyle));
            messageTable.row();
            topPadding -= 50;

        }
        FormatUtil.addButtonTable(messageTable, 2, topPadding, btnContinue);

        Table root = new Table();
        root.setFillParent(true);
        root.add(messageTable).align(Align.center);
        root.row();

        // background picture
        Table background = new Table();
        background.setFillParent(true);
        background.background(new TextureRegionDrawable(new TextureRegion(core.getSpriteImages().getBackground(SpriteImages.MapBackground.Dungeon))));
        stage.addActor(background);

        stage.addActor(root);
    }

    private void initStage() {
    }

    @Override
    public void render(float deltaTime) {
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);
        stage.act(deltaTime);
        stage.draw();
    }

    @Override
    public void resize(int width, int height) {
        MyLogger.getInstance().log("NextLevelScreen.resize()", "width=" + width + ", heightX=" + height);
        stage.getViewport().update(width, height, true);
    }

}
