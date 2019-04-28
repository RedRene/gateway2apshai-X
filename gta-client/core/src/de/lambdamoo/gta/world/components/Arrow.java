package de.lambdamoo.gta.world.components;

import com.artemis.Component;

import de.lambdamoo.gta.world.components.Attacking.AttackMode;

public class Arrow extends Component {
    public AttackMode arrowType = null;
    public int damage = 0;

    public Arrow() {
    }

}
