package de.lambdamoo.gta.common.effects.items;

import de.lambdamoo.gta.common.action.BaseItemEffect;
import de.lambdamoo.gta.world.GameWorld;
import de.lambdamoo.gta.world.components.Item;
import de.lambdamoo.gta.world.components.Player;

public class AmuletEffectBase extends BaseItemEffect {
    public String stat = null;

    public AmuletEffectBase(String stat) {
        this.stat = stat;
    }

    /**
     * Always use when found
     *
     * @param item
     * @param mg
     */
    @Override
    public void found(Item item, GameWorld mg) {
        use(item, mg);
        getPlayer(mg).inventory.removeItem(item);
    }

    @Override
    public boolean use(Item item, GameWorld mg) {
        Player player = getPlayer(mg);
        boolean doConsume = false;
        if (stat.equals("Strength")) {
            if (!player.amuletStrengthConsumed) {
                player.strength += 4;
                player.amuletStrengthConsumed = true;
                doConsume = true;
            }
        } else if (stat.equals("Agility")) {
            if (!player.amuletAgilityConsumed) {
                player.agility += 4;
                player.amuletAgilityConsumed = true;
                doConsume = true;
            }
        } else if (stat.equals("Luck")) {
            if (!player.amuletLuckConsumed) {
                player.luck += 4;
                player.baseLuck += 4;
                player.amuletLuckConsumed = true;
                doConsume = true;
            }
        }

        if (doConsume) {
            item.consumed = true;
            mg.addMessage(stat + " amulet consumed.");
        } else {
            mg.addMessage("This amulet is already consumed.");
        }
        return true;
    }
}
