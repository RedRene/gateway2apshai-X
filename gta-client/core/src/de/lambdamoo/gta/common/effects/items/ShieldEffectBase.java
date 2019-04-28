package de.lambdamoo.gta.common.effects.items;

import de.lambdamoo.gta.common.action.BaseItemEffect;
import de.lambdamoo.gta.world.GameWorld;
import de.lambdamoo.gta.world.components.Item;
import de.lambdamoo.gta.world.components.Player;
import de.lambdamoo.gta.world.components.Status;

public class ShieldEffectBase extends BaseItemEffect {
    private int enhancement = 0;

    public ShieldEffectBase(int enhancement) {
        super();
        this.enhancement = enhancement;
    }

    @Override
    public boolean equip(Item item, GameWorld mg) {
        Player player = getPlayer(mg);
        if (item.itemGroup != null && item.itemGroup.equals(Item.ItemGroup.Shield)) {
            player.inventory.currentShield = item;
        }
        return true;
    }

    @Override
    public void recalcPower(Item item, GameWorld mg) {
        Status status = getStatus(mg);
        status.powerShield = enhancement;
    }

    @Override
    public boolean unequip(Item item, GameWorld mg) {
        Status status = getStatus(mg);
        status.powerShield = 0;
        Player player = getPlayer(mg);
        player.inventory.currentShield = null;
        return true;
    }
}