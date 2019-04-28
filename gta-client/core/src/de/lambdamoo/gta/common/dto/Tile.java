package de.lambdamoo.gta.common.dto;

import de.lambdamoo.gta.common.dto.Room;

public class Tile {
    public final static int CELL_WALL = 1;
    public final static int CELL_DOOR = 2;
    public final static int CELL_HIDDENDOOR = 3;
    public final static int CELL_FLOOR = 4;
    public final static int CELL_UNWALKABLE = 5;
    public final static int CELL_DOOR_OPENED = 6;
    private Room room = null;
    private boolean visible = false;
    private int tileType = CELL_UNWALKABLE;
    private boolean changed = true;

    public Tile(Room room, int tileType) {
        super();
        this.room = room;
        this.tileType = tileType;
    }

    public boolean isChanged() {
        return changed;
    }

    public void setChanged(boolean changed) {
        this.changed = changed;
    }

    public Room getRoom() {
        return room;
    }

    public void setRoom(Room room) {
        this.room = room;
    }

    public boolean isVisible() {
        return visible;
    }

    public void setVisible(boolean visible) {
        if (this.visible != visible) {
            changed = true;
        }
        this.visible = visible;

    }

    public int getTileType() {
        return tileType;
    }

    public void setTileType(int tileType) {
        this.tileType = tileType;
    }
}
