package de.lambdamoo.gta.world.components;

import com.artemis.Component;

public class Attacking extends Component {
    public boolean canAttack = true;
    public boolean attacking = false;
    public int currentAttackCycle = 0;
    public int maxAttackCycle = 0;
    public AttackMode attackMode = AttackMode.Sword;
    /**
     * Only process one attack per swing. This is the set to true, when the first collision check is done.
     */
    public boolean processed = false;

    ;

    public Attacking() {
    }

    /**
     * This method increases the attack cycle. The cycle starts with 0.
     */
    public void incAttackCycle() {
        this.currentAttackCycle++;
        if (!(this.currentAttackCycle < this.maxAttackCycle)) {
            this.currentAttackCycle = 0;
            this.attacking = false;
            this.processed = false;
        }
    }


    public enum AttackMode {
        Arrow, Sword
    }

}
