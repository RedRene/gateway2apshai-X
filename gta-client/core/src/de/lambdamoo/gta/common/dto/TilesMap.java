package de.lambdamoo.gta.common.dto;

public class TilesMap {
    int level = -1;
    int dungeon = -1;
    private Tile[][] cells = null;
    private int width = 128;
    private int height = 64;
    private int tileWidthPixel = 8;
    private int tileHeightPixel = 8;
    private boolean changed = false;

    public TilesMap(int level, int dungeon, int tileCountWidth, int tileCountHeight) {
        super();
        this.level = level;
        this.dungeon = dungeon;
        this.setWidth(tileCountWidth);
        this.setHeight(tileCountHeight);
        cells = new Tile[width][height];
    }

    public boolean isChanged() {
        return changed;
    }

    private void setChanged(boolean changed) {
        this.changed = changed;
    }

    public void resetChanged() {
        this.changed = false;
    }

    public int getLevel() {
        return level;
    }

    public int getDungeon() {
        return dungeon;
    }

    public boolean canMove(int x, int y) {
        if (getTileTypeAt(x, y) == Tile.CELL_FLOOR) {
            return true;
        } else {
            return false;
        }
    }

    public int getTileTypeAt(int x, int y) {
        return (cells[x][y].getTileType());
    }

    public Room getRoomAtTile(double xWorldPixel, double yWorldPixel) {
        int x = (int) xWorldPixel / tileWidthPixel;
        int y = (int) yWorldPixel / tileHeightPixel;
        return cells[x][y].getRoom();
    }

    public Tile getTileAt(int x, int y) {
        return cells[x][y];
    }

    public void setCell(int x, int y, Tile tile) {
        cells[x][y] = tile;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public void setTileTypeAt(int x, int y, int tileType) {
        cells[x][y].setTileType(tileType);
        cells[x][y].setChanged(true);
        setChanged(true);
    }

    /**
     * This method sets the room tiles to visible state
     *
     * @param room
     * @param visible
     */
    public void setVisible(Room room, boolean visible) {
        for (int y = 0; y < room.getTileCountY(); y++) {
            for (int x = 0; x < room.getTileCountX(); x++) {
                cells[x + room.getTileX()][y + room.getTileY()].setVisible(visible);
            }
        }
        room.setVisible(true);
        setChanged(true);
    }

    public void setTileAttrAt(int x, int y, int tileType, Room room) {
        Tile tile = cells[x][y];
        tile.setRoom(room);
        tile.setTileType(tileType);
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public int getTileWidthPixel() {
        return tileWidthPixel;
    }

    public void setTileWidthPixel(int tileWidth) {
        this.tileWidthPixel = tileWidth;
    }

    public int getTileHeightPixel() {
        return tileHeightPixel;
    }

    public void setTileHeightPixel(int tileHeight) {
        this.tileHeightPixel = tileHeight;
    }

    public int getTileX(double newX) {
        return (int) (newX / tileWidthPixel);
    }

    public int getTileY(double newY) {
        return (int) (newY / tileHeightPixel);
    }

}
