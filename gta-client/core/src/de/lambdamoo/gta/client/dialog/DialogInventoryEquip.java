package de.lambdamoo.gta.client.dialog;

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ActorGestureListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;

import java.util.List;

import de.lambdamoo.gta.client.util.SpriteImages;
import de.lambdamoo.gta.world.GameWorld;
import de.lambdamoo.gta.world.components.Item;
import de.lambdamoo.gta.world.components.Player;

public class DialogInventoryEquip extends WindowDialog {
    private Table inventoryTable = null;
    private Item lastRemovedItem = null;
    private ActorGestureListener gestureListener = new ActorGestureListener() {

        /**
         * With this gesture the player can drop items from inventory
         * @param event
         * @param velocityX
         * @param velocityY
         * @param button
         */
        @Override
        public void fling(InputEvent event, float velocityX, float velocityY, int button) {
            if (Math.abs(velocityX) > Math.abs(velocityY)) {
                Label label = (Label) event.getTarget();
                if (label.getUserObject() != null) {
                    destroyItem((Item) label.getUserObject());
                }
            }
        }

        /**
         * With this gesture the player can equip or use items from inventory
         * @param event
         * @param x
         * @param y
         * @param count
         * @param button
         */
        @Override
        public void tap(InputEvent event, float x, float y, int count, int button) {
            if (count >= 2) {
                Label label = (Label) event.getTarget();
                Item item = (Item) label.getUserObject();
                gameWorld.getActionSystem().playerItemEquip(item);
                insertInventory(gameWorld.getActionSystem().getPlayerInventory(), gameWorld.getActionSystem().getPlayerEquipedItems());
            }
        }
    };
    private ClickListener removeBtnClickListener = new ClickListener() {
        @Override
        public void clicked(InputEvent event, float x, float y) {
            Image btn = (Image) event.getTarget();
            destroyItem((Item) btn.getUserObject());
        }
    };

    public DialogInventoryEquip(GameWorld ggameWorld, SpriteImages spriteImages) {
        super("Inventory", ggameWorld, spriteImages);

        inventoryTable = new Table();
        ScrollPane scrollPane = new ScrollPane(inventoryTable);
        Label label = new Label("Double tap to equip an item, swipe right to drop an unequipped item.", spriteImages.getLabelStyleDefault());
        label.setWrap(true);
        add(label).align(Align.center).fillX().padLeft(30).padRight(30);
        row();
        add(scrollPane).fill().expand().padTop(20);
        inventoryTable.setTouchable(Touchable.enabled);
        inventoryTable.padLeft(10);
        inventoryTable.padRight(10);
    }

    private void destroyItem(Item item) {
        lastRemovedItem = item;
        gameWorld.getCore().showBoxConfirmation(stage, "Destroy " + lastRemovedItem.name + "?", new MyDialogListener() {
            @Override
            public boolean onPerform(Result result) {
                if (result.equals(Result.Yes)) {
                    // destroy the item
                    if (lastRemovedItem != null) {
                        gameWorld.getActionSystem().playerItemDrop(lastRemovedItem);
                        insertInventory(gameWorld.getActionSystem().getPlayerInventory(), gameWorld.getActionSystem().getPlayerEquipedItems());
                        gameWorld.addMessage("You drop the " + lastRemovedItem.name);
                        lastRemovedItem = null;
                    }
                }
                return true;
            }
        });


    }

    private void insertInventory(List<Item> items, List<Item> equiped) {
        inventoryTable.clearChildren();
        for (int i = 0; i < items.size(); i++) {
            Item item = items.get(i);
            if (!item.usable) {
                boolean isEquipped = equiped.contains(item);

                // show equip icon
                if (isEquipped) {
                    inventoryTable.add(new Image(spriteImages.getBtnEquip()));
                } else {
                    inventoryTable.add();
                }

                // label to handle equip the item
                Label labelEquip = new Label("", spriteImages.getLabelStyleDefault());
                String name = item.name;
                if (item.amount > 1) {
                    name += " (" + item.amount + ")";
                }
                labelEquip.setText(name);
                if (!isEquipped) {
                    labelEquip.setUserObject(item);
                    labelEquip.addListener(gestureListener);
                }
                inventoryTable.add(labelEquip).align(Align.left).padTop(10);

                inventoryTable.row();
            }
        }
    }

    public void populateInventory(Player player) {
        insertInventory(player.inventory.getListInventory(), player.inventory.listEquiped);
    }

}
