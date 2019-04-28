package de.lambdamoo.gta.common.action;

import de.lambdamoo.gta.world.GameWorld;
import de.lambdamoo.gta.world.components.Trap;

public interface TrapEffect {
    void trigger(Trap trap, GameWorld mg);
}
