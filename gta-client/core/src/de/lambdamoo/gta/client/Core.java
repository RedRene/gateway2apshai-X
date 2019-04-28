package de.lambdamoo.gta.client;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.scenes.scene2d.Stage;

import java.util.List;

import de.lambdamoo.gta.client.dialog.DialogBox;
import de.lambdamoo.gta.client.dialog.MyDialogListener;
import de.lambdamoo.gta.client.screens.HighScoreScreen;
import de.lambdamoo.gta.client.screens.MapDungeonScreen;
import de.lambdamoo.gta.client.screens.MenuScreen;
import de.lambdamoo.gta.client.screens.NewGameScreen;
import de.lambdamoo.gta.client.screens.NextLevelScreen;
import de.lambdamoo.gta.client.screens.OptionsScreen;
import de.lambdamoo.gta.client.screens.ScreenListener;
import de.lambdamoo.gta.client.screens.SplashScreen;
import de.lambdamoo.gta.client.screens.manager.ScreenManager;
import de.lambdamoo.gta.client.screens.manager.ScreenTransition;
import de.lambdamoo.gta.client.screens.manager.ScreenTransitionFade;
import de.lambdamoo.gta.client.util.MyLogger;
import de.lambdamoo.gta.client.util.PlayServices;
import de.lambdamoo.gta.client.util.SpriteImages;
import de.lambdamoo.gta.world.GameWorld;
import de.lambdamoo.gta.world.STATUS;

public class Core extends ApplicationAdapter {
    private final static String PREFSFILE = "de.lambdamoo.gta.client.Core";
    private MapDungeonScreen mapDungeonScreen = null;
    private GameWorld gameWorld;
    private SpriteImages spriteImages = null;
    private boolean isDevelopment = false;
    private NewGameScreen newGameScreen = null;
    private NextLevelScreen nextLevelScreen = null;
    private MenuScreen menuScreen = null;
    private STATUS oldStatus = null;
    private PlayServices playServices = null;
    private HighScoreScreen highScoreScreen = null;
    private DialogBox dialogBox = null;
    private OptionsScreen optionsScreen;
    private ScreenManager screenManager = new ScreenManager();

    public Core(PlayServices playServices) {
        this.playServices = playServices;
    }

    public boolean isDevelopment() {
        return isDevelopment;
    }

    public void setDevelopment(boolean development) {
        isDevelopment = development;
    }

    @Override
    public void render() {
        screenManager.render();
    }

    public SpriteImages getSpriteImages() {
        return spriteImages;
    }

    /**
     * This method saves the options to the file
     */
    public void savePreferences() {
        MyLogger.getInstance().log("GameWorld", "save options");
        Preferences prefs = Gdx.app.getPreferences(PREFSFILE);
        prefs.clear();
        prefs.putString("prefs.fontSize", spriteImages.getFontSize().toString());
        prefs.putFloat("prefs.volume", getGameWorld().getSoundManager().getSoundVolume());
        prefs.putFloat("prefs.zoom", gameWorld.getRenderSystem().getZoom());
        prefs.flush();
    }

    public GameWorld getGameWorld() {
        return gameWorld;
    }

    @Override
    public void create() {
        Gdx.app.setLogLevel(Application.LOG_DEBUG);
        screenManager.setScreen(new SplashScreen(this));
    }

    /**
     * This method inits the Game Core. It must be invoked before the menu screen is displayed
     *
     * @param images
     */
    public void initCore(SpriteImages images) {
        this.spriteImages = images;
        gameWorld = new GameWorld(this);
        dialogBox = new DialogBox("", spriteImages.getWindowStyle(), spriteImages.getTextButtonStyle(), spriteImages.getLabelStyleDefault());
        dialogBox.setBackground(spriteImages.getNinePatchOpaqueBackgroundStrong());
        getPlayServices().init();
        loadPreferences();
    }

