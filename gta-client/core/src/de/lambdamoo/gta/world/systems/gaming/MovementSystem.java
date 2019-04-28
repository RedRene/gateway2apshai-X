package de.lambdamoo.gta.world.systems.gaming;

import com.artemis.Aspect;
import com.artemis.ComponentMapper;
import com.artemis.systems.IteratingSystem;

import de.lambdamoo.gta.client.util.SoundManager;
import de.lambdamoo.gta.common.dto.Room;
import de.lambdamoo.gta.common.dto.Tile;
import de.lambdamoo.gta.common.dto.TilesMap;
import de.lambdamoo.gta.world.GameWorld;
import de.lambdamoo.gta.world.STATUS;
import de.lambdamoo.gta.world.components.MapObject;
import de.lambdamoo.gta.world.components.Player;
import de.lambdamoo.gta.world.components.Position;
import de.lambdamoo.gta.world.components.Velocity;

/**
 * This class is for pixelMovement of the Movement components
 */
public class MovementSystem extends IteratingSystem {
    private ComponentMapper<Position> mPosition;
    private ComponentMapper<Velocity> mVelo;
    private ComponentMapper<MapObject> mMapObject;
    private ComponentMapper<Player> mPlayer;
    private GameWorld gameWorld = null;

    public MovementSystem(GameWorld gameWorld) {
        super(Aspect.all(Position.class, Velocity.class, MapObject.class));
        this.gameWorld = gameWorld;
    }

    @Override
    protected void begin() {
        super.begin();
    }


    @Override
    protected void process(int entId) {
        boolean move = false;
        MapObject mapObj = mMapObject.get(entId);
        if (mapObj.activated) {
            Position position = mPosition.get(entId);
            Velocity velocity = mVelo.get(entId);
            TilesMap map = gameWorld.getMap();

            if (velocity.velocityXPixel != 0 || velocity.velocityYPixel != 0) {
                float newWorldX = position.xWorld + velocity.velocityXPixel * world.delta;
                float newWorldY = position.yWorld + velocity.velocityYPixel * world.delta;
                if (newWorldX < 0) {
                    newWorldX = 0;
                } else if (newWorldX > map.getTileWidthPixel() * map.getWidth()) {
                    newWorldX = map.getTileWidthPixel() * map.getWidth();
                }
                if (newWorldY < 0) {
                    newWorldY = 0;
                } else if (newWorldY > map.getTileHeightPixel() * map.getHeight()) {
                    newWorldY = map.getTileHeightPixel() * map.getHeight();
                }
                int newTileX = map.getTileX((int) newWorldX + 8);
                int newTileY = map.getTileY((int) newWorldY + 8);
                int newCell = map.getTileTypeAt(newTileX, newTileY);

                if (newCell == Tile.CELL_FLOOR) {
                    move = true;
                }
                if (newCell == Tile.CELL_DOOR_OPENED) {
                    // paint new room if not already visible
                    checkDoor(map, position.currentRoom, newTileX, newTileY);
                    move = true;
                }
                if (newCell == Tile.CELL_DOOR) {
                    // must open door first
                    // checkDoor(map, position.currentRoom, newTileX, newTileY);
                    move = false;
                }
                if (move) {
                    position.xWorld = newWorldX;
                    position.yWorld = newWorldY;
                    position.currentRoom = map.getTileAt(newTileX, newTileY).getRoom();
                    position.updateBoundingBox();
                }
                //MyLogger.getInstance().log("MovementSystem", newTileX + "/" + newTileY + " in room nr. " + position.currentRoom.getRoomNumber());
            }
        }
    }

    protected void checkDoor(TilesMap map, Room currentRoom, int tileX, int tileY) {
        Room otherRoom = null;
        if (currentRoom.getExitNorth() != null) {
            Room other = currentRoom.getExitNorth().getOtherExit(currentRoom);
            if (isInRoom(other, tileX, tileY)) {
                otherRoom = other;
                currentRoom.getExitNorth().setDoorType(Tile.CELL_DOOR_OPENED);
            }
        }
        if (currentRoom.getExitEast() != null) {
            Room other = currentRoom.getExitEast().getOtherExit(currentRoom);
            if (isInRoom(other, tileX, tileY)) {
                otherRoom = other;
                currentRoom.getExitEast().setDoorType(Tile.CELL_DOOR_OPENED);
            }
        }
        if (currentRoom.getExitSouth() != null) {
            Room other = currentRoom.getExitSouth().getOtherExit(currentRoom);
            if (isInRoom(other, tileX, tileY)) {
                otherRoom = other;
                currentRoom.getExitSouth().setDoorType(Tile.CELL_DOOR_OPENED);
            }
        }
        if (currentRoom.getExitWest() != null) {
            Room other = currentRoom.getExitWest().getOtherExit(currentRoom);
            if (isInRoom(other, tileX, tileY)) {
                otherRoom = other;
                currentRoom.getExitWest().setDoorType(Tile.CELL_DOOR_OPENED);
            }
        }
        if (otherRoom != null) {
            if (!otherRoom.isVisible()) {
                map.setVisible(otherRoom, true);
                otherRoom.setVisible(true);
            }
            if (!otherRoom.isActivated()) {
                otherRoom.activateRoom(world, true, true);
                if (otherRoom.hasMonster()) {
                    gameWorld.getSoundManager().playSound(SoundManager.Sounds.Encounter);
                    gameWorld.addMessage("Careful, you face a " + otherRoom.getFirstMonsterName(world));
                }
            }
        }
    }

    protected boolean isInRoom(Room r, int tileX, int tileY) {
        boolean result = tileX >= r.getTileX() && tileX < r.getTileX() + r.getTileCountX();
        result = result && tileY >= r.getTileY() && tileY < r.getTileY() + r.getTileCountY();
        return result;
    }

    @Override
    protected boolean checkProcessing() {
        return gameWorld != null && gameWorld.getGameStatus() != null && gameWorld.getGameStatus().equals(STATUS.GAMING);
    }
}
