package de.lambdamoo.gta.world.systems.gaming;

import com.artemis.Aspect;
import com.artemis.ComponentMapper;
import com.artemis.systems.IntervalIteratingSystem;
import com.artemis.utils.IntBag;
import com.badlogic.gdx.math.Rectangle;

import java.util.Iterator;
import java.util.Random;

import de.lambdamoo.gta.client.util.SoundManager;
import de.lambdamoo.gta.common.effects.player.PlayerEffect;
import de.lambdamoo.gta.world.GameWorld;
import de.lambdamoo.gta.world.STATUS;
import de.lambdamoo.gta.world.components.Attacking;
import de.lambdamoo.gta.world.components.Item;
import de.lambdamoo.gta.world.components.MapObject;
import de.lambdamoo.gta.world.components.Monster;
import de.lambdamoo.gta.world.components.Player;
import de.lambdamoo.gta.world.components.Position;
import de.lambdamoo.gta.world.components.Status;
import de.lambdamoo.gta.world.components.Trap;
import de.lambdamoo.gta.world.components.Treasure;
import de.lambdamoo.gta.world.components.Velocity;

public class CollisionSystem extends IntervalIteratingSystem {

    private ComponentMapper<MapObject> mMapObject;
    private ComponentMapper<Velocity> mv;
    private ComponentMapper<Position> mPosition;
    private ComponentMapper<Status> mStatus;
    private ComponentMapper<Attacking> am;
    private ComponentMapper<Monster> mMonster;
    private ComponentMapper<Player> mPlayer;
    private ComponentMapper<Treasure> tr;
    private ComponentMapper<Item> mItem;
    private ComponentMapper<Trap> mTrap;
    private Random random = new Random();
    private GameWorld gameWorld = null;
    private Rectangle tempRectangle = new Rectangle();

    public CollisionSystem(GameWorld gameWorld) {
        super(Aspect.all(Player.class), 100);
        this.gameWorld = gameWorld;
    }

    @Override
    protected boolean checkProcessing() {
        return gameWorld != null && gameWorld.getGameStatus() != null && gameWorld.getGameStatus().equals(STATUS.GAMING);
    }

    @Override
    protected void begin() {
        super.begin();
    }

    @Override
    protected void process(int entityId) {
        if (gameWorld.getGameStatus() != null && gameWorld.getGameStatus().equals(STATUS.GAMING)) {
            checkCollisionsItems(entityId, gameWorld.getGroupManager().getItems());
            checkCollisionsMonster(entityId, gameWorld.getGroupManager().getMonsters());
        }
    }

    protected void checkCollisionsItems(int playerId, IntBag entities) {
        Position heroPos = mPosition.get(playerId);
        Player player = mPlayer.get(playerId);
        Status status = mStatus.get(playerId);

        for (int i = 0; i < entities.size(); i++) {
            int itemId = entities.get(i);
            MapObject mapObj = mMapObject.get(itemId);
            if (mapObj.activated) {
                Position itemPos = mPosition.get(itemId);
                if (isCollision(heroPos.boundingBox, itemPos.boundingBox)) {
                    // player collide with item (treasure or item)
                    MapObject mapObject = mMapObject.get(itemId);
                    switch (mapObject.type) {
                        case Treasure:
                            Treasure treasure = tr.get(itemId);
                            player.addScore(treasure.score * gameWorld.getCurrentMapLevel());
                            status.levelPoints += treasure.quality + 1;
                            itemPos.currentRoom.removeItem(itemId);
                            addMessage("You pick up a " + treasure.qualityName + " " + treasure.name + ".");
                            gameWorld.getSoundManager().playSound(SoundManager.Sounds.Picktreasure);
                            world.delete(itemId);
                            break;
                        case Item:
                            Item item = mItem.get(itemId);
                            gameWorld.getActionSystem().playerItemAdd(item);
                            itemPos.currentRoom.removeItem(itemId);
                            if (item.effect != null) {
                                item.effect.found(item, gameWorld);
                            }
                            addMessage("You pick up a " + item.name + ".");
                            gameWorld.getSoundManager().playSound(SoundManager.Sounds.Pickitem);
                            world.delete(itemId);
                            break;
                        case Trap:
                            Trap trap = mTrap.get(itemId);
                            if (trap.effect != null) {
                                trap.effect.trigger(trap, gameWorld);
                            }
                            itemPos.currentRoom.removeTrap(itemId);
                            world.delete(itemId);
                            break;
                    }
                }
            }
        }
    }

    protected void addMessage(String msg) {
        gameWorld.addMessage(msg);
    }

    /**
     * Implementation of the Rectangle overlap algorithm:
     * x < r.x + r.width && x + width > r.x && y < r.y + r.height && y + height > r.y;
     * float x1 = p1.boundingBox.x + p1.xWorld;
     * float y1 = p1.boundingBox.y + p1.yWorld;
     * float x2 = p2.boundingBox.x + p2.xWorld;
     * float y2 = p2.boundingBox.y + p2.yWorld;
     * boolean inX = x1 < x2 + p2.boundingBox.width && x1 + p1.boundingBox.width > x2;
     * boolean inY = y1 < y2 + p2.boundingBox.height && y1 + p1.boundingBox.height > y2;
     * return inX && inY;
     *
     * @param p1
     * @param p2
     * @return
     */

