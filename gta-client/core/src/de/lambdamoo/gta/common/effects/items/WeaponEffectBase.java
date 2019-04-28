package de.lambdamoo.gta.common.effects.items;

import de.lambdamoo.gta.common.action.BaseItemEffect;
import de.lambdamoo.gta.world.GameWorld;
import de.lambdamoo.gta.world.components.Item;
import de.lambdamoo.gta.world.components.Player;
import de.lambdamoo.gta.world.components.Status;

public class WeaponEffectBase extends BaseItemEffect {
    private int enhancement = 0;

    public WeaponEffectBase(int enhancement) {
        super();
        this.enhancement = enhancement;
    }

    @Override
    public void recalcPower(Item item, GameWorld mg) {
        Status status = getStatus(mg);
        status.powerWeapon = enhancement;
    }

    @Override
    public boolean equip(Item item, GameWorld mg) {
        Player player = getPlayer(mg);
        if (item.itemGroup != null && item.itemGroup.equals(Item.ItemGroup.Weapon)) {
            player.inventory.currentWeapon = item;
        }
        return true;
    }

    @Override
    public boolean unequip(Item item, GameWorld mg) {
        Status status = getStatus(mg);
        status.powerWeapon = 0;
        Player player = getPlayer(mg);
        player.inventory.currentWeapon = null;
        return true;
    }
}