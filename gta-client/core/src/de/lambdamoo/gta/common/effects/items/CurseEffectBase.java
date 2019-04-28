package de.lambdamoo.gta.common.effects.items;

import de.lambdamoo.gta.common.action.BaseItemEffect;
import de.lambdamoo.gta.common.effects.player.PlayerEffectCurse;
import de.lambdamoo.gta.world.GameWorld;
import de.lambdamoo.gta.world.components.Item;
import de.lambdamoo.gta.world.components.Status;

public class CurseEffectBase extends BaseItemEffect {
    @Override
    public boolean equip(Item item, GameWorld mg) {
        int curseCount = 1;
        int rand = mg.getRandom().nextInt(1000);
        if (rand > 750) {
            // 2 curses
            curseCount++;
        }
        if (rand > 953) {
            // 3 curses
            curseCount++;
        }
        if (rand > 998) {
            // 4 curses
            curseCount++;
        }
        boolean applyCurse = false;

        // apply curses
        for (int i = 0; i < curseCount; i++) {
            rand = mg.getRandom().nextInt() % 4;
            Status status = getStatus(mg);
            switch (rand) {
                case 0:
                    // Half health
                    int health = status.healthCurrent / 2;
                    status.healthCurrent = health;
                    break;
                case 1:
                case 2:
                case 3:
                    applyCurse = true;
                    break;
            }
        }
        if (applyCurse) {
            // start PlayerEffectCurse with luck = 0 only once
            mg.getActionSystem().playerAddEffect(new PlayerEffectCurse(mg));

        }
        mg.addMessage("You equipped a cursed weapon.");
        return true;
    }
}