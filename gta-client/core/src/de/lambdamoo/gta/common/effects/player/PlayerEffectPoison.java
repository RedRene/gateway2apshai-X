package de.lambdamoo.gta.common.effects.player;

import com.artemis.ComponentMapper;

import de.lambdamoo.gta.common.effects.items.SpellCast;
import de.lambdamoo.gta.world.GameWorld;
import de.lambdamoo.gta.world.STATUS;
import de.lambdamoo.gta.world.components.Status;

public class PlayerEffectPoison extends PlayerEffect {
    private float timerSeconds = 5;
    private float durationLeftSeconds = 0;
    private GameWorld gameWorld = null;

    public PlayerEffectPoison(GameWorld gameWorld) {
        super("poison", -1, SpellCast.Spell.Poison);
        infinite = true;
        this.gameWorld = gameWorld;
    }

    @Override
    public boolean applyEffect() {
        this.durationLeftSeconds = timerSeconds;
        return true;
    }

    @Override
    public boolean removeEffect() {
        return true;
    }

    public void reduceDurationLeft(float time) {
        this.durationLeftSeconds -= time;
        if (this.durationLeftSeconds < 0) {
            // tick
            this.durationLeftSeconds = timerSeconds;
            ComponentMapper<Status> mStatus = gameWorld.getComponentMapper(Status.class);
            Status status = mStatus.get(gameWorld.getLocalPlayerId());
            status.healthCurrent--;
            if (status.healthCurrent < 0) {
                gameWorld.addMessage("You are killed by poison");
                gameWorld.setGameStatus(STATUS.DEAD);
            } else {
                gameWorld.addMessage("You are hit by poison");
            }
        }
    }
}
