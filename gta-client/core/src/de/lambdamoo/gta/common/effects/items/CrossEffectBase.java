package de.lambdamoo.gta.common.effects.items;

import de.lambdamoo.gta.common.action.BaseItemEffect;
import de.lambdamoo.gta.world.GameWorld;
import de.lambdamoo.gta.world.components.Item;
import de.lambdamoo.gta.world.components.Player;

public class CrossEffectBase extends BaseItemEffect {
    @Override
    public boolean equip(Item item, GameWorld mg) {
        Player player = getPlayer(mg);
        player.inventory.holdsCross = true;
        return true;
    }

    @Override
    public boolean unequip(Item item, GameWorld mg) {
        Player player = getPlayer(mg);
        player.inventory.holdsCross = false;
        return true;
    }
}
