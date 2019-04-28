package de.lambdamoo.gta.common.effects.traps;

import de.lambdamoo.gta.common.action.BaseTrapEffect;
import de.lambdamoo.gta.common.effects.player.PlayerEffectPoison;
import de.lambdamoo.gta.world.GameWorld;
import de.lambdamoo.gta.world.components.Trap;

public class PoisonTrapEffect extends BaseTrapEffect {
    @Override
    public void trigger(Trap trap, GameWorld mg) {
        mg.addMessage("You are hit by a poison needle");
        mg.getActionSystem().playerAddEffect(new PlayerEffectPoison(mg));
    }
}
