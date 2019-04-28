package de.lambdamoo.gta.common.effects.items;

import de.lambdamoo.gta.common.action.BaseItemEffect;
import de.lambdamoo.gta.world.GameWorld;
import de.lambdamoo.gta.world.components.Item;
import de.lambdamoo.gta.world.components.Player;
import de.lambdamoo.gta.world.components.Status;

public class ArmorEffectBase extends BaseItemEffect {
    private int enhancement = 0;

    public ArmorEffectBase(int enhancement) {
        super();
        this.enhancement = enhancement;
    }

    @Override
    public void recalcPower(Item item, GameWorld mg) {
        Status status = getStatus(mg);
        status.powerArmor = enhancement;
    }

    @Override
    public boolean equip(Item item, GameWorld mg) {
        Player player = getPlayer(mg);
        if (item.itemGroup != null && item.itemGroup.equals(Item.ItemGroup.Armor)) {
            player.inventory.currentArmor = item;
        }
        return true;
    }

    @Override
    public boolean unequip(Item item, GameWorld mg) {
        Status status = getStatus(mg);
        status.powerArmor = 0;
        Player player = getPlayer(mg);
        player.inventory.currentArmor = null;
        return true;
    }
}