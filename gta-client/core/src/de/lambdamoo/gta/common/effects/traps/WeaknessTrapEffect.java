package de.lambdamoo.gta.common.effects.traps;

import de.lambdamoo.gta.common.action.BaseTrapEffect;
import de.lambdamoo.gta.world.GameWorld;
import de.lambdamoo.gta.world.components.Status;
import de.lambdamoo.gta.world.components.Trap;

public class WeaknessTrapEffect extends BaseTrapEffect {
    @Override
    public void trigger(Trap trap, GameWorld mg) {
        mg.addMessage("You are hit by Weakness Trap");
        Status status = getStatus(mg);
        status.healthCurrent = status.healthCurrent / 2;
    }
}
