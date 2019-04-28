package de.lambdamoo.gta.world;

import com.artemis.Aspect;
import com.artemis.ComponentMapper;
import com.artemis.World;
import com.artemis.WorldConfiguration;
import com.artemis.WorldConfigurationBuilder;
import com.artemis.utils.IntBag;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.StringTokenizer;

import de.lambdamoo.gta.client.Core;
import de.lambdamoo.gta.client.dialog.DialogGameOver;
import de.lambdamoo.gta.client.dialog.DialogInventoryEquip;
import de.lambdamoo.gta.client.dialog.DialogScrollCast;
import de.lambdamoo.gta.client.dialog.MyDialogListener;
import de.lambdamoo.gta.client.screens.ScreenListener;
import de.lambdamoo.gta.client.systems.RenderSystem;
import de.lambdamoo.gta.client.util.MapBuilder;
import de.lambdamoo.gta.client.util.MyLogger;
import de.lambdamoo.gta.client.util.SoundManager;
import de.lambdamoo.gta.common.dto.Room;
import de.lambdamoo.gta.common.dto.TilesMap;
import de.lambdamoo.gta.common.game.MonsterFactory;
import de.lambdamoo.gta.world.components.Attacking;
import de.lambdamoo.gta.world.components.Item;
import de.lambdamoo.gta.world.components.MapObject;
import de.lambdamoo.gta.world.components.Monster;
import de.lambdamoo.gta.world.components.Player;
import de.lambdamoo.gta.world.components.Position;
import de.lambdamoo.gta.world.components.Status;
import de.lambdamoo.gta.world.components.Trap;
import de.lambdamoo.gta.world.components.Treasure;
import de.lambdamoo.gta.world.systems.ActionSystem;
import de.lambdamoo.gta.world.systems.PostProcessSystem;
import de.lambdamoo.gta.world.systems.gaming.AISystem;
import de.lambdamoo.gta.world.systems.gaming.ArrowSystem;
import de.lambdamoo.gta.world.systems.gaming.CollisionSystem;
import de.lambdamoo.gta.world.systems.gaming.MapObjectsGroupManager;
import de.lambdamoo.gta.world.systems.gaming.MovementSystem;
import de.lambdamoo.gta.world.systems.util.EntityHelper;

import static de.lambdamoo.gta.world.components.Player.LEVEL_TIME;


public class GameWorld {
    private final static String RESUME_PREFSFILE = "de.lambdamoo.gta.client.GameWorld";
    protected TilesMap map = null;
    protected List<Room> roomList = null;
    private Random random = new Random();
    private World world = null;
    private STATUS gameStatus = STATUS.GAMING;
    private int[][] _seeds = {{113, 143, 102, 100, 157, 112, 115, 206, 82, 252, 167, 0, 73, 17, 18, 223},
            {178, 98, 71, 170, 237, 169, 127, 223, 231, 9, 76, 211, 183, 172, 0, 220},
            {160, 120, 229, 129, 220, 88, 181, 187, 205, 94, 236, 177, 237, 110, 255, 248},
            {173, 134, 126, 174, 66, 169, 130, 171, 118, 87, 102, 187, 80, 41, 0, 172},
            {225, 67, 238, 224, 75, 9, 167, 136, 175, 85, 201, 173, 176, 131, 216, 249},
            {163, 114, 174, 160, 193, 237, 176, 122, 205, 115, 209, 101, 119, 181, 245, 219},
            {194, 72, 134, 186, 158, 226, 50, 147, 207, 225, 216, 99, 162, 107, 179, 100},
            {171, 234, 70, 150, 136, 96, 188, 174, 167, 4, 13, 7, 200, 114, 107, 236}};
    private ActionSystem actionSystem = null;
    private RenderSystem renderSystem = null;
    private MapObjectsGroupManager groupManager = null;
    private boolean tickerSecond = false;
    private boolean tickerHalfSecond = false;
    private boolean tickerQuarterSecond = false;
    private boolean tickerEightSecond = false;
    private float counterSecond = 0.0f;
    private float counterHalfSecond = 0.0f;
    private float counterQuarterSecond = 0.0f;
    private float counterEightSecond = 0.0f;
    private SoundManager soundManager = new SoundManager();
    private Core core = null;
    private DialogInventoryEquip dlgInventory = null;
    private DialogScrollCast dlgInventorySelect = null;
    private MonsterFactory factory = null;
    private DialogGameOver dlgGameOver = null;

