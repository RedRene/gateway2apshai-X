package de.lambdamoo.gta.common.action;

import de.lambdamoo.gta.world.GameWorld;
import de.lambdamoo.gta.world.components.Item;

/**
 * This interface describes an item effect
 */
public interface ItemEffect {

    boolean equip(Item item, GameWorld mg);

    void found(Item item, GameWorld mg);

    boolean unequip(Item item, GameWorld mg);

    boolean use(Item item, GameWorld mg);

    void recalcPower(Item item, GameWorld mg);
}
