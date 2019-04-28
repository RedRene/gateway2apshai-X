package de.lambdamoo.gta.client.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Scaling;
import com.badlogic.gdx.utils.viewport.FitViewport;

import de.lambdamoo.gta.client.Core;
import de.lambdamoo.gta.client.screens.manager.AbstractScreen;
import de.lambdamoo.gta.client.screens.manager.ScreenResolution;
import de.lambdamoo.gta.client.screens.util.TypedTextBox;
import de.lambdamoo.gta.client.screens.util.Version;
import de.lambdamoo.gta.client.util.MyLogger;
import de.lambdamoo.gta.client.util.SpriteImages;

import static de.lambdamoo.gta.client.screens.util.FormatUtil.PAD_INNER_ROW;
import static de.lambdamoo.gta.client.screens.util.FormatUtil.PAD_OUTER_BORDER;

public class MenuScreen extends AbstractScreen {

    private Core core = null;
    private TypedTextBox introTextBox;
    private TextButton btnResume = null;
    private Label messageLabel;

    public MenuScreen(Core core) {
        this.core = core;
    }

    @Override
    public void show() {
        initStage();
        buildStage();
        if (core.isDevelopment()) {
            core.getGameWorld().gameNew("RedRene", 2, true);
            core.showScreenMapDungeon();
        }
        String name = core.getGameWorld().hasResumeInformation();
        if (name != null) {
            btnResume.setDisabled(false);
            btnResume.setText("Resume " + name);
        } else {
            btnResume.setDisabled(true);
        }
    }

    private void buildStage() {
        stage = new Stage(new FitViewport(ScreenResolution.getResolution().getWidth(), ScreenResolution.getResolution().getHeight()));

        Button btnNewGame = new TextButton("New Game", core.getSpriteImages().getTextButtonStyle());
        btnNewGame.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                core.showSelectLevelScreen();
            }
        });

        Button btnHighScore = new TextButton("High Scores", core.getSpriteImages().getTextButtonStyle());
        btnHighScore.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                try {
                    core.showHighScoreScreen();
                } catch (Exception e) {
                    MyLogger.getInstance().log(e);
                }
            }
        });

        Button btnOptions = new TextButton("Options", core.getSpriteImages().getTextButtonStyle());
        btnOptions.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                try {
                    core.showScreenOptions();
                } catch (Exception e) {
                    MyLogger.getInstance().log(e);
                }
            }
        });
        btnResume = new TextButton("Resume", core.getSpriteImages().getTextButtonStyle());
        btnResume.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (!btnResume.isDisabled()) {
                    core.getGameWorld().gameResumeSavedLevel();
                }
            }
        });

        Table menu = new Table();
        menu.defaults().width(500).height(100).padBottom(20);
        Image img = new Image(core.getSpriteImages().getBackground(SpriteImages.MapBackground.TitleText));
        img.setScaling(Scaling.fill);
        menu.add(img).width(img.getWidth()).height(img.getHeight()).padBottom(150);
        menu.row();
        menu.add(btnNewGame);
        menu.row();
        menu.add(btnHighScore);
        menu.row();
        menu.add(btnOptions);
        menu.row();
        menu.add(btnResume);
        menu.row();

        FileHandle handle = Gdx.files.internal("texts/intro.txt");
        String text = handle.readString();
        introTextBox = new TypedTextBox(text);

        messageLabel = new Label("", core.getSpriteImages().getLabelStyleDefault());
        messageLabel.setWrap(true);
        messageLabel.setAlignment(Align.topLeft);
        ScrollPane scrollMessages = new ScrollPane(messageLabel);
        scrollMessages.getStyle().background = core.getSpriteImages().getNinePatchTranslucent65Background();

        Table root = new Table();
        root.setFillParent(true);
        root.padTop(PAD_OUTER_BORDER / 2);
        root.padBottom(PAD_OUTER_BORDER / 2);
        root.padLeft(PAD_OUTER_BORDER);
        root.padRight(PAD_OUTER_BORDER);
        root.columnDefaults(0).align(Align.left).spaceBottom(PAD_INNER_ROW).growY();
        root.columnDefaults(1).align(Align.left).spaceBottom(PAD_INNER_ROW).grow().padLeft(50);
        root.add(menu);
        root.add(scrollMessages);
        root.row();

        Table background = new Table();
        background.setFillParent(true);
        background.setBackground(new TextureRegionDrawable(new TextureRegion(core.getSpriteImages().getBackground(SpriteImages.MapBackground.Title))));

        stage.addActor(background);
        stage.addActor(root);
        stage.addActor(new Label("V" + Version.version, core.getSpriteImages().getLabelStyleSmall()));
    }

    private void initStage() {
    }

    @Override
    public void render(float deltaTime) {
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);
        if (stage != null) {
            introTextBox.act(deltaTime);
            messageLabel.setText(introTextBox.getCursorText());
            stage.act(deltaTime);
            stage.draw();
        }
    }

    @Override
    public void resize(int width, int height) {
        MyLogger.getInstance().log("MenuScreen.resize()", "width=" + width + ", heightX=" + height);
        stage.getViewport().update(width, height, true);
    }

}
