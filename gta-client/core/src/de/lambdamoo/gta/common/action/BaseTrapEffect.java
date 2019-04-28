package de.lambdamoo.gta.common.action;

import com.artemis.ComponentMapper;

import de.lambdamoo.gta.world.GameWorld;
import de.lambdamoo.gta.world.components.Player;
import de.lambdamoo.gta.world.components.Status;

public abstract class BaseTrapEffect implements TrapEffect {
    protected Status getStatus(GameWorld mg) {
        ComponentMapper<Status> mStatus = mg.getComponentMapper(Status.class);
        return mStatus.get(mg.getLocalPlayerId());
    }

    protected Player getPlayer(GameWorld mg) {
        ComponentMapper<Player> mPlayer = mg.getComponentMapper(Player.class);
        return mPlayer.get(mg.getLocalPlayerId());
    }

    protected int calcDamage(Status status, int damage) {
        damage = damage - status.powerGauntlet + status.powerHelm;
        if (damage < 0) {
            damage = 0;
        }
        return damage;
    }
}
