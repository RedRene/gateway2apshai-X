package de.lambdamoo.gta.world.systems.gaming;

import com.artemis.Aspect;
import com.artemis.systems.IntervalEntitySystem;

import de.lambdamoo.gta.world.GameWorld;
import de.lambdamoo.gta.world.components.Velocity;

public class TurnSystem extends IntervalEntitySystem {
    private GameWorld gameWorld = null;

    public TurnSystem(GameWorld gWorld, float interval) {
        super(Aspect.all(Velocity.class), interval);
        this.gameWorld = gWorld;
    }

    @Override
    protected void processSystem() {

    }
}