    public GameWorld(Core coreVar) {
        this.core = coreVar;
        initWorld();

        dlgGameOver = new DialogGameOver(core, core.getSpriteImages());
        dlgGameOver.setBackground(core.getSpriteImages().getNinePatchOpaqueBackgroundStrong());
        dlgInventory = new DialogInventoryEquip(this, core.getSpriteImages());
        dlgInventorySelect = new DialogScrollCast(this, core.getSpriteImages());
    }

    /**
     * This method initializes the artemis world object with the systems.
     */
    private void initWorld() {
        WorldConfigurationBuilder builder = new WorldConfigurationBuilder();
        groupManager = new MapObjectsGroupManager();
        builder.with(groupManager);

        // do actions
        actionSystem = new ActionSystem(this);
        builder.with(actionSystem);

        AISystem aiSystem = new AISystem(this);
        builder.with(aiSystem);

        MovementSystem movementSystem = new MovementSystem(this);
        builder.with(movementSystem);

        // check collisions
        CollisionSystem colSystem = new CollisionSystem(this);
        builder.with(colSystem);

        // finally render map and sprites
        renderSystem = new RenderSystem(this, core.getSpriteImages());
        builder.with(renderSystem);

        // check arrows
        ArrowSystem arrowSystem = new ArrowSystem(this, core.getSpriteImages());
        builder.with(arrowSystem);

        PostProcessSystem postProcessSystem = new PostProcessSystem(this);
        builder.with(postProcessSystem);

        WorldConfiguration config = builder.build();
        world = new World(config);

        factory = new MonsterFactory();
        factory.init(world);
    }

    /**
     * This method restarts the current level with the same player and his full health status.
     */
    protected void levelRestart() {
        // get the player components
        int playerId = getLocalPlayerId();
        ComponentMapper<Position> mPosition = world.getMapper(Position.class);
        Position pos = mPosition.create(playerId);
        ComponentMapper<Player> mPlayer = world.getMapper(Player.class);
        Player player = mPlayer.create(playerId);
        ComponentMapper<Status> mStatus = world.getMapper(Status.class);
        Status status = mStatus.create(playerId);

        cleanLevelObjects(player);

        int level = map.getLevel();
        int dungeon = map.getDungeon();
        // create new dungeon map
        map = new TilesMap(level, dungeon, 128, 64);
        MapBuilder builder = new MapBuilder(map, _seeds[2][1], false);
        builder.buildRooms();
        builder.populateRooms(world, this, getRandom());
        setRoomList(builder.getRooms());


        // set the position
        Room firstRoom = builder.getFirstRoom();
        map.setVisible(firstRoom, true);
        pos.currentRoom = firstRoom;
        pos.currentRoom.activateRoom(world, true, true);
        int playerTileX = pos.currentRoom.getTileX() + pos.currentRoom.getTileCountX() / 2;
        int playerTileY = pos.currentRoom.getTileY() + pos.currentRoom.getTileCountY() / 2;
        pos.xWorld = playerTileX * map.getTileWidthPixel();
        pos.yWorld = playerTileY * map.getTileHeightPixel();
        pos.updateBoundingBox();

        // reset player status
        player.time = LEVEL_TIME;
        status.healthCurrent = status.healthMax;
    }

    public Random getRandom() {
        return random;
    }

