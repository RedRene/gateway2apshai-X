package de.lambdamoo.gta.client.systems;

import com.artemis.Aspect;
import com.artemis.ComponentMapper;
import com.artemis.Entity;
import com.artemis.systems.IteratingSystem;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.utils.ActorGestureListener;
import com.badlogic.gdx.utils.Array;

import de.lambdamoo.gta.client.screens.hud.PlayerHUD;
import de.lambdamoo.gta.client.systems.util.CameraMapHelper;
import de.lambdamoo.gta.client.util.MyLogger;
import de.lambdamoo.gta.client.util.SpriteImages;
import de.lambdamoo.gta.common.dto.Tile;
import de.lambdamoo.gta.common.dto.TilesMap;
import de.lambdamoo.gta.world.GameWorld;
import de.lambdamoo.gta.world.components.Attacking;
import de.lambdamoo.gta.world.components.MapObject;
import de.lambdamoo.gta.world.components.Monster;
import de.lambdamoo.gta.world.components.Player;
import de.lambdamoo.gta.world.components.Position;
import de.lambdamoo.gta.world.components.Render;
import de.lambdamoo.gta.world.components.Status;
import de.lambdamoo.gta.world.components.Trap;
import de.lambdamoo.gta.world.components.Velocity;

public class RenderSystem extends IteratingSystem {
    private Pixmap backgroundCachePixmap = new Pixmap(128 * 8, 64 * 8, Pixmap.Format.RGB888);
    private Texture backgroundCacheTexture = null;
    private SpriteBatch batch;
    private SpriteImages spriteImages = null;
    private GameWorld gameWorld = null;
    private ComponentMapper<MapObject> mMapObject;
    private ComponentMapper<Player> mPlayer;
    private ComponentMapper<Render> mRender;
    private ComponentMapper<Monster> mMonster;
    private ComponentMapper<Trap> mTrap;
    private ComponentMapper<Velocity> mVelocity;
    private ComponentMapper<Attacking> mAttacking;
    private ComponentMapper<Position> mPosition;
    private ComponentMapper<Status> mStatus;
    private CameraMapHelper cameraMapHelper = new CameraMapHelper();
    private PlayerHUD playerHUD = null;
    private ActorGestureListener mapGestureListener = new ActorGestureListener() {
        private float initialZoomScale = 1.0f;

        @Override
        public void touchDown(InputEvent event, float x, float y, int pointer, int button) {
            if (!event.isHandled()) {
                initialZoomScale = cameraMapHelper.getZoom();
            }
        }

        @Override
        public void zoom(InputEvent event, float initialDistance, float distance) {
            if (!event.isHandled()) {
                float ratio = initialDistance / distance;
                float newZoom = initialZoomScale * ratio;
                //MyLogger.getInstance().log("RenderSystem", "zoom ratio=" + ratio);
                cameraMapHelper.setZoom(newZoom);
            }
        }

        /**
         * This method moves the map
         * @param event
         * @param velocityX
         * @param velocityY
         * @param button
         */
        @Override
        public void fling(InputEvent event, float velocityX, float velocityY, int button) {
            cameraMapHelper.moveMap(velocityX, velocityY, gameWorld.getMap().getWidth(), gameWorld.getMap().getHeight());
        }
    };

    public RenderSystem(GameWorld gameWorld, SpriteImages images) {
        super(Aspect.all(Render.class));
        this.gameWorld = gameWorld;
        this.spriteImages = images;
        batch = new SpriteBatch();

        playerHUD = new PlayerHUD(gameWorld, images);
    }

    public float getZoom() {
        return cameraMapHelper.getZoom();
    }

    public void setZoom(float zoom) {
        cameraMapHelper.setZoom(zoom);
    }

    public Matrix4 getCombined() {
        return cameraMapHelper.getCombined();
    }

    public void dispose() {
        backgroundCachePixmap.dispose();
        if (backgroundCacheTexture != null) {
            backgroundCacheTexture.dispose();
        }
    }

    public void clearCamera() {
        cameraMapHelper.setTarget(null);
    }

