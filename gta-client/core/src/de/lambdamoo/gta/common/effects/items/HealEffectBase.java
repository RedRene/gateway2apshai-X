package de.lambdamoo.gta.common.effects.items;

import de.lambdamoo.gta.common.action.BaseItemEffect;
import de.lambdamoo.gta.world.GameWorld;
import de.lambdamoo.gta.world.components.Item;
import de.lambdamoo.gta.world.components.Status;

public class HealEffectBase extends BaseItemEffect {
    @Override
    public boolean use(Item item, GameWorld mg) {
        item.consumed = true;
        Status status = getStatus(mg);
        status.heal(1000);
        mg.addMessage("You are healed.");
        mg.getActionSystem().playerRemoveEffect(SpellCast.Spell.Poison);
        return true;
    }
}