    /**
     * This method cleans the objects: all monsters, all traps, all treasure, all items that are not in inventory
     *
     * @param player the Player component for checking inventory, equipped
     */
    protected void cleanLevelObjects(Player player) {
        // clean the objects: all monsters, all traps, all treasure, all items that are not in inventory or equipped
        IntBag entities = world.getAspectSubscriptionManager().get(Aspect.all(Monster.class)).getEntities();
        int[] ids = entities.getData();
        for (int i = 0, s = entities.size(); s > i; i++) {
            world.delete(ids[i]);
        }
        entities = world.getAspectSubscriptionManager().get(Aspect.all(Treasure.class)).getEntities();
        ids = entities.getData();
        for (int i = 0, s = entities.size(); s > i; i++) {
            world.delete(ids[i]);
        }
        entities = world.getAspectSubscriptionManager().get(Aspect.all(Trap.class)).getEntities();
        ids = entities.getData();
        for (int i = 0, s = entities.size(); s > i; i++) {
            world.delete(ids[i]);
        }
        entities = world.getAspectSubscriptionManager().get(Aspect.all(Item.class)).getEntities();
        ids = entities.getData();
        for (int i = 0, s = entities.size(); s > i; i++) {
            int id = ids[i];
            if (!player.inventory.hasItem(id)) {
                world.delete(id);
            }
        }
    }

    public void setRoomList(List<Room> roomList) {
        this.roomList = roomList;
    }

    public Core getCore() {
        return core;
    }

    /**
     * This method clears the world instance of all old objects like player, mobs, treasure, etc.
     */
    public void cleanGameWorld() {
        IntBag entities = world.getAspectSubscriptionManager().get(Aspect.all()).getEntities();
        int[] ids = entities.getData();
        for (int i = 0, s = entities.size(); s > i; i++) {
            world.delete(ids[i]);
        }
        getRenderSystem().clearCamera();
    }

    public MonsterFactory getMonsterFactory() {
        return factory;
    }

    /**
     * This method teleports the player to a random room
     */
    public void teleportPlayerRandom() {
        int playerId = getLocalPlayerId();
        int rand = random.nextInt(roomList.size());
        Room randRoom = roomList.get(rand);
        ComponentMapper<Position> mPosition = world.getMapper(Position.class);
        Position pos = mPosition.get(playerId);

        map.setVisible(randRoom, true);
        randRoom.activateRoom(world, true, true);

        MyLogger.getInstance().log("GameWorld", "Teleport player to room nr.:" + randRoom.getRoomNumber());
        int playerTileX = randRoom.getTileX() + randRoom.getTileCountX() / 2;
        int playerTileY = randRoom.getTileY() + randRoom.getTileCountY() / 2;
        pos.xWorld = playerTileX * 8;
        pos.yWorld = playerTileY * 8;
        pos.currentRoom = randRoom;
        if (randRoom.hasMonster()) {
            getSoundManager().playSound(SoundManager.Sounds.Encounter);
            String name = randRoom.getFirstMonsterName(world);
            if (name != null) {
                addMessage("Careful, you face a " + name);
            }
        }
    }

    public int getLocalPlayerId() {
        return groupManager.getPlayerId();
    }

    public SoundManager getSoundManager() {
        return soundManager;
    }

    /**
     * This method adds a message to the screen
     *
     * @param str
     */
    public void addMessage(String str) {
        if (!str.endsWith("!") && !str.endsWith(".")) {
            str += ".";
        }
        getRenderSystem().addMessage(str);
    }

    public RenderSystem getRenderSystem() {
        return renderSystem;
    }

    private Room getRoomByNumber(int number) {
        Iterator<Room> iter = roomList.iterator();
        while (iter.hasNext()) {
            Room room = iter.next();
            if (room.getRoomNumber() == number) {
                return room;
            }
        }
        return null;
    }

    /**
     * This method sets the AI of all already activated monsters to the parameter one
     *
     * @param ai
     */
    public void applyAI(Monster.AI ai) {
        ComponentMapper<MapObject> mMapObject = world.getMapper(MapObject.class);
        ComponentMapper<Monster> mMonster = world.getMapper(Monster.class);
        ComponentMapper<Attacking> mAttack = world.getMapper(Attacking.class);
        IntBag entities = world.getAspectSubscriptionManager().get(Aspect.all(Monster.class)).getEntities();
        for (int i = 0; i < entities.size(); i++) {
            MapObject mapObj = mMapObject.get(entities.get(i));
            if (mapObj.activated) {
                Monster monster = mMonster.get(entities.get(i));
                monster.ai = ai;
                Attacking attack = mAttack.get(entities.get(i));
                if (ai.equals(Monster.AI.Static)) {
                    attack.canAttack = false;
                } else {
                    attack.canAttack = true;
                }
            }
        }
    }

