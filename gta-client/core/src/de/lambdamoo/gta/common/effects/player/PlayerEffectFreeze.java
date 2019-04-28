package de.lambdamoo.gta.common.effects.player;

import com.artemis.ComponentMapper;

import de.lambdamoo.gta.world.GameWorld;
import de.lambdamoo.gta.world.components.Status;
import de.lambdamoo.gta.world.components.Velocity;

public class PlayerEffectFreeze extends PlayerEffect {
    private GameWorld gameWorld = null;

    public PlayerEffectFreeze(GameWorld gameWorld) {
        super("freeze", 5, null);
        this.gameWorld = gameWorld;
    }

    @Override
    public boolean applyEffect() {
        ComponentMapper<Status> mStatus = gameWorld.getComponentMapper(Status.class);
        Status status = mStatus.get(gameWorld.getLocalPlayerId());
        status.canMove = false;
        return true;
    }

    @Override
    public boolean removeEffect() {
        int playerId = gameWorld.getLocalPlayerId();
        ComponentMapper<Status> mStatus = gameWorld.getComponentMapper(Status.class);
        ComponentMapper<Velocity> mVelo = gameWorld.getComponentMapper(Velocity.class);
        Status status = mStatus.get(playerId);
        status.canMove = true;
        Velocity velo = mVelo.get(playerId);
        velo.velocityXPixel = 0;
        velo.velocityYPixel = 0;
        return true;
    }
}