    /**
     * Set the Stage of the MapDungeonScreen
     *
     * @param stage
     */
    public void setStage(Stage stage) {
        if (playerHUD.getGameStage() != null) {
            playerHUD.getGameStage().removeListener(mapGestureListener);
        }
        playerHUD.setStage(stage);
        playerHUD.getGameStage().addListener(mapGestureListener);
    }


    public Stage getGameStage() {
        return playerHUD.getGameStage();
    }

    public void resize(int width, int height) {
        playerHUD.resize(width, height);
    }

    public void addMessage(String str) {
        playerHUD.addMessage(str);
    }

    @Override
    protected void end() {
        super.end();
        batch.end();

        playerHUD.render(world);
    }

    /**
     * Renders the real sprites like player, mobs, treasure, etc.
     *
     * @param e
     */
    @Override
    protected void process(int e) {
        Render render = mRender.get(e);
        Player hero = mPlayer.get(e);
        Velocity velo = mVelocity.get(e);
        Attacking att = mAttacking.get(e);
        Status status = mStatus.get(e);
        if (hero != null) {
            // entity is hero
            Position pos = mPosition.get(e);
            if (!cameraMapHelper.hasTarget()) {
                cameraMapHelper.setTarget(pos);
            }
            renderPlayer(pos, render, velo, att);
            playerHUD.renderPlayerStatus(status, hero);
        } else {
            MapObject mapObj = mMapObject.get(e);
            if (mapObj.activated) {
                switch (mapObj.type) {
                    case Monster:
                        renderMonster(e, render);
                        break;
                    case Item:
                        renderItem(e, render);
                        break;
                    case Trap:
                        Trap trap = mTrap.get(e);
                        if (trap.visible) {
                            renderItem(e, render);
                        }
                        break;
                    case Treasure:
                        renderItem(e, render);
                        break;
                }
            }
        }
    }

    /**
     * Renders the player sprite to the screen
     *
     * @param pos
     * @param render
     * @param velo
     * @param attack
     */
    protected void renderPlayer(Position pos, Render render, Velocity velo, Attacking attack) {
        if (pos != null) {
            if (gameWorld.isTickerEightSecond() && velo.isMoving()) {
                render.incSpriteIndex();
            }
            if (gameWorld.isTickerEightSecond() && attack.attacking) {
                attack.incAttackCycle();
            }
            float x = pos.xWorld;
            float y = pos.yWorld;
            TextureRegion tex = null;
            switch (velo.heading) {
                case EAST:
                    if (attack.attacking) {
                        if (attack.attackMode.equals(Attacking.AttackMode.Sword)) {
                            tex = spriteImages.getPlayerAttackEast().get(attack.currentAttackCycle);
                            x += 0;
                        } else {
                            tex = spriteImages.getPlayerBowEast().get(0);
                        }
                    } else {
                        tex = spriteImages.getPlayerEast().get(render.currentSpriteIndex);
                    }
                    break;
                case SOUTH:
                    if (attack.attacking) {
                        if (attack.attackMode.equals(Attacking.AttackMode.Sword)) {
                            tex = spriteImages.getPlayerAttackSouth().get(attack.currentAttackCycle);
                            y -= 5;
                        } else {
                            tex = spriteImages.getPlayerBowSouth().get(0);
                        }
                    } else {
                        tex = spriteImages.getPlayerSouth().get(render.currentSpriteIndex);
                    }
                    break;
                case WEST:
                    if (attack.attacking) {
                        if (attack.attackMode.equals(Attacking.AttackMode.Sword)) {
                            tex = spriteImages.getPlayerAttackWest().get(attack.currentAttackCycle);
                            x -= 10;
                        } else {
                            tex = spriteImages.getPlayerBowWest().get(0);
                        }
                    } else {
                        tex = spriteImages.getPlayerWest().get(render.currentSpriteIndex);
                    }
                    break;
                case NORTH:
                    if (attack.attacking) {
                        if (attack.attackMode.equals(Attacking.AttackMode.Sword)) {
                            tex = spriteImages.getPlayerAttackNorth().get(attack.currentAttackCycle);
                            y += 0;
                        } else {
                            tex = spriteImages.getPlayerBowNorth().get(0);
                        }
                    } else {
                        tex = spriteImages.getPlayerNorth().get(render.currentSpriteIndex);
                    }
                    break;
                default:
                    tex = spriteImages.getPlayerEast().get(render.currentSpriteIndex);
            }
            batch.draw(tex, x, y);
        }
    }

