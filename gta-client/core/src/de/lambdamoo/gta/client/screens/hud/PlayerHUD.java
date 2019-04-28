package de.lambdamoo.gta.client.screens.hud;

import com.artemis.World;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.HorizontalGroup;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;

import de.lambdamoo.gta.client.screens.manager.ScreenResolution;
import de.lambdamoo.gta.client.util.SpriteImages;
import de.lambdamoo.gta.world.GameWorld;
import de.lambdamoo.gta.world.components.Player;
import de.lambdamoo.gta.world.components.Status;

public class PlayerHUD {

    private ActionBar actionBar = null;
    private MessageTable messageTable = null;
    private HealthBar healthBar = null;
    private StatusPlayerBar statusPlayer = null;
    private TopBar topbar = null;
    private ControlBar controlBar = null;
    private OrthographicCamera cameraGUI;
    private Stage gameStage = null;
    private SpriteImages spriteImages = null;
    private GameWorld gameWorld = null;

    public PlayerHUD(GameWorld gameWorld, SpriteImages images) {
        this.gameWorld = gameWorld;
        this.spriteImages = images;
        cameraGUI = new OrthographicCamera(ScreenResolution.getResolution().getVirtualWidth(), ScreenResolution.getResolution().getVirtualHeight());
        cameraGUI.position.set(0, 0, 0);
        cameraGUI.setToOrtho(true); // flip y-axis
        cameraGUI.update();
    }

    /**
     * Set the Stage of the MapDungeonScreen
     *
     * @param stage
     */
    public void setStage(Stage stage) {
        this.gameStage = stage;
        build2D();
    }

    /**
     * Build the 2D HUD
     */
    private void build2D() {
        Label.LabelStyle labelStyleWhite = spriteImages.getLabelStyleDefault();

        // ########################
        // ## top score panel
        // ########################
        topbar = new TopBar();
        topbar.build2D(gameWorld, spriteImages);

        // ########################
        // ## left center panel
        // ########################

        Table leftCenter = new Table();

        // left side of center: MessageTable
        messageTable = new MessageTable();
        messageTable.build2D(spriteImages);

        controlBar = new ControlBar(spriteImages.getTouchpadStyle());
        controlBar.build2D(gameStage, gameWorld, spriteImages);

        actionBar = new ActionBar();
        actionBar.build2D(spriteImages, gameWorld);

        healthBar = new HealthBar(300, 40, spriteImages);

        Image imageHealth = new Image(spriteImages.getDrawableFromAtlasGameUI("health"));
        imageHealth.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                gameWorld.getActionSystem().useHealItem();
            }
        });

        Table grp = new Table();
        grp.add(actionBar);
        grp.add(imageHealth).padRight(-20).padLeft(50);
        grp.add(healthBar).width(300);

        leftCenter.add(messageTable).colspan(2).align(Align.topLeft).grow();
        leftCenter.row();
        leftCenter.add(controlBar).align(Align.bottomLeft);
        leftCenter.add(grp).align(Align.bottom).growX();

        // ########################
        // ## right center panel
        // ########################

        // status
        statusPlayer = new StatusPlayerBar();
        statusPlayer.build2D(labelStyleWhite, spriteImages);

        HorizontalGroup buttonGrp = new HorizontalGroup();
        buttonGrp.space(50);
        buttonGrp.addActor(controlBar.getFireButtonArrows());
        buttonGrp.addActor(controlBar.getFireButtonSword());

        Table rightCenter = new Table();
        rightCenter.add(statusPlayer).expandY().align(Align.right);
        rightCenter.row();
        rightCenter.add(buttonGrp);

        // root panel
        Table root = new Table();
        root.pad(20);
        root.setFillParent(true);
        root.add(topbar).colspan(2).growX();
        root.row();
        root.add(leftCenter).grow().align(Align.left);
        root.add(rightCenter).growY().align(Align.right);
        root.row();

        this.gameStage.addActor(root);
    }

    public void addMessage(String str) {
        messageTable.addMessage(str);
    }

    /**
     * This method renders the current status of the hero: top, status and health
     *
     * @param status
     * @param hero
     */
    public void renderPlayerStatus(Status status, Player hero) {
        topbar.renderPlayerStatus(hero, status);
        statusPlayer.renderPlayerStatus(hero, status);
        renderWeaponStatus(hero, status);
        healthBar.renderPlayerStatus(status.healthCurrent, status.healthMax);

    }

    /**
     * This method renders the arrow count on top of the arrow button
     *
     * @param hero
     * @param status
     */
    public void renderWeaponStatus(Player hero, Status status) {
        controlBar.setArrowCount(hero.inventory.arrowsCount);
        controlBar.setDisabledButtonArrows(!hero.inventory.hasBow && hero.inventory.arrowsCount > 0);
    }

    public void render(World world) {
        // topbar.renderFpsCounter(world.getDelta());
        actionBar.update(world.getDelta());
        messageTable.update(world.getDelta());
        gameStage.act(world.getDelta());
        gameStage.draw();
    }

    public void resize(int width, int height) {
        gameStage.getViewport().update(width, height, true);

        cameraGUI.viewportHeight = ScreenResolution.getResolution().getVirtualWidth();
        cameraGUI.viewportWidth = (ScreenResolution.getResolution().getVirtualWidth() / (float) height) * (float) width;
        cameraGUI.position.set(cameraGUI.viewportWidth / 2, cameraGUI.viewportHeight / 2, 0);
        cameraGUI.update();
    }

    public Stage getGameStage() {
        return gameStage;
    }

}
