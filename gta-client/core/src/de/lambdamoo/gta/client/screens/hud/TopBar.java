package de.lambdamoo.gta.client.screens.hud;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.profiling.GLProfiler;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.HorizontalGroup;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.Align;

import java.text.NumberFormat;
import java.util.Iterator;

import de.lambdamoo.gta.client.dialog.MyDialogListener;
import de.lambdamoo.gta.client.util.SpriteImages;
import de.lambdamoo.gta.common.effects.player.PlayerEffect;
import de.lambdamoo.gta.world.GameWorld;
import de.lambdamoo.gta.world.STATUS;
import de.lambdamoo.gta.world.components.Player;
import de.lambdamoo.gta.world.components.Status;

public class TopBar extends Table {
    private Label labelScore = null;
    private Label labelTime = null;
    private Label labelFPS = null;
    private Label labelLevel = null;
    private Label labelLives = null;
    private GLProfiler profiler = null;
    private NumberFormat numberFormat = NumberFormat.getIntegerInstance();
    private NumberFormat secFormater = null;
    private int calls = 0;
    private int draws = 0;
    private int bindings = 0;
    private HorizontalGroup effectGroup = new HorizontalGroup();
    private SpriteImages spriteImages = null;
    private GameWorld gameWorld = null;

    public TopBar() {
        secFormater = NumberFormat.getIntegerInstance();
        secFormater.setMaximumFractionDigits(0);
        secFormater.setMinimumIntegerDigits(2);
        profiler = new GLProfiler(Gdx.graphics);
        profiler.enable();
    }

    public void build2D(GameWorld gameWorld, SpriteImages spriteImages) {
        this.gameWorld = gameWorld;
        this.spriteImages = spriteImages;
        Table table = new Table();
        this.align(Align.top);

        Label.LabelStyle labelStyle = spriteImages.getLabelStyleDefault();
        labelScore = new Label("", labelStyle);
        labelLives = new Label("", labelStyle);
        labelTime = new Label("", labelStyle);
        labelLevel = new Label("", labelStyle);
        labelFPS = new Label("", labelStyle);
        table.add(new Image(spriteImages.getDrawableFromAtlasGameUI("score-48"))).padLeft(20);
        table.add(labelScore).width(160).padRight(30);
        table.add(new Image(spriteImages.getDrawableFromAtlasGameUI("lives-48")));
        table.add(labelLives).width(60).padRight(30);
        table.add(new Image(spriteImages.getDrawableFromAtlasGameUI("time-48")));
        table.add(labelTime).width(110).padRight(30);
        table.add(new Image(spriteImages.getDrawableFromAtlasGameUI("level-48")));
        table.add(labelLevel).width(70);
        table.setWidth(500);

        Table leftTable = new Table();
        leftTable.add(table);
        leftTable.row();
        leftTable.add(effectGroup).align(Align.left);
        leftTable.row();

        ImageButton btnNextLevel = new ImageButton(spriteImages.getImageButtonStyle());
        btnNextLevel.addListener(new ChangeListener() {
            public void changed(ChangeEvent event, Actor actor) {
                nextLevelConfirm();
            }
        });
        StackButton stackBtn = new StackButton(btnNextLevel, spriteImages.getDrawableFromAtlasGameUI("exit-door"));

        add(leftTable).expandX().fillX().align(Align.topLeft);
        add(stackBtn).align(Align.right);
    }

    private void nextLevelConfirm() {
        gameWorld.getCore().showBoxConfirmation(gameWorld.getRenderSystem().getGameStage(), "Proceed to next level?", new MyDialogListener() {
            @Override
            public boolean onPerform(MyDialogListener.Result result) {
                if (result.equals(Result.Yes)) {
                    nextLevel();
                }
                return true;
            }
        });
    }

    private void nextLevel() {
        gameWorld.setGameStatus(STATUS.NEXTLEVEL);
    }

    public void renderPlayerStatus(Player player, Status status) {
        labelScore.setText(numberFormat.format(player.score));
        int min = (int) (player.time / 60);
        int sec = (int) (player.time % 60);
        labelTime.setText(min + ":" + secFormater.format(sec));
        labelLives.setText(String.valueOf(player.lifes));
        labelLevel.setText(String.valueOf(status.level));
        if (player.isEffectsChanged()) {
            renderPlayerEffects(player);
            player.resetEffectsChanged();
        }
    }

    private void renderPlayerEffects(Player player) {
        effectGroup.clear();
        Iterator iter = player.listEffects.iterator();
        while (iter.hasNext()) {
            PlayerEffect effect = (PlayerEffect) iter.next();
            if (effect.name != null) {
                Image img = new Image(spriteImages.getDrawableFromAtlasGameUI(effect.name));
                effectGroup.addActor(img);
            }
        }
    }

    /**
     * Render debug information to the screen
     *
     * @param deltaTime
     */
    public void renderFpsCounter(float deltaTime) {
        float fps = 1 / deltaTime;
        int frameCalls = profiler.getCalls() - calls;
        calls = profiler.getCalls();
        int frameDraws = profiler.getDrawCalls() - draws;
        draws = profiler.getDrawCalls();
        int frameBindings = profiler.getTextureBindings() - bindings;
        bindings = profiler.getTextureBindings();
        String str = "FPS: " + numberFormat.format(fps) + "; TB: " + frameBindings + "; Draws: " + frameDraws + "; Calls: " + frameCalls;
        labelFPS.setText(str);
    }
}