    /**
     * This method kills all already activated monsters in the current level.
     */
    public void killActiveMonsters() {
        ComponentMapper<MapObject> mMapObject = world.getMapper(MapObject.class);
        ComponentMapper<Monster> mMonster = world.getMapper(Monster.class);
        ComponentMapper<Position> mPosition = world.getMapper(Position.class);
        IntBag entities = world.getAspectSubscriptionManager().get(Aspect.all(Monster.class)).getEntities();
        for (int i = 0; i < entities.size(); i++) {
            int monsterId = entities.get(i);
            MapObject mapObj = mMapObject.get(monsterId);
            if (mapObj != null && mapObj.activated) {
                Monster monster = mMonster.get(monsterId);
                Position pos = mPosition.get(monsterId);
                pos.currentRoom.removeMonster(monsterId);
                world.delete(monsterId);
                addMessage("You kill " + monster.name);
            }
        }
    }

    public ActionSystem getActionSystem() {
        return actionSystem;
    }

    private List<String> applyDungeonBonus(Player player, Status status) {
        int abilityRaise = 0;
        // max. 3 ability raises
        if (status.levelPoints >= 21) {
            abilityRaise++;
        }
        if (status.levelPoints >= 43) {
            abilityRaise++;
        }
        if (status.levelPoints >= 51) {
            abilityRaise++;
        }
        List<String> messages = new ArrayList<String>(5);
        // raise the abilities only destinct
        List<Integer> raised = new ArrayList<Integer>(3);
        int counter = 0;
        while (counter < abilityRaise) {
            int randomAbility = random.nextInt(3);
            if (!raised.contains(randomAbility)) {
                String str = player.raiseAbility(randomAbility);
                raised.add(randomAbility);
                counter++;
                messages.add(str);
            }
        }
        return messages;
    }

    /**
     * This method saves the current status to disk
     *
     * @param player
     * @param status
     */
    private void savePlayerStatus(Player player, Status status) {
        MyLogger.getInstance().log("GameWorld", "savePlayerStatus:" + player.name);
        Preferences prefs = Gdx.app.getPreferences(RESUME_PREFSFILE);
        prefs.clear();
        prefs.putString("player.name", player.name);
        prefs.putBoolean("player.trainer", player.trainer);
        if (player.inventory.currentWeapon != null) {
            prefs.putString("player.currentWeapon", player.inventory.currentWeapon.name);
        }
        if (player.inventory.currentArmor != null) {
            prefs.putString("player.currentArmor", player.inventory.currentArmor.name);
        }
        if (player.inventory.currentShield != null) {
            prefs.putString("player.currentShield", player.inventory.currentShield.name);
        }
        prefs.putInteger("player.lifes", player.lifes);
        prefs.putInteger("player.score", player.score);
        prefs.putBoolean("player.amuletStrengthConsumed", player.amuletStrengthConsumed);
        prefs.putBoolean("player.amuletAgilityConsumed", player.amuletAgilityConsumed);
        prefs.putBoolean("player.amuletLuckConsumed", player.amuletLuckConsumed);
        prefs.putBoolean("player.holdsCross", player.inventory.holdsCross);
        prefs.putInteger("player.arrowsCount", player.inventory.arrowsCount);
        prefs.putBoolean("player.hasBow", player.inventory.hasBow);
        prefs.putInteger("player.strength", player.strength);
        prefs.putInteger("player.agility", player.agility);
        prefs.putInteger("player.baseLuck", player.baseLuck);
        prefs.putInteger("player.luck", player.luck);
        prefs.putInteger("player.killCount", player.killCount);
        prefs.putString("player.listEquiped", getItemListNames(player.inventory.listEquiped));
        prefs.putString("player.listInventory", getItemListNames(player.inventory.getListInventory()));

        prefs.putInteger("status.healthMax", status.healthMax);
        prefs.putInteger("status.level", status.level);

        prefs.putInteger("dungeon", map.getDungeon());

        // bulk update your preferences
        prefs.flush();

    }

