package de.lambdamoo.gta.client.screens.hud;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Touchpad;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.Align;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import de.lambdamoo.gta.client.util.SpriteImages;
import de.lambdamoo.gta.world.GameWorld;
import de.lambdamoo.gta.world.components.Attacking;
import de.lambdamoo.gta.world.components.Velocity;
import de.lambdamoo.gta.world.systems.ActionSystem;

public class ControlBar extends Touchpad {
    protected ImageButton btnCtrlFireSword = null;
    protected ImageButton btnCtrlFireArrow = null;
    private List<Integer> pressedMoveKeyCodes = new ArrayList<Integer>(5);
    private int[] moveKeycodes = new int[]{Input.Keys.W, Input.Keys.S, Input.Keys.A, Input.Keys.D};
    private Stage gameStage = null;
    private GameWorld gameWorld = null;
    private SpriteImages spriteImages = null;
    private Label labelArrowCount;

    public ControlBar(TouchpadStyle style) {
        super(20, style);
    }

    public void build2D(Stage gameStage, GameWorld gameWorld, SpriteImages spriteImages) {
        this.gameStage = gameStage;
        this.gameWorld = gameWorld;
        this.spriteImages = spriteImages;
        this.init();
    }

    protected void init() {
        btnCtrlFireSword = new ImageButton(spriteImages.getButtonControlFire());
        btnCtrlFireSword.addListener(new InputListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                event.handle();
                ActionSystem as = gameWorld.getActionSystem();
                as.setAttackMode(Attacking.AttackMode.Sword);
                as.startAttackPlayer();
                return true;
            }
        });
        btnCtrlFireArrow = new ImageButton(spriteImages.getButtonControlFire());
        btnCtrlFireArrow.addListener(new InputListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                event.handle();
                ActionSystem as = gameWorld.getActionSystem();
                as.setAttackMode(Attacking.AttackMode.Arrow);
                as.startAttackPlayer();
                return true;
            }
        });
        labelArrowCount = new Label("", spriteImages.getLabelStyleDefault());
        labelArrowCount.setFillParent(true);
        labelArrowCount.setAlignment(Align.bottomRight);

        setResetOnTouchUp(true);

        addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                event.handle();
                ActionSystem as = gameWorld.getActionSystem();
                // This is run when anything is changed on this actor.
                float deltaX = ((Touchpad) actor).getKnobPercentX();
                float deltaY = ((Touchpad) actor).getKnobPercentY();
                if (Math.abs(deltaX) < 0.20 && Math.abs(deltaY) < 0.20) {
                    as.stopMovePlayer();
                } else {
                    if (Math.abs(deltaX) > Math.abs(deltaY)) {
                        // x-axis
                        if (deltaX < 0) {
                            as.startMovePlayer(Velocity.HeadingDirection.WEST);
                        } else {
                            as.startMovePlayer(Velocity.HeadingDirection.EAST);
                        }
                    } else {
                        // y-axis
                        if (deltaY > 0) {
                            as.startMovePlayer(Velocity.HeadingDirection.NORTH);
                        } else {
                            as.startMovePlayer(Velocity.HeadingDirection.SOUTH);
                        }
                    }
                }
            }
        });

        // this is only for the desktop version:
        gameStage.addListener(new InputListener() {
            @Override
            public boolean keyUp(InputEvent event, int keycode) {
                pressedMoveKeyCodes.remove(new Integer(keycode));
                if (pressedMoveKeyCodes.size() == 0) {
                    gameWorld.getActionSystem().stopMovePlayer();
                }
                return true;
            }

            @Override
            public boolean keyDown(InputEvent event, int keycode) {
                ActionSystem as = gameWorld.getActionSystem();
                if (Arrays.asList(moveKeycodes).contains(keycode)) {
                    pressedMoveKeyCodes.add(keycode);
                }
                switch (keycode) {
                    case Input.Keys.W:
                        as.startMovePlayer(Velocity.HeadingDirection.NORTH);
                        break;
                    case Input.Keys.S:
                        as.startMovePlayer(Velocity.HeadingDirection.SOUTH);
                        break;
                    case Input.Keys.A:
                        as.startMovePlayer(Velocity.HeadingDirection.WEST);
                        break;
                    case Input.Keys.D:
                        as.startMovePlayer(Velocity.HeadingDirection.EAST);
                        break;
                    case Input.Keys.SPACE:
                        as.startAttackPlayer();
                        break;
                }
                return true;
            }
        });
    }

    public void setArrowCount(int count) {
        this.labelArrowCount.setText(String.valueOf(count + " "));
    }

    public StackButton getFireButtonSword() {
        StackButton btn = new StackButton(btnCtrlFireSword, spriteImages.getDrawableFromAtlasGameUI("schwert96"));
        return btn;
    }

    public void setDisabledButtonArrows(boolean dis) {
        this.btnCtrlFireArrow.setDisabled(dis);
    }

    public StackButton getFireButtonArrows() {
        StackButton btn = new StackButton(btnCtrlFireArrow, spriteImages.getDrawableFromAtlasGameUI("pfeil96magisch"));
        btn.addLabel(labelArrowCount);
        return btn;
    }

}