    boolean isCollision(Rectangle p1, Rectangle p2) {
        return p1.overlaps(p2);
    }

    protected void checkCollisionsMonster(int playerId, IntBag entities) {
        Position playerPos = mPosition.get(playerId);
        Attacking playerAttack = am.get(playerId);

        for (int i = 0; i < entities.size(); i++) {
            int mobId = entities.get(i);
            Position mobPos = mPosition.get(mobId);

            // monster hits hero, check once per second
            Attacking attackMonster = am.get(mobId);
            MapObject mapObject = mMapObject.get(mobId);
            if (mapObject.activated && attackMonster.canAttack && gameWorld.isTickerSecond() && isCollision(playerPos.boundingBox, mobPos.boundingBox)) {
                fightMonsterToHero(playerId, mobId);
            }
            // hero hits monster, check every frame
            if (playerAttack.attacking && !playerAttack.processed) {
                Velocity velo = mv.get(playerId);
                if (isCollision(getBoundingBoxPlayerWeapon(velo, playerPos), mobPos.boundingBox)) {
                    fightHeroToMonster(playerId, mobId);
                    playerAttack.processed = true;
                }
            }
        }
    }

    private Rectangle getBoundingBoxPlayerWeapon(Velocity velo, Position heroPos) {
        switch (velo.heading) {
            case NORTH:
                tempRectangle.set(heroPos.xWorld + 2, heroPos.yWorld + 12, 10, 8);
                break;
            case SOUTH:
                tempRectangle.set(heroPos.xWorld + 2, heroPos.yWorld, 10, 8);
                break;
            case EAST:
                tempRectangle.set(heroPos.xWorld + 16, heroPos.yWorld + 4, 10, 8);
                break;
            case WEST:
                tempRectangle.set(heroPos.xWorld, heroPos.yWorld + 4, 10, 8);
                break;
        }
        return tempRectangle;
    }


    protected void fightHeroToMonster(int heroId, int monsterId) {
        Status playerStatus = mStatus.get(heroId);
        Player player = mPlayer.get(heroId);
        Status monsterStatus = mStatus.get(monsterId);
        Monster monster = mMonster.get(monsterId);
        Position monsterPos = mPosition.get(monsterId);

        int dmg = playerStatus.powerWeapon + (player.strength / 4) + playerStatus.powerWand - monsterStatus.powerArmor;
        if (dmg < 0) {
            dmg = 0;
        }
        if (monster.undead && player.inventory.holdsCross) {
            dmg += 3;
        }
        monsterStatus.healthCurrent -= dmg;

        if (monsterStatus.healthCurrent <= 0) {
            world.delete(monsterId);
            player.incKillCount();
            monsterPos.currentRoom.removeMonster(monsterId);
            addMessage("You hit " + monster.name + " for " + dmg + " damage and kill it.");
        } else {
            addMessage("You hit " + monster.name + " for " + dmg + " damage.");
        }
    }

    private void fightMonsterToHero(int playerId, int monsterId) {
        Player player = mPlayer.get(playerId);
        Status playerStatus = mStatus.get(playerId);
        Status monsterStatus = mStatus.get(monsterId);
        Monster monster = mMonster.get(monsterId);

        int rand = random.nextInt(30) + 1;
        if (rand > player.agility) {
            // hit
            int dmg = monsterStatus.powerWeapon - playerStatus.powerArmor - playerStatus.powerShield - playerStatus.powerWand;
            if (dmg < 0) {
                dmg = 0;
            }
            if (random.nextInt(30) + 1 < player.luck) {
                dmg = dmg / 2;
                addMessage("Your luck reduces the damage.");
            }
            // do nothing if one of the effects mitigates the damage to the player
            if (!applyEffects(player, monsterId, monsterStatus, dmg)) {
                // no effect, so monster can hit you
                addMessage(monster.name + " hits you for " + dmg + " damage.");
                playerStatus.healthCurrent = playerStatus.healthCurrent - dmg;
                if (playerStatus.healthCurrent < 0) {
                    addMessage("You died !");
                    gameWorld.setGameStatus(STATUS.DEAD);
                }
            }
        } else {
            addMessage(monster.name + " misses you.");
        }
    }

    /**
     * This method checks the current player effects and if necessary applies it
     *
     * @param player
     * @param monsterId
     * @param monsterStatus
     * @param damage
     * @return
     */
    private boolean applyEffects(Player player, int monsterId, Status monsterStatus, int damage) {
        boolean result = false;
        Iterator<PlayerEffect> iter = player.listEffects.iterator();
        while (iter.hasNext()) {
            PlayerEffect effect = iter.next();
            if (effect.spell != null) {
                switch (effect.spell) {
                    case Protect:
                        addMessage("You are protected.");
                        result = true;
                        break;
                    case Reflect:
                        monsterStatus.healthCurrent -= damage;
                        if (monsterStatus.healthCurrent <= 0) {
                            world.delete(monsterId);
                            player.incKillCount();
                            Position monsterPos = mPosition.get(monsterId);
                            monsterPos.currentRoom.removeMonster(monsterId);
                            addMessage("You reflect " + damage + " damage and kill it.");
                        } else {
                            addMessage("You reflect " + damage + " damage.");
                        }
                        result = true;
                        break;
                }
            }
        }
        return result;
    }
}