    /**
     * This method starts the next level
     */
    private void gameNextLevel() {

        // get the player components
        final int playerId = getLocalPlayerId();
        ComponentMapper<Player> mPlayer = world.getMapper(Player.class);
        final Player player = mPlayer.get(playerId);
        ComponentMapper<Status> mStatus = world.getMapper(Status.class);
        final Status status = mStatus.get(playerId);

        cleanLevelObjects(player);

        int level = map.getLevel();
        if (level < 8) {
            level++;
        }
        // increase stats
        List<String> messages = applyDungeonBonus(player, status);

        // reset player status
        player.time = LEVEL_TIME;
        status.healthMax = player.strength + player.agility + player.luck;
        status.healthCurrent = status.healthMax;
        status.level = level;
        status.levelPoints = 0;

        // store the data to disk
        savePlayerStatus(player, status);

        // show dungeon bonus screen
        core.showNextLevelScreen(messages, new ScreenListener() {
            @Override
            public void returnFromScreen() {
                startLevel(playerId, map.getDungeon(), status);
            }
        });
    }

    /**
     * This method loads the last saved status from disk and starts the level
     */
    public void gameResumeSavedLevel() {
        // clean old player
        int playerId = getLocalPlayerId();
        if (playerId != -1) {
            world.delete(playerId);
        }

        // create with archetype
        playerId = EntityHelper.createPlayer(world);
        ComponentMapper<Player> mPlayer = world.getMapper(Player.class);
        ComponentMapper<Status> mStatus = world.getMapper(Status.class);

        Player player = mPlayer.get(playerId);
        Status status = mStatus.get(playerId);

        Preferences prefs = Gdx.app.getPreferences(RESUME_PREFSFILE);
        player.name = prefs.getString("player.name");
        player.trainer = prefs.getBoolean("player.trainer");
        player.inventory.currentWeapon = factory.createItemByName(prefs.getString("player.currentWeapon"));
        player.inventory.currentArmor = factory.createItemByName(prefs.getString("player.currentArmor"));
        player.inventory.currentShield = factory.createItemByName(prefs.getString("player.currentShield"));
        player.lifes = prefs.getInteger("player.lifes");
        player.score = prefs.getInteger("player.score");
        player.amuletStrengthConsumed = prefs.getBoolean("player.amuletStrengthConsumed");
        player.amuletAgilityConsumed = prefs.getBoolean("player.amuletAgilityConsumed");
        player.amuletLuckConsumed = prefs.getBoolean("player.amuletLuckConsumed");
        player.inventory.holdsCross = prefs.getBoolean("player.holdsCross");
        player.inventory.arrowsCount = prefs.getInteger("player.arrowsCount");
        player.inventory.hasBow = prefs.getBoolean("player.hasBow");
        player.strength = prefs.getInteger("player.strength");
        player.agility = prefs.getInteger("player.agility");
        player.baseLuck = prefs.getInteger("player.baseLuck");
        player.luck = prefs.getInteger("player.luck");
        player.killCount = prefs.getInteger("player.killCount");

        List<Item> listInv = scanItemList(prefs.getString("player.listInventory"));
        Iterator<Item> iter = listInv.iterator();
        while (iter.hasNext()) {
            Item item = iter.next();
            player.inventory.addItem(item);
        }
        List<Item> listEquiped = scanItemList(prefs.getString("player.listEquiped"));
        iter = listEquiped.iterator();
        while (iter.hasNext()) {
            Item item = iter.next();
            player.inventory.listEquiped.add(item);
        }

        status.healthMax = prefs.getInteger("status.healthMax");
        status.level = prefs.getInteger("status.level");
        status.healthCurrent = status.healthMax;
        getActionSystem().playerRecallEquippedItemPower();

        ComponentMapper<Position> mPosition = world.getMapper(Position.class);
        Position position = mPosition.get(playerId);
        position.widthWorld = 16;
        position.heightWorld = 16;


        int dungeon = prefs.getInteger("dungeon");

        startLevel(playerId, dungeon, status);
    }

    /**
     * This method scans the comma separated list of item names and returns the appropriated items
     *
     * @param name
     * @return
     */
    private List<Item> scanItemList(String name) {
        List<Item> result = new ArrayList<Item>();
        StringTokenizer st = new StringTokenizer(name, ",");
        while (st.hasMoreElements()) {
            String itemSinglename = st.nextToken();
            result.add(factory.createItemByName(itemSinglename.trim()));
        }
        return result;
    }

