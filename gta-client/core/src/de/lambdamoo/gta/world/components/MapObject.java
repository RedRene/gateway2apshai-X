package de.lambdamoo.gta.world.components;

import com.artemis.Component;

public class MapObject extends Component {
    public boolean activated = false;
    public MapObjectType type = null;

    public enum MapObjectType {
        Item, Monster, Treasure, Player, Trap, Arrow
    }
}
