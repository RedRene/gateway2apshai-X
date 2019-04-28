package de.lambdamoo.gta.world.systems;

import com.artemis.BaseSystem;
import com.artemis.ComponentMapper;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import de.lambdamoo.gta.client.util.MapUtils;
import de.lambdamoo.gta.client.util.SoundManager;
import de.lambdamoo.gta.common.dto.Door;
import de.lambdamoo.gta.common.dto.Room;
import de.lambdamoo.gta.common.dto.Tile;
import de.lambdamoo.gta.common.effects.items.SpellCast;
import de.lambdamoo.gta.common.effects.player.PlayerEffect;
import de.lambdamoo.gta.world.GameWorld;
import de.lambdamoo.gta.world.components.Arrow;
import de.lambdamoo.gta.world.components.Attacking;
import de.lambdamoo.gta.world.components.Item;
import de.lambdamoo.gta.world.components.MapObject;
import de.lambdamoo.gta.world.components.Player;
import de.lambdamoo.gta.world.components.Position;
import de.lambdamoo.gta.world.components.Render;
import de.lambdamoo.gta.world.components.Status;
import de.lambdamoo.gta.world.components.Velocity;

public class ActionSystem extends BaseSystem {
    /**
     * This class sorts the items by the type
     */
    private ComponentMapper<Velocity> mVelo;
    private ComponentMapper<Position> mPosition;
    private ComponentMapper<MapObject> mMapObj;
    private ComponentMapper<Player> mPlayer;
    private ComponentMapper<Arrow> mArrow;
    private ComponentMapper<Attacking> mAttacking;
    private ComponentMapper<Render> mRender;
    private ComponentMapper<Status> mStatus;
    private GameWorld gameWorld = null;

    public ActionSystem(GameWorld gameWorld) {
        super();
        this.gameWorld = gameWorld;
    }

    /**
     * This method uses the next heal item if the player has one in his inventory.
     */
    public void useHealItem() {
        int playerId = gameWorld.getLocalPlayerId();
        Player player = mPlayer.get(playerId);
        Status status = mStatus.get(playerId);
        if (status.healthCurrent < status.healthMax) {
            // do only if the player is injured
            Item selectHealItem = null;
            Iterator<Item> iter = player.inventory.getListInventory().iterator();
            while (iter.hasNext()) {
                Item item = iter.next();
                if (item.name.startsWith("Healing")) {
                    item.consumed = true;
                    selectHealItem = item;
                    break;
                }
            }
            if (selectHealItem != null) {
                player.inventory.removeItem(selectHealItem);
                status.heal(1000);
                playerRemoveEffect(SpellCast.Spell.Poison);
                gameWorld.addMessage("You use a " + selectHealItem.name + " and are healed.");
            }
        }
    }

    /**
     * This method removes the effect from the
     *
     * @param spell
     */
    public void playerRemoveEffect(SpellCast.Spell spell) {
        int playerId = gameWorld.getLocalPlayerId();
        Player player = mPlayer.get(playerId);
        List<PlayerEffect> toRemove = new ArrayList<PlayerEffect>(3);
        Iterator<PlayerEffect> iter = player.listEffects.iterator();
        while (iter.hasNext()) {
            PlayerEffect effect = iter.next();
            if (effect.spell != null && effect.spell.equals(spell)) {
                toRemove.add(effect);
            }
        }
        iter = toRemove.iterator();
        while (iter.hasNext()) {
            PlayerEffect effect = iter.next();
            player.removeEffect(effect);
            effect.removeEffect();
        }
    }

    /**
     * Do nothing
     */
    @Override
    protected void processSystem() {
    }

    public boolean startMovePlayer(Velocity.HeadingDirection direction) {
        boolean result = true;
        int playerId = gameWorld.getLocalPlayerId();
        Status status = mStatus.get(playerId);
        if (status.canMove) {
            Velocity velo = mVelo.get(playerId);
            velo.heading = direction;
            Attacking attack = mAttacking.get(playerId);
            attack.attacking = false;
            velo.updateVelocityByDirection();
        }
        return result;
    }

    /**
     * This method adds an effect to the player
     *
     * @param effect
     */
    public void playerAddEffect(PlayerEffect effect) {
        int playerId = gameWorld.getLocalPlayerId();
        Player player = mPlayer.get(playerId);
        player.addEffect(effect);
        effect.applyEffect();
    }

    public Attacking.AttackMode getAttackMode() {
        int playerId = gameWorld.getLocalPlayerId();
        if (playerId != -1) {
            Attacking attack = mAttacking.get(playerId);
            return attack.attackMode;
        } else {
            return null;
        }
    }

    public void setAttackMode(Attacking.AttackMode mode) {
        int playerId = gameWorld.getLocalPlayerId();
        if (playerId != -1) {
            Attacking attack = mAttacking.get(playerId);
            attack.attackMode = mode;
        }
    }

