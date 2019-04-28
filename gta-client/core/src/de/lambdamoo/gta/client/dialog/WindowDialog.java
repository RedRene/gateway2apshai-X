package de.lambdamoo.gta.client.dialog;

import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Window;
import com.badlogic.gdx.utils.Align;

import de.lambdamoo.gta.client.screens.manager.ScreenResolution;
import de.lambdamoo.gta.client.util.SpriteImages;
import de.lambdamoo.gta.world.GameWorld;

/**
 * WindowDialog
 */
public class WindowDialog extends Window {
    protected SpriteImages spriteImages = null;
    protected Stage stage = null;
    protected GameWorld gameWorld = null;

    /**
     * Default constructor.
     */
    public WindowDialog(String title, GameWorld gameWorld, SpriteImages spritImages) {
        super("", spritImages.getWindowStyle());
        this.gameWorld = gameWorld;
        this.spriteImages = spritImages;
        setBackground(spritImages.getNinePatchOpaqueBackground());
        Label label = new Label(title, spritImages.getLabelStyleHeadline());
        getTitleTable().clearChildren();
        add(label).align(Align.center);
        row();
        setKeepWithinStage(true);
        setClip(false);
        setTransform(true);

        setModal(true);
        setVisible(true);
        setMovable(true);
        pad(10);
    }

    public void show(Stage stage) {
        this.stage = stage;
        if (stage.getActors().indexOf(this, false) == -1) {
            // if dlgInventory not already added
            stage.addActor(this);
        }
        //float height = Gdx.graphics.getHeight();
        float height = ScreenResolution.getResolution().getHeight();
        height = height / 1.5f;
        setSize(600, height);
        setPosition(100, 100);
        this.setVisible(true);
    }
}
