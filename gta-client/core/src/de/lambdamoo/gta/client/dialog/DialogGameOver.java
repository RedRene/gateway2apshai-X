package de.lambdamoo.gta.client.dialog;

import com.artemis.ComponentMapper;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Dialog;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

import de.lambdamoo.gta.client.Core;
import de.lambdamoo.gta.client.util.SpriteImages;
import de.lambdamoo.gta.world.components.Player;

public class DialogGameOver extends DialogBox {

    private Core core = null;
    private Label contentLabel = null;
    private Label highScoreLabel = null;
    private Button btnSaveScore = null;

    public DialogGameOver(Core ccore, SpriteImages spriteImages) {
        super("", spriteImages.getWindowStyle(), spriteImages.getTextButtonStyle(), spriteImages.getLabelStyleDefault());
        this.core = ccore;

        contentLabel = new Label("Game Over, you lost your last life!", spriteImages.getLabelStyleDefault());
        highScoreLabel = new Label("Your high score was saved.", spriteImages.getLabelStyleDefault());
        text(contentLabel);
        showButtonOk("Menu");
        btnSaveScore = new TextButton("Save score", spriteImages.getTextButtonStyle());
        getButtonTable().add(btnSaveScore);
        btnSaveScore.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (!btnSaveScore.isDisabled()) {
                    ComponentMapper<Player> mPlayer = core.getGameWorld().getComponentMapper(Player.class);
                    Player player = mPlayer.get(core.getGameWorld().getLocalPlayerId());
                    core.getPlayServices().saveScore(player.name, player.score);
                    getContentTable().removeActor(contentLabel);
                    getContentTable().add(highScoreLabel);
                    btnSaveScore.setDisabled(true);
                }
            }
        });
    }

    /**
     * This method cleans the game world and shows the menu screen
     *
     * @param event
     * @param x
     * @param y
     */
    @Override
    public void onClick(InputEvent event, float x, float y) {
        core.getGameWorld().cleanGameWorld();
        hide();
        core.showScreenMenu();
    }

    @Override
    public Dialog show(Stage stage) {
        Dialog dlg = super.show(stage);
        ComponentMapper<Player> mPlayer = core.getGameWorld().getComponentMapper(Player.class);
        Player player = mPlayer.get(core.getGameWorld().getLocalPlayerId());
        if (player.trainer) {
            btnSaveScore.setDisabled(true);
            btnSaveScore.setVisible(false);
        } else {
            btnSaveScore.setDisabled(false);
            btnSaveScore.setVisible(true);
        }
        return dlg;
    }
}
