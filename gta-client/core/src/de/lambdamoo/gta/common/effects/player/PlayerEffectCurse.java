package de.lambdamoo.gta.common.effects.player;

import com.artemis.ComponentMapper;

import de.lambdamoo.gta.world.GameWorld;
import de.lambdamoo.gta.world.components.Player;

public class PlayerEffectCurse extends PlayerEffect {
    private GameWorld gameWorld = null;

    public PlayerEffectCurse(GameWorld gameWorld) {
        super("curse", 60, null);
        this.gameWorld = gameWorld;
    }

    @Override
    public boolean applyEffect() {
        ComponentMapper<Player> mPlayer = gameWorld.getComponentMapper(Player.class);
        int playerId = gameWorld.getLocalPlayerId();
        Player player = mPlayer.get(playerId);
        player.luck = 0;
        gameWorld.addMessage("Your luck value drops to zero.");
        return true;
    }

    @Override
    public boolean removeEffect() {
        ComponentMapper<Player> mPlayer = gameWorld.getComponentMapper(Player.class);
        int playerId = gameWorld.getLocalPlayerId();
        Player player = mPlayer.get(playerId);
        player.luck = player.baseLuck;
        gameWorld.addMessage("Your luck value is restored.");
        return true;
    }
}
