package de.lambdamoo.gta.world.components;

import com.artemis.Component;

public class Monster extends Component {
    public boolean undead = false;
    public AI ai = AI.Static;
    public String name = null;

    public enum AI {Walk, Confuse, Berzerk, Static, Teleport, Fear, Randomly}
}