    /**
     * Renders an monster to the screen
     *
     * @param spriteId
     * @param render
     */
    protected void renderMonster(int spriteId, Render render) {
        Position pos = mPosition.get(spriteId);
        if (gameWorld.isTickerHalfSecond()) {
            render.incSpriteIndex();
        }
        Array<TextureAtlas.AtlasRegion> texArray = spriteImages.getMonsterSprites(render.spriteIndex);
        TextureRegion tex = texArray.get(render.currentSpriteIndex);
        batch.draw(tex, pos.xWorld, pos.yWorld);
    }

    /**
     * Renders an item to the screen
     *
     * @param spriteId
     * @param render
     */
    protected void renderItem(int spriteId, Render render) {
        Entity sprite = world.getEntity(spriteId);
        Position pos = mPosition.get(sprite);
        TextureRegion tex = spriteImages.getItemSprite(render.spriteIndex);
        batch.draw(tex, pos.xWorld, pos.yWorld);
    }

    /**
     * This method renders the map and the "inhabitants". Furthermore it updates the camera position to follow the player.
     */
    @Override
    protected void begin() {
        TilesMap tilesMap = gameWorld.getMap();
        cameraMapHelper.update(world.getDelta());
        cameraMapHelper.applyTo();

        batch.setProjectionMatrix(cameraMapHelper.getCombined());

        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        batch.begin();
        if (tilesMap != null && tilesMap.isChanged()) {
            MyLogger.getInstance().log("RenderSystem", "renderToCache");
            renderBackgroundMapToCache(tilesMap);
            tilesMap.resetChanged();
            MyLogger.getInstance().log("RenderSystem", "renderToCache finished");
        }
        if (backgroundCacheTexture != null) {
            batch.draw(backgroundCacheTexture, 0, 0);
        }
    }

    /**
     * This method renders the full TileMap to the screen
     *
     * @param tilesMap
     */
    protected void renderBackgroundMapToCache(TilesMap tilesMap) {
        int counter = 0;
        int c = 64 * 8;
        int level = tilesMap.getLevel();
        int tilesCountY = tilesMap.getHeight();
        int tilesCountX = tilesMap.getWidth();
        for (int y = 0; y < tilesCountY; y++) {
            for (int x = 0; x < tilesCountX; x++) {
                Tile tile = tilesMap.getTileAt(x, y);
                // write tile only when changed
                if (tile.isChanged()) {
                    counter++;
                    Pixmap spritePixmap = getPixmapForSprite(tile, level);
                    if (spritePixmap != null) {
                        backgroundCachePixmap.drawPixmap(spritePixmap, x * 8, c - (y + 1) * 8);
                    } else {
                        MyLogger.getInstance().log("RenderSystem", "image is null: " + x + "/" + y);
                    }
                    tile.setChanged(false);
                }
            }
        }
        this.backgroundCacheTexture = new Texture(this.backgroundCachePixmap);
        this.backgroundCacheTexture.getTextureData().consumePixmap();
        MyLogger.getInstance().log("RenderSystem", "cached " + counter + " tiles.");
    }


    private Pixmap getPixmapForSprite(Tile tile, int level) {
        Pixmap pm = null;
        if (tile.isVisible()) {
            switch (tile.getTileType()) {
                case Tile.CELL_DOOR:
                    pm = spriteImages.getPixmapLevelObject(level, 2);
                    break;
                case Tile.CELL_WALL:
                    pm = spriteImages.getPixmapLevelObject(level, 0);
                    break;
                case Tile.CELL_HIDDENDOOR:
                    pm = spriteImages.getPixmapLevelObject(level, 0);
                    break;
                case Tile.CELL_FLOOR:
                    pm = spriteImages.getPixmapTextureNonLevel(0);
                    break;
                case Tile.CELL_UNWALKABLE:
                    pm = spriteImages.getPixmapTextureNonLevel(1);
                    break;
                case Tile.CELL_DOOR_OPENED:
                    pm = spriteImages.getPixmapTextureNonLevel(0);
                    break;
            }
        } else {
            pm = spriteImages.getPixmapTextureNonLevel(1);
        }
        return pm;
    }
}