    /**
     * This method loads the options from the file
     */
    private void loadPreferences() {
        MyLogger.getInstance().log("GameWorld", "save options");
        Preferences prefs = Gdx.app.getPreferences(PREFSFILE);
        SpriteImages.FontSize fontSize = SpriteImages.FontSize.Medium;
        String fontSizeName = prefs.getString("prefs.fontSize");
        if (fontSizeName != null && fontSizeName.trim().length() > 0) {
            fontSize = SpriteImages.FontSize.valueOf(fontSizeName);
            spriteImages.setFontSize(fontSize);
            float volume = prefs.getFloat("prefs.volume");
            float zoom = prefs.getFloat("prefs.zoom");
            getGameWorld().getSoundManager().setSoundVolume(volume);
            getGameWorld().getRenderSystem().setZoom(zoom);
        }
    }

    public PlayServices getPlayServices() {
        return playServices;
    }

    /**
     * This method shows the menu screen
     */
    public void showScreenMenu() {
        if (menuScreen == null) {
            menuScreen = new MenuScreen(this);
        }
        screenManager.setScreen(menuScreen);
    }

    /**
     * This method shows the high score screen
     */
    public void showHighScoreScreen() {
        if (highScoreScreen == null) {
            highScoreScreen = new HighScoreScreen(this);
        }
        screenManager.setScreen(highScoreScreen);
        highScoreScreen.reloadHighScores();
    }

    /**
     * This method shows the select level screen
     */
    public void showSelectLevelScreen() {
        if (newGameScreen == null) {
            newGameScreen = new NewGameScreen(this);
        }
        screenManager.setScreen(newGameScreen);
    }

    /**
     * This method shows the info screen
     */
    public void showNextLevelScreen(List<String> messages, ScreenListener listener) {
        if (nextLevelScreen == null) {
            nextLevelScreen = new NextLevelScreen(this);
        }
        nextLevelScreen.setContinueListener(listener);
        nextLevelScreen.addMessages(messages);
        screenManager.setScreen(nextLevelScreen);
    }

    /**
     * This method shows the game screen. The stage of the MapDungeonScreen is given to the renderSystem
     */
    public void showScreenMapDungeon() {
        if (mapDungeonScreen == null) {
            mapDungeonScreen = new MapDungeonScreen(this);
            gameWorld.setStage(mapDungeonScreen.getStage());
        }
        ScreenTransition transition = ScreenTransitionFade.init(0.75f);
        screenManager.setScreen(mapDungeonScreen, transition);
    }


    /**
     * This method shows the game screen. The stage of the MapDungeonScreen is given to the renderSystem
     */
    public void showScreenOptions() {
        if (optionsScreen == null) {
            optionsScreen = new OptionsScreen(this);
        }
        screenManager.setScreen(optionsScreen);
    }

    @Override
    public void dispose() {
        if (getGameWorld() != null) {
            getGameWorld().dispose();
        }
    }

    @Override
    public void pause() {
        if (gameWorld != null) {
            this.oldStatus = gameWorld.getGameStatus();
        }
    }

    @Override
    public void resume() {
        if (gameWorld != null) {
            gameWorld.setGameStatus(this.oldStatus);
        }
        this.oldStatus = null;
    }

    @Override
    public void resize(int width, int height) {
        screenManager.resize(width, height);
    }

    /**
     * This method shows the standard yes|no dialog box.
     *
     * @param stage
     * @param text
     * @param listener
     */
    public void showBoxConfirmation(Stage stage, String text, MyDialogListener listener) {
        dialogBox.setText(text);
        dialogBox.setDialogListener(listener);
        dialogBox.showButtonYesNo();
        dialogBox.show(stage);
    }

    /**
     * This method shows the standard yes|no dialog box.
     *
     * @param stage
     * @param text
     */
    public void showBoxMessage(Stage stage, String text, MyDialogListener listener) {
        dialogBox.setText(text);
        dialogBox.setDialogListener(listener);
        dialogBox.showButtonOk();
        dialogBox.show(stage);
    }

    /**
     * This method shows the standard yes|no dialog box.
     *
     * @param stage
     * @param text
     */
    public void showBoxMessage(Stage stage, String text, String btnText, MyDialogListener listener) {
        dialogBox.setText(text);
        dialogBox.setDialogListener(listener);
        dialogBox.showButtonOk(btnText);
        dialogBox.show(stage);
    }

}
