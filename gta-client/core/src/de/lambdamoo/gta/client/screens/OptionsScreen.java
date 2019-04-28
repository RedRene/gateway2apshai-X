package de.lambdamoo.gta.client.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Slider;
import com.badlogic.gdx.scenes.scene2d.ui.Stack;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.FitViewport;

import java.util.Arrays;

import de.lambdamoo.gta.client.Core;
import de.lambdamoo.gta.client.screens.manager.AbstractScreen;
import de.lambdamoo.gta.client.screens.manager.ScreenResolution;
import de.lambdamoo.gta.client.screens.util.FormatUtil;
import de.lambdamoo.gta.client.util.MyLogger;
import de.lambdamoo.gta.client.util.SpriteImages;

import static de.lambdamoo.gta.client.util.SpriteImages.FontSize.Large;
import static de.lambdamoo.gta.client.util.SpriteImages.FontSize.Medium;
import static de.lambdamoo.gta.client.util.SpriteImages.FontSize.Small;

public class OptionsScreen extends AbstractScreen {

    final private SpriteImages.FontSize[] fontSizes = {Small, Medium, Large};
    private Core core = null;
    private Slider sliderFontSize = null;
    private Slider sliderZoom = null;
    private Slider sliderVolume = null;
    private Label labelFontSize = null;
    private Label labelVolume = null;
    private Label labelZoom = null;
    private String[] zoomNames = new String[]{
            "1x", "2x", "3x", "4x", "5x"
    };
    private float[] zoomValues = new float[]{
            2.0f, 1.0f, 0.75f, 0.5f, 0.25f};

    public OptionsScreen(Core core) {
        this.core = core;
    }

    @Override
    public void show() {
        initStage();
        buildStage();
    }

    private void buildStage() {
        stage = new Stage(new FitViewport(ScreenResolution.getResolution().getWidth(), ScreenResolution.getResolution().getHeight()));
        InputListener listener = new InputListener() {
            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                updateSliderTexts();
            }

            @Override
            public void touchDragged(InputEvent event, float x, float y, int pointer) {
                updateSliderTexts();
            }

            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                return true;
            }
        };

        // font size
        sliderFontSize = new Slider(0, 2, 1, false, core.getSpriteImages().getSliderStyle());
        Stack stackFontSize = new Stack();
        labelFontSize = createLabel("Font size:");
        stackFontSize.add(sliderFontSize);
        stackFontSize.add(labelFontSize);
        sliderFontSize.addListener(listener);

        // volume
        sliderVolume = new Slider(0.0f, 1.0f, 0.01f, false, core.getSpriteImages().getSliderStyle());
        Stack stackVolume = new Stack();
        labelVolume = createLabel("Volume:");
        stackVolume.add(sliderVolume);
        stackVolume.add(labelVolume);
        sliderVolume.addListener(listener);

        // zoom
        sliderZoom = new Slider(0, 4, 1, false, core.getSpriteImages().getSliderStyle());
        Stack stackZoom = new Stack();
        labelZoom = createLabel("Zoom:");
        stackZoom.add(sliderZoom);
        stackZoom.add(labelZoom);
        sliderZoom.addListener(listener);

        Button btnBack = new TextButton("Back", core.getSpriteImages().getTextButtonStyle());
        btnBack.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                int value = Math.round(sliderFontSize.getValue());
                SpriteImages.FontSize fontSize = fontSizes[value];
                float volume = sliderVolume.getValue();
                value = Math.round(sliderZoom.getValue());
                float zoomFactor = zoomValues[value];

                core.getSpriteImages().setFontSize(fontSize);
                core.getGameWorld().getSoundManager().setSoundVolume(volume);
                core.getGameWorld().getRenderSystem().setZoom(zoomFactor);
                core.savePreferences();
                core.showScreenMenu();
            }
        });


        Table root = new Table();
        root.setFillParent(true);
        root.pad(10);
        root.columnDefaults(0).align(Align.right).padLeft(30);
        root.columnDefaults(1).width(500).align(Align.left).padLeft(50);
        root.defaults().padTop(20);
        root.align(Align.center);
        Image img = new Image(core.getSpriteImages().getBackground(SpriteImages.MapBackground.TitleText));
        root.add(img).colspan(2).padBottom(50).minHeight(img.getHeight()).align(Align.center);
        root.row();
        root.add(new Label("Font size:", core.getSpriteImages().getLabelStyleDefault()));
        root.add(stackFontSize);
        root.row();
        root.add(new Label("Volume:", core.getSpriteImages().getLabelStyleDefault()));
        root.add(stackVolume);
        root.row();
        root.add(new Label("Zoom factor:", core.getSpriteImages().getLabelStyleDefault()));
        root.add(stackZoom);
        root.row();
        FormatUtil.addButtonTable(root, 2, 300, btnBack);
        stage.addActor(root);
        initSliderValues();
        updateSliderTexts();
    }

    private void updateSliderTexts() {
        int value = Math.round(sliderFontSize.getValue());
        SpriteImages.FontSize fontSize = fontSizes[value];
        labelFontSize.setText(fontSize.toString());

        int volume = Math.round(sliderVolume.getValue() * 100);
        labelVolume.setText("" + volume + "%");

        value = Math.round(sliderZoom.getValue());
        labelZoom.setText("" + zoomNames[value]);

    }

    private Label createLabel(String text) {
        Label result = new Label(text, core.getSpriteImages().getLabelStyleDefault());
        result.setAlignment(Align.center);
        result.setTouchable(Touchable.disabled);
        return result;
    }

    private void initSliderValues() {
        SpriteImages.FontSize fontSize = core.getSpriteImages().getFontSize();
        int index = Arrays.binarySearch(fontSizes, fontSize);
        sliderFontSize.setValue(index);

        float volume = core.getGameWorld().getSoundManager().getSoundVolume();
        sliderVolume.setValue(volume);

        float zoomValue = core.getGameWorld().getRenderSystem().getZoom();
        index = 0;
        for (int i = 0; i < zoomValues.length; i++) {
            if (zoomValue <= zoomValues[i]) {
                index = i;
            }
        }
        sliderZoom.setValue(index);
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
        MyLogger.getInstance().log("OptionsScreen.resize()", "width=" + width + ", heightX=" + height);
        stage.getViewport().update(width, height, true);
    }


}
