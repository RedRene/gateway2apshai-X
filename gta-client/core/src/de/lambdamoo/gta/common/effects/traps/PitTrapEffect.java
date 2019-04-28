package de.lambdamoo.gta.common.effects.traps;

import de.lambdamoo.gta.common.action.BaseTrapEffect;
import de.lambdamoo.gta.world.GameWorld;
import de.lambdamoo.gta.world.STATUS;
import de.lambdamoo.gta.world.components.Status;
import de.lambdamoo.gta.world.components.Trap;

public class PitTrapEffect extends BaseTrapEffect {
    @Override
    public void trigger(Trap trap, GameWorld mg) {
        Status status = getStatus(mg);
        int dmg = mg.getRandom().nextInt(3) + 1;
        dmg = calcDamage(status, dmg);
        mg.addMessage("You take " + dmg + " damage from the Pit Trap");
        status.healthCurrent -= dmg;
        if (status.healthCurrent < 0) {
            mg.addMessage("You died !");
            mg.setGameStatus(STATUS.DEAD);
        }
    }
}
