package de.lambdamoo.gta.world.systems;

import com.artemis.Aspect;
import com.artemis.BaseEntitySystem;
import com.artemis.ComponentMapper;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import de.lambdamoo.gta.common.effects.player.PlayerEffect;
import de.lambdamoo.gta.world.GameWorld;
import de.lambdamoo.gta.world.STATUS;
import de.lambdamoo.gta.world.components.Player;


public class PostProcessSystem extends BaseEntitySystem {
    ComponentMapper<Player> mPlayer;
    private GameWorld gameWorld = null;

    public PostProcessSystem(GameWorld gameWorld) {
        super(Aspect.all(Player.class));
        this.gameWorld = gameWorld;
    }

    @Override
    protected void processSystem() {
        int playerId = gameWorld.getLocalPlayerId();
        if (playerId != -1) {
            // time
            Player player = mPlayer.get(playerId);
            player.decreaseTime(world.getDelta());
            if (player.time < 0) {
                // level ended
                gameWorld.setGameStatus(STATUS.NEXTLEVEL);
                return;
            }
            // recalc if inventory has changed
            if (player.inventory.inventoryHasChanged) {
                gameWorld.getActionSystem().playerRecallEquippedItemPower();
                player.inventory.inventoryHasChanged = false;
            }
            // check the effects and remove it when expired
            List<PlayerEffect> toRemove = new ArrayList<PlayerEffect>(3);
            Iterator<PlayerEffect> iter = player.listEffects.iterator();
            while (iter.hasNext()) {
                PlayerEffect effect = iter.next();
                effect.reduceDurationLeft(world.getDelta());
                if (effect.isExpired()) {
                    toRemove.add(effect);
                }
            }
            iter = toRemove.iterator();
            while (iter.hasNext()) {
                PlayerEffect effect = iter.next();
                player.removeEffect(effect);
                effect.removeEffect();
            }
        }
    }
}
