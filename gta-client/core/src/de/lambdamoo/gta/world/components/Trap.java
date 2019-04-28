package de.lambdamoo.gta.world.components;

import com.artemis.Component;

import de.lambdamoo.gta.common.action.TrapEffect;

public class Trap extends Component {
    public String name = null;
    public TrapEffect effect = null;
    public int level = -1;
    public boolean visible = false;
}
