package de.lambdamoo.gta.common.effects.items;

import de.lambdamoo.gta.world.GameWorld;
import de.lambdamoo.gta.world.components.Item;
import de.lambdamoo.gta.world.components.Player;

public class MagicSwordCurseEffectBase extends CurseEffectBase {
    @Override
    public boolean equip(Item item, GameWorld mg) {
        super.equip(item, mg);
        Player player = getPlayer(mg);
        if (item.itemGroup != null && item.itemGroup.equals(Item.ItemGroup.Weapon)) {
            player.inventory.currentWeapon = item;
        }
        return true;
    }

    @Override
    public void recalcPower(Item item, GameWorld mg) {
        super.recalcPower(item, mg);
        getStatus(mg).powerWeapon = 3;
    }
}
