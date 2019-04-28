package de.lambdamoo.gta.common.effects.traps;

import de.lambdamoo.gta.common.action.BaseTrapEffect;
import de.lambdamoo.gta.common.effects.player.PlayerEffectFreeze;
import de.lambdamoo.gta.world.GameWorld;
import de.lambdamoo.gta.world.components.Trap;

public class FreezeTrapEffect extends BaseTrapEffect {

    @Override
    public void trigger(Trap trap, final GameWorld mg) {
        mg.getActionSystem().playerAddEffect(new PlayerEffectFreeze(mg));
        mg.addMessage("You run into a Freeze Trap");
    }
}
