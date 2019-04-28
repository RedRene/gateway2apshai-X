package de.lambdamoo.gta.client.screens.hud;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.Align;

import de.lambdamoo.gta.client.util.SpriteImages;
import de.lambdamoo.gta.world.GameWorld;
import de.lambdamoo.gta.world.STATUS;

public class ActionBar extends Table {
    private ActionBarImageButton btnKeys = null;
    private ActionBarImageButton btnSearchSecret = null;
    private ActionBarImageButton btnInventory = null;
    private ActionBarImageButton btnUseItem = null;
    private ActionBarImageButton btnLocateTraps = null;
    private GameWorld gameWorld = null;

    public void update(float deltaTime) {
        btnKeys.update(deltaTime);
        btnLocateTraps.update(deltaTime);
        btnSearchSecret.update(deltaTime);
    }

    public void build2D(SpriteImages spriteImages, GameWorld gWorld) {
        this.gameWorld = gWorld;
        align(Align.center);
        btnKeys = new ActionBarImageButton(spriteImages.getImageButtonStyle(), 10);
        btnLocateTraps = new ActionBarImageButton(spriteImages.getImageButtonStyle(), 10);
        btnSearchSecret = new ActionBarImageButton(spriteImages.getImageButtonStyle(), 10);
        btnInventory = new ActionBarImageButton(spriteImages.getImageButtonStyle(), 10);
        btnUseItem = new ActionBarImageButton(spriteImages.getImageButtonStyle(), 10);
        add(new StackButton(btnKeys, spriteImages.getDrawableFromAtlasGameUI("key"))).width(103).padLeft(10);
        add(new StackButton(btnLocateTraps, spriteImages.getDrawableFromAtlasGameUI("locatetrap"))).width(103).padLeft(10);
        add(new StackButton(btnSearchSecret, spriteImages.getDrawableFromAtlasGameUI("secretdoor"))).width(103).padLeft(10);
        add(new StackButton(btnInventory, spriteImages.getDrawableFromAtlasGameUI("bag"))).width(103).padLeft(30);
        add(new StackButton(btnUseItem, spriteImages.getDrawableFromAtlasGameUI("scroll"))).width(103).padLeft(10);
        btnKeys.addListener(new ChangeListener() {
            public void changed(ChangeEvent event, Actor actor) {
                if (!btnKeys.isOnCooldown()) {
                    btnKeys.startCooldown();
                    gameWorld.getActionSystem().openDoor();
                }
            }
        });
        btnLocateTraps.addListener(new ChangeListener() {
            public void changed(ChangeEvent event, Actor actor) {
                if (!btnLocateTraps.isOnCooldown()) {
                    btnLocateTraps.startCooldown();
                    gameWorld.getActionSystem().locateTraps();
                }
            }
        });
        btnSearchSecret.addListener(new ChangeListener() {
            public void changed(ChangeEvent event, Actor actor) {
                if (!btnSearchSecret.isOnCooldown()) {
                    btnSearchSecret.startCooldown();
                    gameWorld.getActionSystem().searchSecretDoors();
                    //gameWorld.setGameStatus(STATUS.GAMEOVER); // only for testing
                }
            }
        });
        btnInventory.addListener(new ChangeListener() {
            public void changed(ChangeEvent event, Actor actor) {
                gameWorld.showDialogInventory();
            }
        });
        btnUseItem.addListener(new ChangeListener() {
            public void changed(ChangeEvent event, Actor actor) {
                gameWorld.showDialogUseItem();
            }
        });
        //Image frame = new Image(spriteImages.getDrawableFromAtlasGameUI("hud"));
        //frame.setPosition(0, 0);
        //frame.setTouchable(Touchable.disabled);
        //addActor(frame);
    }
}
