package de.lambdamoo.gta.client.dialog;

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ActorGestureListener;
import com.badlogic.gdx.utils.Align;

import de.lambdamoo.gta.client.util.SpriteImages;
import de.lambdamoo.gta.world.GameWorld;
import de.lambdamoo.gta.world.components.Item;
import de.lambdamoo.gta.world.components.Player;

public class DialogScrollCast extends WindowDialog {
    private ActorGestureListener gestureListener = new ActorGestureListener() {
        @Override
        public void tap(InputEvent event, float x, float y, int count, int button) {
            Label label = (Label) event.getTarget();
            Item item = (Item) label.getUserObject();
            gameWorld.getActionSystem().playerItemUse(item);
            setVisible(false);
        }
    };

    private Table inventoryTable = null;

    public DialogScrollCast(GameWorld gameWorld, SpriteImages spriteImages) {
        super("Cast scroll", gameWorld, spriteImages);

        inventoryTable = new Table();
        ScrollPane scrollPane = new ScrollPane(inventoryTable);
        Label label = new Label("Tap on a scroll or salve to use it.", spriteImages.getLabelStyleDefault());
        label.setWrap(true);
        add(label).align(Align.center).fillX().padLeft(30).padRight(30);
        row();
        add(scrollPane).fill().expand().padTop(20);
        inventoryTable.setTouchable(Touchable.enabled);
        inventoryTable.padLeft(10);
        inventoryTable.padRight(10);
    }

    public void populateUsable(Player player) {
        inventoryTable.clearChildren();
        for (int i = 0; i < player.inventory.getListInventory().size(); i++) {
            Item item = player.inventory.getListInventory().get(i);
            if (item.usable) {
                String name = item.name;
                if (item.amount > 1) {
                    name += " (" + item.amount + ")";
                }
                Label label = new Label(name, spriteImages.getLabelStyleDefault());
                label.addListener(gestureListener);
                label.setUserObject(item);
                inventoryTable.add(label).align(Align.left);
                inventoryTable.row();
            }
        }
    }


}
