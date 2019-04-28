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
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.FitViewport;

import java.util.Random;

import de.lambdamoo.gta.client.Core;
import de.lambdamoo.gta.client.screens.manager.AbstractScreen;
import de.lambdamoo.gta.client.screens.manager.ScreenResolution;
import de.lambdamoo.gta.client.screens.util.FormatUtil;
import de.lambdamoo.gta.client.util.MyLogger;
import de.lambdamoo.gta.client.util.SpriteImages;

public class NewGameScreen extends AbstractScreen {

    private static final String[] names = {"Ashur", "Armand", "Asim", "Barbara", "Brenda", "Breand", "Corin", "Delma", "Edgar", "Eamon", "Egil",
            "Genesis", "Garland", "Garraway", "Gunter", "Ingvar", "Jason", "Kempley", "Kempton", "Kane", "Luther", "Maeve", "Milland", "Midgard", "Murrow", "Paige",
            "Perceval", "Pike", "Quiriana", "Patton", "Rodgar", "Saxon", "Sigmund", "Sigurd", "Sonja", "Tyr", "Thorgar", "Vilmos", "Xandra", "Zandra", "Xenia"};
    private Core core = null;
    private TextField textfieldName = null;
    private Slider sliderLevel = null;
    private Label labelSlider = null;

    public NewGameScreen(Core core) {
        this.core = core;
    }

    @Override
    public void show() {
        initStage();
        buildStage();
    }

    private void buildStage() {
        stage = new Stage(new FitViewport(ScreenResolution.getResolution().getWidth(), ScreenResolution.getResolution().getHeight()));

        Random rand = new Random();
        int ran = rand.nextInt(names.length);
        String name = names[ran];

        textfieldName = new TextField(name, core.getSpriteImages().getTextFieldStyle());
        textfieldName.setMaxLength(20);

        sliderLevel = new Slider(1, 16, 1, false, core.getSpriteImages().getSliderStyle());
        sliderLevel.getStyle().background.setMinHeight(50);
        Stack stackSlider = new Stack();
        stackSlider.add(sliderLevel);
        labelSlider = new Label("Dungeon:", core.getSpriteImages().getLabelStyleDefault());
        labelSlider.setAlignment(Align.center);
        labelSlider.setTouchable(Touchable.disabled);
        stackSlider.add(labelSlider);
        sliderLevel.addListener(new InputListener() {
            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                updateSliderValue();
            }

            @Override
            public void touchDragged(InputEvent event, float x, float y, int pointer) {
                updateSliderValue();
            }

            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                return true;
            }
        });

        Button btnStartGame = new TextButton("Start", core.getSpriteImages().getTextButtonStyle());
        btnStartGame.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                core.showScreenMapDungeon();
                core.getGameWorld().gameNew(textfieldName.getText(), Math.round(sliderLevel.getValue()), false);
            }
        });
        Button btnBack = new TextButton("Back", core.getSpriteImages().getTextButtonStyle());
        btnBack.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                core.showScreenMenu();
            }
        });

        Table root = new Table();
        root.setFillParent(true);
        root.pad(10);
        root.columnDefaults(0).align(Align.right).padLeft(30);
        root.columnDefaults(1).width(500).align(Align.left).padLeft(30);
        root.defaults().padTop(20);
        root.align(Align.center);
        Image img = new Image(core.getSpriteImages().getBackground(SpriteImages.MapBackground.TitleText));
        root.add(img).colspan(2).padBottom(50).minHeight(img.getHeight()).align(Align.center);
        root.row();
        root.add(new Label("Character name:", core.getSpriteImages().getLabelStyleDefault()));
        root.add(textfieldName);
        root.row();
        root.add(new Label("Select the Dungeon:", core.getSpriteImages().getLabelStyleDefault()));
        root.add(stackSlider);
        root.row();
        Table grp = FormatUtil.addButtonTable(root, 2, 300, btnBack, btnStartGame);

        // isDevelopment
        if (core.isDevelopment()) {
            Button btnStartTrainerGame = new TextButton("Trainer", core.getSpriteImages().getTextButtonStyle());
            btnStartTrainerGame.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    core.showScreenMapDungeon();
                    core.getGameWorld().gameNew(textfieldName.getText(), Math.round(sliderLevel.getValue()), true);
                }
            });
            grp.add(btnStartTrainerGame).padLeft(50);
        }

        stage.addActor(root);
        updateSliderValue();
    }

    private void updateSliderValue() {
        int dung = Math.round(sliderLevel.getValue());
        labelSlider.setText("" + dung);
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
        MyLogger.getInstance().log("NewGameScreen.resize()", "width=" + width + ", heightX=" + height);
        stage.getViewport().update(width, height, true);
    }

}