    public boolean playerHasArrows() {
        boolean result = false;
        int playerId = gameWorld.getLocalPlayerId();
        if (playerId != -1) {
            Player player = mPlayer.get(playerId);
            result = player.inventory.hasArrows();
        }
        return result;
    }

    public boolean playerHasBow() {
        boolean result = false;
        int playerId = gameWorld.getLocalPlayerId();
        if (playerId != -1) {
            Player player = mPlayer.get(playerId);
            result = player.inventory.hasBow();
        }
        return result;
    }

    /**
     * This method disarms the traps in the current room. The trap entities are removed from the world.
     */
    public void disarmTraps() {
        int playerId = gameWorld.getLocalPlayerId();
        Position playerPos = mPosition.get(playerId);
        int count = playerPos.currentRoom.removeTraps(world);
        gameWorld.addMessage("You disarmed " + count + " traps.");
    }

    /**
     * This method starts the attack of the player
     *
     * @return
     */
    public boolean startAttackPlayer() {
        int playerId = gameWorld.getLocalPlayerId();
        Attacking attack = mAttacking.get(playerId);
        attack.attacking = true;
        Velocity velo = mVelo.get(playerId);
        velo.velocityXPixel = 0;
        velo.velocityYPixel = 0;
        Render render = mRender.get(playerId);
        render.currentSpriteIndex = 0;

        if (attack.attackMode.equals(Attacking.AttackMode.Arrow)) {
            // player attacks with bow
            Player player = mPlayer.get(playerId);
            boolean canFire = player.inventory.hasBow && player.inventory.hasArrows();

            // fire max 3 arrows
            if (canFire && gameWorld.getGroupManager().getArrows().size() < 3) {
                Position playerPos = mPosition.get(playerId);
                player.inventory.decreaseArrows(1);
                attack.processed = true;
                // fire in direction of player heading
                int arrowId = world.create();
                MapObject mapObj = mMapObj.create(arrowId);
                mapObj.type = MapObject.MapObjectType.Arrow;
                Arrow arrow = mArrow.create(arrowId);
                arrow.arrowType = attack.attackMode;
                arrow.damage = 8;
                Position posArrow = mPosition.create(arrowId);
                posArrow.xWorld = playerPos.xWorld;
                posArrow.yWorld = playerPos.yWorld + 5;
                Velocity veloArrow = mVelo.create(arrowId);
                veloArrow.heading = velo.heading;
                veloArrow.moveSpeed = 100;
                switch (veloArrow.heading) {
                    case NORTH:
                        posArrow.xWorld += 10;
                        break;
                    case SOUTH:
                        posArrow.yWorld -= 10;
                        break;
                }
                veloArrow.updateVelocityByDirection();
            }
        } else {
            // Sword attack
            attack.processed = false;
            gameWorld.getSoundManager().playRandomAttackSound();
        }
        return true;
    }

    /**
     * This method unequipes the items in the inventory of the same type. Use it, when equip an item is exclusive.
     *
     * @param typename type of exclusive item
     */
    private void unequippByType(String typename) {
        int playerId = gameWorld.getLocalPlayerId();
        Player player = mPlayer.get(playerId);

        List<Item> toRemove = new ArrayList<Item>();
        for (int i = 0; i < player.inventory.listEquiped.size(); i++) {
            Item item = player.inventory.listEquiped.get(i);
            if (item.typeName.equals(typename)) {
                toRemove.add(item);
            }
        }
        player.inventory.listEquiped.removeAll(toRemove);
    }

    public List<Item> getPlayerInventory() {
        int playerId = gameWorld.getLocalPlayerId();
        Player player = mPlayer.get(playerId);

        return player.inventory.getListInventory();
    }

    public List<Item> getPlayerEquipedItems() {
        int playerId = gameWorld.getLocalPlayerId();
        Player player = mPlayer.get(playerId);

        return player.inventory.listEquiped;
    }


    /**
     * The player uses this item. Afterwards when consumed it is removed from inventory
     *
     * @param item
     */
    public void playerItemUse(Item item) {
        if (item.usable) {
            item.effect.use(item, gameWorld);
            int playerId = gameWorld.getLocalPlayerId();
            Player player = mPlayer.get(playerId);
            player.inventory.removeItem(item);
            playerRecallEquippedItemPower();
        }
    }

    public void playerRecallEquippedItemPower() {
        int playerId = gameWorld.getLocalPlayerId();
        if (playerId != -1) {
            Status status = mStatus.get(playerId);
            status.powerArmor = 0;
            status.powerWeapon = 0;
            status.powerGauntlet = 0;
            status.powerHelm = 0;
            status.powerShield = 0;

            Player player = mPlayer.get(playerId);
            for (int i = 0; i < player.inventory.listEquiped.size(); i++) {
                Item item = player.inventory.listEquiped.get(i);
                if (item.effect != null) {
                    item.effect.recalcPower(item, gameWorld);
                }
            }
        }
    }