    /**
     * This method generates from the item list a string of the item names separated by comma.
     *
     * @param list
     * @return
     */
    private String getItemListNames(List<Item> list) {
        StringBuffer result = new StringBuffer();
        Iterator<Item> iter = list.iterator();
        while (iter.hasNext()) {
            Item item = iter.next();
            result.append(item.name);
            if (iter.hasNext()) {
                result.append(", ");
            }
        }
        return result.toString();
    }

    private void startLevel(int playerId, int dungeon, Status status) {
        ComponentMapper<Position> mPosition = world.getMapper(Position.class);
        Position pos = mPosition.get(playerId);

        // create new dungeon map
        map = new TilesMap(status.level, dungeon, 128, 64);
        MapBuilder builder = new MapBuilder(map, _seeds[2][1], false);
        builder.buildRooms();
        builder.populateRooms(world, this, getRandom());
        setRoomList(builder.getRooms());

        // set the position
        Room firstRoom = builder.getFirstRoom();
        map.setVisible(firstRoom, true);
        pos.currentRoom = firstRoom;
        pos.currentRoom.activateRoom(world, true, true);
        int playerTileX = pos.currentRoom.getTileX() + pos.currentRoom.getTileCountX() / 2;
        int playerTileY = pos.currentRoom.getTileY() + pos.currentRoom.getTileCountY() / 2;
        pos.xWorld = playerTileX * map.getTileWidthPixel();
        pos.yWorld = playerTileY * map.getTileHeightPixel();
        pos.updateBoundingBox();

        setGameStatus(STATUS.GAMING);
        core.showScreenMapDungeon();
    }

    public String hasResumeInformation() {
        Preferences prefs = Gdx.app.getPreferences(RESUME_PREFSFILE);
        String name = prefs.getString("player.name", null);
        if (name != null && name.trim().length() == 0) {
            name = null;
        }
        return name;
    }

