package de.lambdamoo.gta.client.util;

import de.lambdamoo.gta.common.dto.Tile;
import de.lambdamoo.gta.common.dto.TilesMap;
import de.lambdamoo.gta.common.dto.Door;
import de.lambdamoo.gta.common.dto.Room;

public class MapUtils {

    /**
     * This method writes the room walls to the cell array
     *
     * @param room
     */
    static public void writeRoomCells(TilesMap map, Room room) {
        // draw walls
        int y = room.getTileY();
        for (int x = room.getTileX(); x < room.getTileX() + room.getTileCountX(); x++) {
            if (map == null || map.getTileAt(x, y) == null) {
                int fsdf = 1;
            }
            if (map.getTileTypeAt(x, y) == Tile.CELL_UNWALKABLE) {
                map.setTileAttrAt(x, y, Tile.CELL_WALL, room);
            }
            if (map.getTileTypeAt(x, y + room.getTileCountY() - 1) == Tile.CELL_UNWALKABLE) {
                map.setTileAttrAt(x, y + room.getTileCountY() - 1, Tile.CELL_WALL, room);
            }
        }
        int x = room.getTileX();
        for (y = room.getTileY(); y < room.getTileY() + room.getTileCountY(); y++) {
            if (map.getTileTypeAt(x, y) == Tile.CELL_UNWALKABLE) {
                map.setTileAttrAt(x, y, Tile.CELL_WALL, room);
            }
            if (map.getTileTypeAt(x + room.getTileCountX() - 1, y) == Tile.CELL_UNWALKABLE) {
                map.setTileAttrAt(x + room.getTileCountX() - 1, y, Tile.CELL_WALL, room);
            }
        }
        // draw floor
        for (x = room.getTileX() + 1; x < room.getTileX() + room.getTileCountX() - 1; x++) {
            for (y = room.getTileY() + 1; y < room.getTileY() + room.getTileCountY() - 1; y++) {
                map.setTileAttrAt(x, y, Tile.CELL_FLOOR, room);
            }
        }

        // draw exits to other rooms
        if (room.getExitNorth() != null) {
            writeDoor(map, room.getExitNorth());
        }
        if (room.getExitSouth() != null) {
            writeDoor(map, room.getExitSouth());
        }
        if (room.getExitEast() != null) {
            writeDoor(map, room.getExitEast());
        }
        if (room.getExitWest() != null) {
            writeDoor(map, room.getExitWest());
        }
    }

    public static void writeDoor(TilesMap map, Door door) {
        for (int y = 0; y < door.getTileCountY(); y++) {
            for (int x = 0; x < door.getTileCountX(); x++) {
                map.setTileTypeAt(x + door.getTileX(), y + door.getTileY(), door.getDoorType());
            }
        }
    }

}
