package de.lambdamoo.gta.world.components;

import com.artemis.Component;
import com.badlogic.gdx.math.Rectangle;

import de.lambdamoo.gta.common.dto.Room;

/**
 * This is the component to store the x and y position of the object in world coordinates
 */
public class Position extends Component {
    public float xWorld;
    public float yWorld;
    public float widthWorld;
    public float heightWorld;
    public Room currentRoom = null;
    public Rectangle boundingBox = new Rectangle();

    public Position() {
        super();
    }

    public void setPositionTo(Position pos) {
        xWorld = pos.xWorld;
        yWorld = pos.yWorld;
        currentRoom = pos.currentRoom;
        widthWorld = pos.widthWorld;
        heightWorld = pos.heightWorld;
        updateBoundingBox();
    }

    public void updateBoundingBox() {
        boundingBox.set(xWorld, yWorld, widthWorld, heightWorld);
    }

}
