package de.lambdamoo.gta.common.effects.items;

import de.lambdamoo.gta.common.action.BaseItemEffect;
import de.lambdamoo.gta.world.GameWorld;
import de.lambdamoo.gta.world.components.Item;
import de.lambdamoo.gta.world.components.Status;

public class GauntletEffectBase extends BaseItemEffect {
    private int enhancement = 0;

    public GauntletEffectBase(int enhancement) {
        super();
        this.enhancement = enhancement;
    }

    @Override
    public void recalcPower(Item item, GameWorld mg) {
        Status status = getStatus(mg);
        status.powerGauntlet = enhancement;
    }

    @Override
    public boolean equip(Item item, GameWorld mg) {
        return true;
    }

    @Override
    public boolean unequip(Item item, GameWorld mg) {
        Status status = getStatus(mg);
        status.powerGauntlet = 0;
        return true;
    }
}