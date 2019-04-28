package de.lambdamoo.gta.world.systems.util;

import java.util.Comparator;

import de.lambdamoo.gta.world.components.Item;

public class ItemNameComparator implements Comparator<Item> {
    @Override
    public int compare(Item item1, Item item2) {
        return item1.name.compareToIgnoreCase(item2.name);
    }
}