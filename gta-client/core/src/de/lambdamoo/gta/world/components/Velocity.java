package de.lambdamoo.gta.world.components;

import com.artemis.Component;

public class Velocity extends Component {
    public float moveSpeed = 0;
    public float velocityXPixel = 0;
    public float velocityYPixel = 0;
    public HeadingDirection heading = null;

    public boolean isMoving() {
        return velocityXPixel != 0 || velocityYPixel != 0;
    }

    public void updateVelocityByDirection() {
        switch (heading) {
            case NORTH:
                velocityXPixel = 0;
                velocityYPixel = moveSpeed;
                break;
            case SOUTH:
                velocityXPixel = 0;
                velocityYPixel = -moveSpeed;
                break;
            case WEST:
                velocityXPixel = -moveSpeed;
                velocityYPixel = 0;
                break;
            case EAST:
                velocityXPixel = moveSpeed;
                velocityYPixel = 0;
                break;
        }
    }

    public enum HeadingDirection {
        NORTH, EAST, SOUTH, WEST
    }
}