    public void setStage(Stage stage) {
        getRenderSystem().setStage(stage);
        renderSystem.getGameStage().addListener(new InputListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                if (dlgInventory.isVisible()) {
                    Rectangle rect = new Rectangle(dlgInventory.getX(), dlgInventory.getY(), dlgInventory.getWidth(), dlgInventory.getHeight());
                    if (!rect.contains(x, y)) {
                        dlgInventory.setVisible(false);
                    }
                }
                if (dlgInventorySelect.isVisible()) {
                    Rectangle rect = new Rectangle(dlgInventorySelect.getX(), dlgInventorySelect.getY(), dlgInventorySelect.getWidth(), dlgInventorySelect.getHeight());
                    if (!rect.contains(x, y)) {
                        dlgInventorySelect.setVisible(false);
                    }
                }
                return true;
            }
        });
    }

    public boolean isTickerQuarterSecond() {
        return tickerQuarterSecond;
    }

    public ComponentMapper getComponentMapper(Class clazz) {
        return world.getMapper(clazz);
    }

    public MapObjectsGroupManager getGroupManager() {
        return groupManager;
    }

    public STATUS getGameStatus() {
        return gameStatus;
    }

    /**
     * This method can set the current game status
     *
     * @param gameStatus
     */
    public void setGameStatus(STATUS gameStatus) {
        this.gameStatus = gameStatus;
        if (gameStatus != null) {
            switch (gameStatus) {
                case DEAD:
                    ComponentMapper<Player> mPlayer = world.getMapper(Player.class);
                    Player player = mPlayer.get(getLocalPlayerId());
                    player.lifes--;
                    if (player.lifes <= 0) {
                        // send the score to leaderboard
                        removeResumeInformation();
                        showDialogGameOver();
                    } else {
                        showDialogPlayerDead();
                    }
                    break;
                case INIT:
                    break;
                case GAMING:
                    break;
                case OPTIONS:
                    break;
                case NEXTLEVEL:
                    gameNextLevel();
                    break;
                case GAMEOVER:
                    removeResumeInformation();
                    showDialogGameOver();
                    break;
            }
        }
    }

    protected void showDialogGameOver() {
        dlgGameOver.show(getRenderSystem().getGameStage());
    }

    protected boolean showDialogPlayerDead() {
        core.showBoxMessage(getRenderSystem().getGameStage(), "Unfortunatly you died!", "Restart", new MyDialogListener() {
            @Override
            public boolean onPerform(Result result) {
                levelRestart();
                gameStatus = STATUS.GAMING;
                return true;
            }
        });
        return true;
    }

    /**
     * This method removes the resume information.
     */
    private void removeResumeInformation() {
        MyLogger.getInstance().log("GameWorld", "removeResumeInformation:");
        Preferences prefs = Gdx.app.getPreferences(RESUME_PREFSFILE);
        prefs.clear();
        prefs.flush();
    }

    public TilesMap getMap() {
        return map;
    }

    /**
     * This method makes all rooms visible and updates the tile map
     */
    public void showMap() {
        for (Room room : roomList) {
            if (!room.isVisible()) {
                map.setVisible(room, true);
                room.activateRoom(world, false, true);
            }
        }
    }

    /**
     * This method shows the inventory of the player on the screen
     */
    public void showDialogInventory() {
        ComponentMapper<Player> mPlayer = world.getMapper(Player.class);
        Player player = mPlayer.get(getLocalPlayerId());
        dlgInventory.populateInventory(player);
        dlgInventory.show(getRenderSystem().getGameStage());
    }

    /**
     * This method shows the use (select) item dialog of the player on the screen
     */
    public void showDialogUseItem() {
        ComponentMapper<Player> mPlayer = world.getMapper(Player.class);
        Player player = mPlayer.get(getLocalPlayerId());
        dlgInventorySelect.populateUsable(player);
        if (getRenderSystem().getGameStage().getActors().indexOf(dlgInventorySelect, false) == -1) {
            getRenderSystem().getGameStage().addActor(dlgInventorySelect);
        }
        dlgInventorySelect.show(getRenderSystem().getGameStage());
    }

    public int getCurrentMapLevel() {
        return map.getLevel();
    }

    /*The ModelBatch is one of the objects, which require disposing, hence we
      add it to the dispose function. */
    public void dispose() {
        if (renderSystem != null) {
            renderSystem.dispose();
        }
    }

    public void resize(int width, int height) {
        renderSystem.resize(width, height);
    }

    public boolean isTickerSecond() {
        return tickerSecond;
    }

    public boolean isTickerHalfSecond() {
        return tickerHalfSecond;
    }

    //and set up the render function with the Modelbatch
    public void process(float delta) {
        // tickers
        counterHalfSecond += delta;
        counterSecond += delta;
        counterQuarterSecond += delta;
        counterEightSecond += delta;
        if (counterSecond > 1.0f) {
            tickerSecond = true;
            counterSecond = 0.0f;
        } else {
            tickerSecond = false;
        }
        if (counterHalfSecond > 0.5f) {
            tickerHalfSecond = true;
            counterHalfSecond = 0.0f;
        } else {
            tickerHalfSecond = false;
        }
        if (counterQuarterSecond > 0.25f) {
            tickerQuarterSecond = true;
            counterQuarterSecond = 0.0f;
        } else {
            tickerQuarterSecond = false;
        }
        if (counterEightSecond > 0.125) {
            tickerEightSecond = true;
            counterEightSecond = 0.0f;
        } else {
            tickerEightSecond = false;
        }
        world.setDelta(delta);
        world.process();
    }


    public boolean isTickerEightSecond() {
        return tickerEightSecond;
    }

    public void gameNew(String playerName, int dungeon, boolean trainer) {
        // remove old players
        cleanGameWorld();

        // create new dungeon
        map = new TilesMap(1, dungeon, 128, 64);
        MapBuilder builder = new MapBuilder(map, _seeds[2][1], false);
        boolean buildOk = builder.buildRooms();
        builder.populateRooms(world, this, getRandom());
        builder.buildPlayer(playerName, world, this, trainer);
        setRoomList(builder.getRooms());
        setGameStatus(STATUS.GAMING);
        getActionSystem().playerRecallEquippedItemPower();

        // test code
        //int cursedSword = getMonsterFactory().createItemByTypeId(22, world);
        //builder.placeItemInRoom(world, builder.getRooms().get(1), cursedSword);
    }
}
