package de.lambdamoo.gta.world.systems.gaming;

import com.artemis.Aspect;
import com.artemis.ComponentMapper;
import com.artemis.systems.IntervalIteratingSystem;
import com.artemis.utils.IntBag;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;

import java.util.Random;

import de.lambdamoo.gta.client.util.SpriteImages;
import de.lambdamoo.gta.common.dto.Tile;
import de.lambdamoo.gta.common.dto.TilesMap;
import de.lambdamoo.gta.world.GameWorld;
import de.lambdamoo.gta.world.STATUS;
import de.lambdamoo.gta.world.components.Arrow;
import de.lambdamoo.gta.world.components.Attacking;
import de.lambdamoo.gta.world.components.MapObject;
import de.lambdamoo.gta.world.components.Monster;
import de.lambdamoo.gta.world.components.Player;
import de.lambdamoo.gta.world.components.Position;
import de.lambdamoo.gta.world.components.Status;
import de.lambdamoo.gta.world.components.Velocity;

public class ArrowSystem extends IntervalIteratingSystem {
    private Random rand = new Random();
    private ComponentMapper<Position> mPosition;
    private ComponentMapper<Attacking> mAttack;
    private ComponentMapper<Monster> mMonster;
    private ComponentMapper<Velocity> mVelo;
    private ComponentMapper<Arrow> mArrow;
    private ComponentMapper<Player> mPlayer;
    private ComponentMapper<Status> mStatus;
    private GameWorld gameWorld = null;
    private ComponentMapper<MapObject> mMapObject;
    private SpriteImages spriteImages = null;
    private SpriteBatch batch;
    private OrthographicCamera linkedCamera;

    public ArrowSystem(GameWorld gameWorld, SpriteImages spriteImages) {
        super(Aspect.all(Arrow.class), 100);
        this.gameWorld = gameWorld;
        this.spriteImages = spriteImages;

        batch = new SpriteBatch();
    }

    public void linkCamera(OrthographicCamera cam) {
        this.linkedCamera = cam;
    }

    @Override
    protected boolean checkProcessing() {
        return gameWorld.getGameStatus() != null && gameWorld.getGameStatus().equals(STATUS.GAMING);
    }

    /**
     *
     */
    @Override
    protected void process(int arrowId) {
        TilesMap map = gameWorld.getMap();

        // set to true when all is ok
        boolean proceed = false;
        Arrow arrow = mArrow.get(arrowId);
        Velocity velo = mVelo.get(arrowId);
        Position position = mPosition.get(arrowId);
        float newWorldX = position.xWorld + velo.velocityXPixel * world.delta;
        float newWorldY = position.yWorld + velo.velocityYPixel * world.delta;
        // check borders
        float maxX = map.getTileWidthPixel() * map.getWidth();
        float maxY = map.getTileHeightPixel() * map.getHeight();

        if (newWorldX < 0 || newWorldY < 0 || newWorldX > maxX || newWorldY > maxY) {
            // Arrow is out of play field
            proceed = false;
        } else {
            // check tile type
            int newTileX = map.getTileX((int) newWorldX + 8);
            int newTileY = map.getTileY((int) newWorldY + 8);
            int newCell = map.getTileTypeAt(newTileX, newTileY);

            if (map.getTileAt(newTileX, newTileY).isVisible() && (newCell == Tile.CELL_FLOOR || newCell == Tile.CELL_DOOR_OPENED)) {
                // move arrow and continue
                proceed = true;
                position.xWorld = newWorldX;
                position.yWorld = newWorldY;
                position.currentRoom = map.getTileAt(newTileX, newTileY).getRoom();
                position.updateBoundingBox();
            }
        }
        if (proceed) {
            // check collision
            IntBag mobs = gameWorld.getGroupManager().getMonsters();
            for (int i = 0; i < mobs.size(); i++) {
                int mobId = mobs.get(i);
                Position mobPos = mPosition.get(mobId);
                if (isCollision(position.boundingBox, mobPos.boundingBox)) {
                    int playerId = gameWorld.getLocalPlayerId();
                    Player player = mPlayer.get(playerId);
                    Monster monster = mMonster.get(mobId);
                    // arrow hits the mobs
                    proceed = false;
                    Status monsterStatus = mStatus.get(mobId);
                    int dmg = arrow.damage - monsterStatus.powerArmor;
                    if (monster.undead && player.inventory.holdsCross) {
                        dmg += 3;
                    }
                    if (dmg < 0) {
                        dmg = 0;
                    }
                    monsterStatus.healthCurrent -= dmg;
                    if (monsterStatus.healthCurrent <= 0) {
                        world.delete(mobId);
                        player.incKillCount();
                        mobPos.currentRoom.removeMonster(mobId);
                        gameWorld.addMessage("You hit " + monster.name + " for " + dmg + " damage and kill it.");
                    } else {
                        gameWorld.addMessage("You hit " + monster.name + " for " + dmg + " damage.");
                    }

                }
            }
        }

        if (proceed) {
            // render
            TextureRegion tex = null;
            switch (velo.heading) {
                case EAST:
                    tex = spriteImages.getArrowEast();
                    break;
                case WEST:
                    tex = spriteImages.getArrowWest();
                    break;
                case SOUTH:
                    tex = spriteImages.getArrowSouth();
                    break;
                case NORTH:
                    tex = spriteImages.getArrowNorth();
                    break;
            }
            batch.draw(tex, position.xWorld, position.yWorld);
        }

        if (!proceed) {
            // arrow shot against obstacle
            world.delete(arrowId);
        }
    }

    boolean isCollision(Rectangle p1, Rectangle p2) {
        return p1.overlaps(p2);
    }

    @Override
    protected void end() {
        batch.end();
    }

    @Override
    protected void begin() {
        batch.setProjectionMatrix(gameWorld.getRenderSystem().getCombined());
        batch.begin();
    }
}
