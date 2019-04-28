package de.lambdamoo.gta.common.effects.traps;

import de.lambdamoo.gta.common.action.BaseTrapEffect;
import de.lambdamoo.gta.world.GameWorld;
import de.lambdamoo.gta.world.STATUS;
import de.lambdamoo.gta.world.components.Player;
import de.lambdamoo.gta.world.components.Trap;

public class DeathTrapEffect extends BaseTrapEffect {
    @Override
    public void trigger(Trap trap, GameWorld mg) {
        Player player = getPlayer(mg);
        int r = mg.getRandom().nextInt(31);
        if (player.luck < r) {
            // die
            mg.addMessage("You are killed by a Death Trap");
            mg.setGameStatus(STATUS.DEAD);
        } else {
            mg.addMessage("Your high luck prevents you from being killed by a Death Trap");
        }
    }
}
