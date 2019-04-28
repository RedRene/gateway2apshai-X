package de.lambdamoo.gta.world.components;

import com.artemis.Component;

public class Status extends Component {
    public int healthMax = 9;
    public int healthCurrent = 9;
    public int level = 1;
    public int levelPoints = 0;
    public int powerArmor = 0;
    public int powerWeapon = 0;
    public int powerHelm = 0;
    public int powerGauntlet = 0;
    public int powerShield = 0;
    public int powerWand = 0;
    public boolean canMove = true;

    public void heal(int hp) {
        healthCurrent += hp;
        if (healthCurrent > healthMax) {
            healthCurrent = healthMax;
        }
    }

}
