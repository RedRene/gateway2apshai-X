package de.lambdamoo.gta.common.effects.traps;

import de.lambdamoo.gta.common.action.BaseTrapEffect;
import de.lambdamoo.gta.world.GameWorld;
import de.lambdamoo.gta.world.components.Trap;

public class TeleportTrapEffect extends BaseTrapEffect {
    @Override
    public void trigger(Trap trap, GameWorld mg) {
        mg.addMessage("You run into a Teleport Trap");
        mg.teleportPlayerRandom();
    }
}
