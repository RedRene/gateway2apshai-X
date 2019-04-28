package de.lambdamoo.gta.world.systems.gaming;

import com.artemis.Aspect;
import com.artemis.BaseEntitySystem;
import com.artemis.ComponentMapper;
import com.artemis.utils.IntBag;

import de.lambdamoo.gta.world.components.MapObject;

/**
 * This class handles the groups of map game objects
 */
public class MapObjectsGroupManager extends BaseEntitySystem {

    private ComponentMapper<MapObject> mMapObject;
    private IntBag items = new IntBag();
    private IntBag monsters = new IntBag();
    private IntBag arrows = new IntBag();
    private int playerId = -1;

    public MapObjectsGroupManager() {
        super(Aspect.all(MapObject.class));
    }

    public int getPlayerId() {
        return playerId;
    }

    @Override
    protected void inserted(int entityId) {
        super.inserted(entityId);
        MapObject mapObj = mMapObject.get(entityId);
        switch (mapObj.type) {
            case Trap:
            case Treasure:
            case Item:
                items.add(entityId);
                break;
            case Monster:
                monsters.add(entityId);
                break;
            case Player:
                this.playerId = entityId;
                break;
            case Arrow:
                arrows.add(entityId);
                break;
        }
    }

    public IntBag getItems() {
        return items;
    }

    public IntBag getMonsters() {
        return monsters;
    }

    public IntBag getArrows() {
        return arrows;
    }

    @Override
    protected void removed(int entityId) {
        super.removed(entityId);
        items.removeValue(entityId);
        monsters.removeValue(entityId);
        arrows.removeValue(entityId);

        MapObject mapObj = mMapObject.get(entityId);
        if (mapObj != null) {
            if (mapObj.type.equals(MapObject.MapObjectType.Player)) {
                this.playerId = -1;
            }
        }
    }

    /**
     * do nothing
     */
    @Override
    protected void processSystem() {
    }
}
