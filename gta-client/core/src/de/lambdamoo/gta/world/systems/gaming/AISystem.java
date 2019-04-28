package de.lambdamoo.gta.world.systems.gaming;

import com.artemis.Aspect;
import com.artemis.ComponentMapper;
import com.artemis.systems.IntervalIteratingSystem;

import java.util.Random;

import de.lambdamoo.gta.world.GameWorld;
import de.lambdamoo.gta.world.STATUS;
import de.lambdamoo.gta.world.components.Attacking;
import de.lambdamoo.gta.world.components.MapObject;
import de.lambdamoo.gta.world.components.Monster;
import de.lambdamoo.gta.world.components.Player;
import de.lambdamoo.gta.world.components.Position;
import de.lambdamoo.gta.world.components.Velocity;

public class AISystem extends IntervalIteratingSystem {
    private Random rand = new Random();
    private ComponentMapper<Position> mPosition;
    private ComponentMapper<Attacking> mAttack;
    private ComponentMapper<Monster> mMonster;
    private ComponentMapper<Velocity> mVelo;
    private ComponentMapper<Player> mPlayer;
    private GameWorld gameWorld = null;
    private ComponentMapper<MapObject> mMapObject;

    public AISystem(GameWorld gameWorld) {
        super(Aspect.all(Monster.class, Attacking.class), 100);
        this.gameWorld = gameWorld;
    }

    @Override
    protected boolean checkProcessing() {
        return gameWorld.getGameStatus() != null && gameWorld.getGameStatus().equals(STATUS.GAMING);
    }

    /**
     * only do this method one time per second
     */
    @Override
    protected void process(int entityId) {
        MapObject mapObj = mMapObject.get(entityId);
        if (mapObj.activated) {
            int playerId = gameWorld.getLocalPlayerId();
            Position playerPos = mPosition.get(playerId);
            Position monsterPos = mPosition.get(entityId);
            Velocity monsterVelo = mVelo.get(entityId);
            Monster monster = mMonster.get(entityId);


            switch (monster.ai) {
                case Static:
                    monsterVelo.velocityXPixel = 0;
                    monsterVelo.velocityYPixel = 0;
                    break;
                case Confuse:
                    int r = rand.nextInt(10);
                    if (r < 3) {
                        walk(playerPos, monsterPos, monsterVelo);
                    } else {
                        randomly(playerPos, monsterPos, monsterVelo);
                    }
                    break;
                case Randomly:
                    randomly(playerPos, monsterPos, monsterVelo);
                    break;
                case Berzerk:
                case Walk:
                    // move toward player
                    walk(playerPos, monsterPos, monsterVelo);
                    break;
                case Teleport:
                    // walk and sometimes teleport
                    int rt = rand.nextInt(10);
                    if (rt < 3) {
                        walk(playerPos, monsterPos, monsterVelo);
                    }
                    break;
                case Fear:
                    // walk away from player
                    walk(playerPos, monsterPos, monsterVelo);
                    monsterVelo.velocityXPixel = monsterVelo.velocityXPixel * (-1);
                    monsterVelo.velocityYPixel = monsterVelo.velocityYPixel * (-1);
                    break;
            }
        }
    }

    private void walk(Position playerPos, Position monsterPos, Velocity monsterVelo) {
        float absX = playerPos.xWorld - monsterPos.xWorld;
        float absY = playerPos.yWorld - monsterPos.yWorld;
        if (Math.abs(absX) > Math.abs(absY)) {
            // move x axis
            if (monsterPos.xWorld < playerPos.xWorld) {
                monsterVelo.velocityXPixel = monsterVelo.moveSpeed;
            } else {
                monsterVelo.velocityXPixel = -monsterVelo.moveSpeed;
            }
            monsterVelo.velocityYPixel = 0;
        } else {
            // move y axis
            monsterVelo.velocityXPixel = 0;
            if (monsterPos.yWorld < playerPos.yWorld) {
                monsterVelo.velocityYPixel = monsterVelo.moveSpeed;
            } else {
                monsterVelo.velocityYPixel = -monsterVelo.moveSpeed;
            }
        }
    }

    private void randomly(Position playerPos, Position monsterPos, Velocity monsterVelo) {
        int randValue = rand.nextInt(4);
        switch (randValue) {
            case 0:
                monsterVelo.velocityXPixel = 0;
                monsterVelo.velocityYPixel = -monsterVelo.moveSpeed;
                break;
            case 1:
                monsterVelo.velocityXPixel = monsterVelo.moveSpeed;
                monsterVelo.velocityYPixel = 0;
                break;
            case 2:
                monsterVelo.velocityXPixel = 0;
                monsterVelo.velocityYPixel = monsterVelo.moveSpeed;
                break;
            case 3:
                monsterVelo.velocityXPixel = -monsterVelo.moveSpeed;
                monsterVelo.velocityYPixel = 0;
                break;
        }
    }

}