    public void playerItemAdd(Item item) {
        int playerId = gameWorld.getLocalPlayerId();
        Player player = mPlayer.get(playerId);
        player.inventory.addItem(item);
    }

    /**
     * This method drops an item from the inventory of the player
     *
     * @param item
     */
    public void playerItemDrop(Item item) {
        int playerId = gameWorld.getLocalPlayerId();
        Player player = mPlayer.get(playerId);
        player.inventory.removeItem(item);
        player.inventory.listEquiped.remove(item);
        playerRecallEquippedItemPower();
    }

    /**
     * This method equips an item. If an item from the same itemgroup is equiped, then the old one is unequiped.
     *
     * @param item
     * @return true, if other items were unequiped
     */
    public boolean playerItemEquip(Item item) {
        boolean result = false;
        if (item.isEquippable) {
            int playerId = gameWorld.getLocalPlayerId();
            Player player = mPlayer.get(playerId);

            if (item.itemGroup != null) {
                //unequip items of same group
                Iterator iter = player.inventory.listEquiped.iterator();
                while (iter.hasNext()) {
                    Item oldItem = (Item) iter.next();
                    if (oldItem.itemGroup != null && oldItem.itemGroup.equals(item.itemGroup)) {
                        oldItem.effect.unequip(item, gameWorld);
                        iter.remove();
                        result = true;
                    }
                }
            }
            player.inventory.listEquiped.add(item);
            if (item.effect != null) {
                item.effect.equip(item, gameWorld);
            }
            playerRecallEquippedItemPower();
        }
        return result;
    }

    /**
     * This method detects the secret doors in the current room.
     */
    public void searchSecretDoors() {
        // SoundManager.getInstance().playSound(SoundManager.SOUND_SECRET_DOOR_DETECT);
        int playerId = gameWorld.getLocalPlayerId();
        Position pos = mPosition.get(playerId);


        Room r = pos.currentRoom;
        boolean found = findSecretDoor(r.getExitNorth());
        found = found || findSecretDoor(r.getExitSouth());
        found = found || findSecretDoor(r.getExitWest());
        found = found || findSecretDoor(r.getExitEast());
        if (found) {
            gameWorld.getSoundManager().playSound(SoundManager.Sounds.Secretdoor_found);
            gameWorld.addMessage("You found a secret door");
        } else {
            gameWorld.getSoundManager().playSound(SoundManager.Sounds.Secretdoor_detect);
        }
    }

    /**
     * Find one secret door
     *
     * @param door
     */
    private boolean findSecretDoor(Door door) {
        boolean result = false;
        if (door != null && door.getDoorType() == Tile.CELL_HIDDENDOOR) {
            door.setDoorType(Tile.CELL_DOOR);
            MapUtils.writeDoor(gameWorld.getMap(), door);
            result = true;
        }
        return result;
    }

    /**
     * With this method can the player opens all the doors in the current room
     */
    public void openDoor() {
        int playerId = gameWorld.getLocalPlayerId();
        Position pos = mPosition.get(playerId);
        Room r = pos.currentRoom;
        gameWorld.getSoundManager().playSound(SoundManager.Sounds.Opendoor);
        boolean found = openDoor(r.getExitNorth());
        found = found || openDoor(r.getExitSouth());
        found = found || openDoor(r.getExitWest());
        found = found || openDoor(r.getExitEast());
        if (found) {
            gameWorld.addMessage("You opened a door.");
        }
    }

    /**
     * Open one door
     *
     * @param door
     */
    private boolean openDoor(Door door) {
        boolean result = false;
        if (door != null && door.getDoorType() == Tile.CELL_DOOR) {
            door.setDoorType(Tile.CELL_DOOR_OPENED);
            MapUtils.writeDoor(gameWorld.getMap(), door);
            result = true;
        }
        return result;
    }

    /**
     * With this method can the player locate the trap in the current room
     */
    public void locateTraps() {
        int playerId = gameWorld.getLocalPlayerId();
        Position pos = mPosition.get(playerId);
        Room r = pos.currentRoom;
        int trapCount = r.locateTraps(world);
        if (trapCount > 1) {
            gameWorld.addMessage("You locate " + trapCount + " traps");
        } else if (trapCount == 1) {
            gameWorld.addMessage("You locate one trap");
        }
        gameWorld.getSoundManager().playSound(SoundManager.Sounds.Trap_detect);
    }

    /**
     * The player movement is stopped at next frame
     */
    public void stopMovePlayer() {
        int playerId = gameWorld.getLocalPlayerId();
        Velocity velo = mVelo.get(playerId);
        velo.velocityXPixel = 0;
        velo.velocityYPixel = 0;

        Render render = mRender.get(playerId);
        render.currentSpriteIndex = 0;
    }
}
