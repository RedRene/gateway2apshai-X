package de.lambdamoo.gta.common.action;

import com.artemis.ComponentMapper;

import de.lambdamoo.gta.world.GameWorld;
import de.lambdamoo.gta.world.components.Item;
import de.lambdamoo.gta.world.components.Player;
import de.lambdamoo.gta.world.components.Status;

public abstract class BaseItemEffect implements ItemEffect {

    private int duration = 0;

    protected Status getStatus(GameWorld mg) {
        ComponentMapper<Status> mStatus = mg.getComponentMapper(Status.class);
        return mStatus.get(mg.getLocalPlayerId());
    }

    protected Player getPlayer(GameWorld mg) {
        ComponentMapper<Player> mPlayer = mg.getComponentMapper(Player.class);
        return mPlayer.get(mg.getLocalPlayerId());
    }

    @Override
    public void recalcPower(Item item, GameWorld mg) {
    }

    @Override
    public void found(Item item, GameWorld mg) {
    }

    @Override
    public boolean unequip(Item item, GameWorld mg) {
        return true;
    }

    @Override
    public boolean use(Item item, GameWorld mg) {
        return false;
    }

    @Override
    public boolean equip(Item item, GameWorld mg) {
        return false;
    }
}
