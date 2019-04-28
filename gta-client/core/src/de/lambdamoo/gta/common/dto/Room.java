package de.lambdamoo.gta.common.dto;

import com.artemis.ComponentMapper;
import com.artemis.World;
import com.artemis.utils.IntBag;

import de.lambdamoo.gta.world.components.MapObject;
import de.lambdamoo.gta.world.components.Monster;
import de.lambdamoo.gta.world.components.Trap;

public class Room {
    public final static int VAL_NULL = -1;
    private IntBag listMonster = new IntBag();
    private IntBag listItems = new IntBag();
    private IntBag listTraps = new IntBag();
    private int roomNumber = VAL_NULL;
    private int tileCountY = VAL_NULL;
    private int tileCountX = VAL_NULL;
    private int tileX = VAL_NULL;
    private int tileY = VAL_NULL;
    private boolean visible = false;
    private boolean activated = false;
    private Door exitNorth = null;
    private Door exitWest = null;
    private Door exitEast = null;
    private Door exitSouth = null;

    public boolean hasMonster() {
        return listMonster.size() > 0;
    }

    public void addMonster(int mobId) {
        this.listMonster.add(mobId);
    }

    public void addTrap(int trapId) {
        this.listTraps.add(trapId);
    }

    public void removeMonster(int mobId) {
        this.listMonster.removeValue(mobId);
    }

    public void addItem(int itemId) {
        this.listItems.add(itemId);
    }

    public void removeItem(int itemId) {
        this.listItems.removeValue(itemId);
    }

    public void removeTrap(int trapId) {
        this.listTraps.removeValue(trapId);
    }

    public boolean isVisible() {
        return visible;
    }

    public void setVisible(boolean visible) {
        this.visible = visible;
    }

    public boolean isActivated() {
        return activated;
    }

    protected boolean isInRoom(int tileX, int tileY) {
        boolean result = tileX >= getTileX() && tileX < getTileX() + getTileCountX();
        result = result && tileY >= getTileY() && tileY < getTileY() + getTileCountY();
        return result;
    }

    public int getTileCountY() {
        return tileCountY;
    }

    public void setTileCountY(int height) {
        this.tileCountY = height;
    }

    public int getTileCountX() {
        return tileCountX;
    }

    public void setTileCountX(int width) {
        this.tileCountX = width;
    }

    public int getTileX() {
        return tileX;
    }

    public void setTileX(int locationX) {
        this.tileX = locationX;
    }

    public int getTileY() {
        return tileY;
    }

    public void setTileY(int locationY) {
        this.tileY = locationY;
    }


    public int locateTraps(World world) {
        int result = 0;
        ComponentMapper<Trap> mTrap = world.getMapper(Trap.class);
        for (int i = 0; i < listTraps.size(); i++) {
            int trapId = listTraps.get(i);
            Trap trap = mTrap.get(trapId);
            if (trap != null) {
                trap.visible = true;
                result++;
            }
        }
        return result;
    }

    /**
     * This method removes the traps of this room from this world
     *
     * @param world
     */
    public int removeTraps(World world) {
        int result = listTraps.size();
        for (int i = 0; i < listTraps.size(); i++) {
            int trapId = listTraps.get(i);
            world.delete(trapId);
        }
        listTraps.clear();
        return result;
    }

    /**
     * This method activates the mobs, items and treasure in the room
     */
    public void activateRoom(World world, boolean monsters, boolean treasure) {
        ComponentMapper<MapObject> mMapObject = world.getMapper(MapObject.class);
        if (monsters) {
            for (int i = 0; i < listMonster.size(); i++) {
                MapObject mapObj = mMapObject.get(listMonster.get(i));
                if (mapObj != null) {
                    mapObj.activated = true;
                }
            }
        }
        if (treasure) {
            for (int i = 0; i < listItems.size(); i++) {
                MapObject mapObj = mMapObject.get(listItems.get(i));
                if (mapObj != null) {
                    mapObj.activated = true;
                }
            }
            for (int i = 0; i < listTraps.size(); i++) {
                MapObject mapObj = mMapObject.get(listTraps.get(i));
                if (mapObj != null) {
                    mapObj.activated = true;
                }
            }
        }
        if (monsters && treasure) {
            activated = true;
        }
    }

    /**
     * This method returns the name of the first monster in this room.
     *
     * @param world
     * @return
     */
    public String getFirstMonsterName(World world) {
        String result = null;
        if (listMonster.size() > 0) {
            int entityId = listMonster.get(0);
            ComponentMapper<Monster> mMonster = world.getMapper(Monster.class);
            Monster monster = mMonster.get(entityId);
            if (monster != null) {
                result = monster.name;
            }
        }
        return result;
    }

    public Door getExitNorth() {
        return exitNorth;
    }

    public void setExitNorth(Door exitNorth) {
        this.exitNorth = exitNorth;
    }

    public Door getExitWest() {
        return exitWest;
    }

    public void setExitWest(Door exitWest) {
        this.exitWest = exitWest;
    }

    public Door getExitEast() {
        return exitEast;
    }

    public void setExitEast(Door exitEast) {
        this.exitEast = exitEast;
    }

    public Door getExitSouth() {
        return exitSouth;
    }

    public void setExitSouth(Door exitSouth) {
        this.exitSouth = exitSouth;
    }

    public int getRoomNumber() {
        return roomNumber;
    }

    public void setRoomNumber(int roomNumber) {
        this.roomNumber = roomNumber;
    }

}
