package de.lambdamoo.gta.world.subcomponents;


import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import de.lambdamoo.gta.world.components.Item;
import de.lambdamoo.gta.world.systems.util.ItemNameComparator;

public class HeroInventory {

    public Item currentWeapon = null;
    public Item currentArmor = null;
    public Item currentShield = null;
    public boolean inventoryHasChanged = false;

    public boolean holdsCross = false;
    public int arrowsCount = 0;
    public boolean hasBow = false;
    public List<Item> listEquiped = new ArrayList<Item>();
    private List<Item> listInventory = new ArrayList<Item>();
    private ItemNameComparator itemNameComparator = new ItemNameComparator();

    /**
     * This method removes the item from the inventory. If the amount is > 1 then the amount is decreased and the item object stays in the inventory list
     *
     * @param item
     */
    public void removeItem(Item item) {
        int indexOld = listInventory.indexOf(item);
        if (indexOld != -1) {
            Item old = listInventory.get(indexOld);
            if (old.amount > 1) {
                old.amount--;
            } else {
                listInventory.remove(item);
            }
        }

        listEquiped.remove(item);
        inventoryHasChanged = true;
    }

    /**
     * checks whether this item is in the inventory or is equipped.
     *
     * @param itemId
     * @return
     */
    public boolean hasItem(int itemId) {
        return listInventory.indexOf(itemId) != -1 || listEquiped.indexOf(itemId) != -1;
    }

    /**
     * This method checks whether the player has a bow
     *
     * @return
     */
    public boolean hasBow() {
        return hasBow;
    }

    public void addItem(Item item) {
        if (item.typeName.equals("Arrow")) {
            increaseArrows(item);
        } else if (item.name.equals("Bow")) {
            hasBow = true;
        } else {
            boolean alreadyInInventory = false;
            int indexOld = listInventory.indexOf(item);
            if (indexOld != -1) {
                // item already there, increase amount
                Item old = listInventory.get(indexOld);
                old.amount++;
                alreadyInInventory = true;
            }
            if (!alreadyInInventory) {
                // add it as a new item in inventory list
                listInventory.add(item);
                Collections.sort(listInventory, itemNameComparator);
            }
        }
        inventoryHasChanged = true;
    }

    public void increaseArrows(Item item) {
        if (item.name.equals("Arrows")) {
            this.arrowsCount += item.amount;
        }
    }

    public List<Item> getListInventory() {
        return listInventory;
    }

    /**
     * This method checks whether the player has arrows in his inventory
     *
     * @return
     */
    public boolean hasArrows() {
        return arrowsCount > 0;
    }

    public void decreaseArrows(int count) {
        this.arrowsCount -= count;
        if (this.arrowsCount < 0) {
            this.arrowsCount = 0;
        }
    }

}
