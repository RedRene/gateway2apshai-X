package de.lambdamoo.gta.world.systems.util;

import com.artemis.ComponentMapper;
import com.artemis.World;

import de.lambdamoo.gta.world.components.Attacking;
import de.lambdamoo.gta.world.components.MapObject;
import de.lambdamoo.gta.world.components.Player;
import de.lambdamoo.gta.world.components.Position;
import de.lambdamoo.gta.world.components.Render;
import de.lambdamoo.gta.world.components.Status;
import de.lambdamoo.gta.world.components.Velocity;

public class EntityHelper {

    public static int createPlayer(World world) {
        int e = world.create();

        ComponentMapper<Position> mPosition = world.getMapper(Position.class);
        ComponentMapper<Render> mRender = world.getMapper(Render.class);
        ComponentMapper<Velocity> mVelocity = world.getMapper(Velocity.class);
        ComponentMapper<Attacking> mAttack = world.getMapper(Attacking.class);
        ComponentMapper<Player> mPlayer = world.getMapper(Player.class);
        ComponentMapper<Status> mStatus = world.getMapper(Status.class);
        ComponentMapper<MapObject> mMapObject = world.getMapper(MapObject.class);


        Position position = mPosition.create(e);

        Render render = mRender.create(e);
        render.spriteIndex = 0;
        render.maxSpriteIndex = 4;

        Velocity velocity = mVelocity.create(e);
        velocity.velocityXPixel = 0;
        velocity.velocityYPixel = 0;
        velocity.moveSpeed = 50;
        velocity.heading = Velocity.HeadingDirection.EAST;

        Attacking attack = mAttack.create(e);
        attack.maxAttackCycle = 5;
        attack.currentAttackCycle = 0;

        Status status = mStatus.create(e);
        Player player = mPlayer.create(e);
        MapObject mapObj = mMapObject.create(e);
        mapObj.activated = true;
        mapObj.type = MapObject.MapObjectType.Player;

        return e;
    }
}
