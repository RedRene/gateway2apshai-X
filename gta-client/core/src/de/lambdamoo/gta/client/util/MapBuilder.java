package de.lambdamoo.gta.client.util;

import com.artemis.ComponentMapper;
import com.artemis.World;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import de.lambdamoo.gta.common.dto.Door;
import de.lambdamoo.gta.common.dto.Room;
import de.lambdamoo.gta.common.dto.Tile;
import de.lambdamoo.gta.common.dto.TilesMap;
import de.lambdamoo.gta.common.game.MonsterFactory;
import de.lambdamoo.gta.world.GameWorld;
import de.lambdamoo.gta.world.components.Item;
import de.lambdamoo.gta.world.components.Monster;
import de.lambdamoo.gta.world.components.Player;
import de.lambdamoo.gta.world.components.Position;
import de.lambdamoo.gta.world.components.Trap;
import de.lambdamoo.gta.world.components.Treasure;
import de.lambdamoo.gta.world.systems.util.EntityHelper;
import de.lambdamoo.gta.world.systems.util.ItemNameComparator;

public class MapBuilder {

    final static protected int ROOM_WIDTH = 12;
    final static protected int ROOM_HEIGHT = 12;
    private final static Character NORTH = new Character('n');
    private final static Character EAST = new Character('e');
    private final static Character SOUTH = new Character('s');
    private final static Character WEST = new Character('w');
    private final static int checkWidth = 10;
    private final static int checkHeight = 10;
    private RandomGen random;
    private TilesMap map = null;
    private List<Room> doorRoomList = new ArrayList<Room>(64);
    private List<Room> allRooms = new ArrayList<Room>(64);
    private int nextRoomNumber = 1;
    private int maxRooms = 60;
    private Room firstRoom = null;
    private int[] _trapItems = {4, 5, 1, 0, 128, 3, 130, 0, 0, 0, 0, 1, 3, 128, 0, 0, 134, 0, 3, 0, 3, 1, 2, 0, 0,
            131, 128, 0, 0, 0, 131, 129, 129, 130, 0, 0, 128, 0, 128, 130, 128};

    public MapBuilder(TilesMap map, int seed, boolean iterate) {
        this.map = map;
        random = new RandomGen(map.getLevel(), map.getDungeon(), seed);
    }

    public List<Room> getRooms() {
        return allRooms;
    }

    public Room getFirstRoom() {
        return firstRoom;
    }

    public void buildPlayer(String playerName, World world, GameWorld gameWorld, boolean trainer) {
        MonsterFactory factory = gameWorld.getMonsterFactory();
        int playerTileX = firstRoom.getTileX() + firstRoom.getTileCountX() / 2;
        int playerTileY = firstRoom.getTileY() + firstRoom.getTileCountY() / 2;

        int playerId = EntityHelper.createPlayer(world);

        ComponentMapper<Player> mPlayer = world.getMapper(Player.class);
        Player player = mPlayer.get(playerId);
        player.name = playerName;
        player.trainer = trainer;

        ComponentMapper<Position> positionMap = world.getMapper(Position.class);
        Position position = positionMap.get(playerId);
        position.xWorld = playerTileX * map.getTileWidthPixel();
        position.yWorld = playerTileY * map.getTileHeightPixel();
        position.widthWorld = 16;
        position.heightWorld = 16;
        position.updateBoundingBox();
        position.currentRoom = firstRoom;
        position.currentRoom.setVisible(true);
        map.setVisible(position.currentRoom, true);
        position.currentRoom.activateRoom(world, true, true);

        Item weapon = null;
        Item armor = null;
        Item shield = null;

        if (trainer) {
            weapon = factory.createItemByTypeId(21);
            armor = factory.createItemByTypeId(27);
            shield = factory.createItemByTypeId(30);
            for (int i = 0; i < 17; i++) {
                Item item = factory.createItemByTypeId(i);
                if (item != null) {
                    player.inventory.getListInventory().add(item);
                }
            }
            // cross
            Item item = factory.createItemByTypeId(40);
            player.inventory.addItem(item);

            // wand
            item = factory.createItemByTypeId(41);
            player.inventory.addItem(item);

            // bow
            item = factory.createItemByTypeId(34);
            player.inventory.addItem(item);
            item = factory.createItemByTypeId(35);
            player.inventory.addItem(item);
            item = factory.createItemByTypeId(36);
            player.inventory.addItem(item);

            player.strength += 4;
            player.agility += 4;
            player.luck += 4;
            player.baseLuck += 4;
            player.amuletStrengthConsumed = true;
            player.amuletAgilityConsumed = true;
            player.amuletLuckConsumed = true;
        } else {
            weapon = factory.createItemByTypeId(17);
            armor = factory.createItemByTypeId(23);
        }

        player.inventory.addItem(weapon);
        player.inventory.addItem(armor);
        player.inventory.listEquiped.add(weapon);
        player.inventory.listEquiped.add(armor);
        player.inventory.currentWeapon = weapon;
        player.inventory.currentArmor = armor;
        // add shield if available
        if (shield != null) {
            player.inventory.addItem(shield);
            player.inventory.listEquiped.add(shield);
            player.inventory.currentShield = shield;
        }
        player.inventory.addItem(factory.createItemByTypeId(0));
        player.inventory.inventoryHasChanged = true;

        Collections.sort(player.inventory.getListInventory(), new ItemNameComparator());
    }

    public void populateRooms(World world, GameWorld gameWorld, Random localRand) {
        MonsterFactory factory = gameWorld.getMonsterFactory();
        int level = this.map.getLevel();
        int type = (level - 1) * 4;
        // Monsters
        for (int i = 0; i < 2; i++) {
            placeMonsterInRoom(world, getRandomRoomWithoutMonster(), factory.createMonster(type, world));
            placeMonsterInRoom(world, getRandomRoomWithoutMonster(), factory.createMonster(type + 1, world));
            placeMonsterInRoom(world, getRandomRoomWithoutMonster(), factory.createMonster(type + 2, world));
            placeMonsterInRoom(world, getRandomRoomWithoutMonster(), factory.createMonster(type + 3, world));
        }
        for (int i = 0; i < 8; i++) {
            placeMonsterInRoom(world, getRandomRoomWithoutMonster(),
                    factory.createMonster(Math.abs(random.getNextRand(type + 3)), world));
        }
        // Treasure
        for (int quality = 0; quality < 5; quality++) {
            placeTreasureInRoom(world, getRandomRoom(0), factory.createTreasure(quality, random.getNextPositiveRand(7), world, localRand, level));
            placeTreasureInRoom(world, getRandomRoom(0), factory.createTreasure(quality, random.getNextPositiveRand(7), world, localRand, level));
            placeTreasureInRoom(world, getRandomRoom(0), factory.createTreasure(quality, random.getNextPositiveRand(7), world, localRand, level));
        }
        placeTreasureInRoom(world, getRandomRoom(0), factory.createTreasure(5, random.getNextPositiveRand(7), world, localRand, level));

        // Items
        int trapCounter = 0;
        MyLogger.getInstance().log("MapBuilder", "place 5 level items");
        for (Integer item : factory.createItemsByLevel(map.getLevel(), world, localRand)) {
            placeItemInRoom(world, getRandomRoom(0), item);
        }
        List<Integer> allItemsToLevel = new ArrayList<Integer>(30);
        for (int i = 1; i <= map.getLevel(); i++) {
            allItemsToLevel.addAll(factory.createItemsByLevel(i, world, localRand));
        }

        ComponentMapper<Position> mPosition = world.getMapper(Position.class);
        ComponentMapper<Item> mItem = world.getMapper(Item.class);

        MyLogger.getInstance().log("MapBuilder", "place " + (map.getLevel() + 2) + " random items");
        List<Integer> placedItems = new ArrayList<Integer>();
        int counter = 0;
        while (counter < map.getLevel() + 2) {
            int rand = random.getNextPositiveRand(allItemsToLevel.size() - 1);
            int ent = allItemsToLevel.get(rand);
            if (!placedItems.contains(ent)) {
                Item item = mItem.get(ent);
                int entItem = factory.createItemByTypeId(item.typeId, world, level, localRand);
                placeItemInRoom(world, getRandomRoom(0), entItem);
                counter++;
            }
        }

        // one additional heal salve
        int entItem = factory.createItemByTypeId(0, world);
        placeItemInRoom(world, getRandomRoom(0), entItem);

        // Traps
        trapCounter = Math.min(16 - trapCounter, map.getLevel() + 2);
        for (int i = 0; i < trapCounter; i++) {
            // random level of trap
            int randomTrap = localRand.nextInt(map.getLevel()) + 1;
            placeTrapInRoom(world, getRandomRoom(1), factory.createTrapsByLevel(randomTrap, world).get(0));
        }
    }

    public void placeTreasureInRoom(World world, Room room, int entityId) {
        int randomX = random.getNextPositiveRand(1, room.getTileCountX() - 2);
        int randomY = random.getNextPositiveRand(1, room.getTileCountY() - 2);

        ComponentMapper<Position> mPosition = world.getMapper(Position.class);
        Position pos = mPosition.get(entityId);
        pos.xWorld = (room.getTileX() + randomX) * map.getTileWidthPixel();
        pos.yWorld = (room.getTileY() + randomY) * map.getTileHeightPixel();
        room.addItem(entityId);
        pos.currentRoom = room;
        pos.updateBoundingBox();

        ComponentMapper<Treasure> mTreasure = world.getMapper(Treasure.class);
        Treasure treas = mTreasure.get(entityId);
        if (treas.trapId != -1) {
            // place the trap to same location
            Position trapPos = mPosition.get(treas.trapId);
            room.addTrap(treas.trapId);
            trapPos.setPositionTo(pos);
            MyLogger.getInstance().log("MapBuilder", "placed Trap: in room: " + room.getRoomNumber() + ": " + pos.xWorld + "/"
                    + pos.yWorld);
        }
        MyLogger.getInstance().log("MapBuilder", "placed Treasure: " + treas.qualityName + " " + treas.name + " in room: " + room.getRoomNumber()
                + ": " + pos.xWorld + "/" + pos.yWorld);
    }

    public void placeMonsterInRoom(World world, Room room, int entityId) {
        int randomX = random.getNextPositiveRand(1, room.getTileCountX() - 2);
        int randomY = random.getNextPositiveRand(1, room.getTileCountY() - 2);

        ComponentMapper<Position> mPosition = world.getMapper(Position.class);
        Position pos = mPosition.get(entityId);
        pos.xWorld = (room.getTileX() + randomX) * map.getTileWidthPixel();
        pos.yWorld = (room.getTileY() + randomY) * map.getTileHeightPixel();
        room.addMonster(entityId);
        pos.currentRoom = room;
        pos.updateBoundingBox();

        ComponentMapper<Monster> mMonster = world.getMapper(Monster.class);
        Monster monster = mMonster.get(entityId);
        MyLogger.getInstance().log("MapBuilder", "placed Monster: " + monster.name + " in room: " + room.getRoomNumber() + ": " + pos.xWorld + "/"
                + pos.yWorld);
    }

    private Room getRandomRoomWithoutMonster() {
        Room result = null;
        boolean empty = false;
        while (!empty) {
            // not the starting room
            result = getRandomRoom(1);
            empty = !result.hasMonster();
        }
        return result;
    }

    /**
     * This method returns a random room
     *
     * @param startRoom
     * @return
     */
    private Room getRandomRoom(int startRoom) {
        int roomNr = random.getNextPositiveRand(startRoom, allRooms.size());
        //roomNr = Math.abs(random.getNextRand(allRooms.size()));
        Room room = this.allRooms.get(roomNr);
        return room;
    }

    public void placeItemInRoom(World world, Room room, int entityId) {
        int randomX = random.getNextPositiveRand(1, room.getTileCountX() - 2);
        int randomY = random.getNextPositiveRand(1, room.getTileCountY() - 2);

        ComponentMapper<Position> mPosition = world.getMapper(Position.class);
        Position pos = mPosition.get(entityId);
        pos.xWorld = (room.getTileX() + randomX) * map.getTileWidthPixel();
        pos.yWorld = (room.getTileY() + randomY) * map.getTileHeightPixel();
        room.addItem(entityId);
        pos.currentRoom = room;
        pos.updateBoundingBox();

        ComponentMapper<Item> mItem = world.getMapper(Item.class);
        Item item = mItem.get(entityId);

        if (item.trapId != -1) {
            // place the trap to same location
            Position trapPos = mPosition.get(item.trapId);
            room.addTrap(item.trapId);
            trapPos.setPositionTo(pos);
            MyLogger.getInstance().log("MapBuilder", "placed Trap: in room: " + room.getRoomNumber() + ": " + pos.xWorld + "/"
                    + pos.yWorld);
        }
        MyLogger.getInstance().log("MapBuilder", "placed Item: " + item.name + " in room: " + room.getRoomNumber() + ": " + pos.xWorld + "/"
                + pos.yWorld);
    }

    public void placeTrapInRoom(World world, Room room, int entityId) {
        int randomX = random.getNextPositiveRand(1, room.getTileCountX() - 2);
        int randomY = random.getNextPositiveRand(1, room.getTileCountY() - 2);

        ComponentMapper<Position> mPosition = world.getMapper(Position.class);
        Position pos = mPosition.get(entityId);
        pos.xWorld = (room.getTileX() + randomX) * map.getTileWidthPixel();
        pos.yWorld = (room.getTileY() + randomY) * map.getTileHeightPixel();
        room.addTrap(entityId);
        pos.currentRoom = room;
        pos.updateBoundingBox();

        ComponentMapper<Trap> mTrap = world.getMapper(Trap.class);
        Trap trap = mTrap.get(entityId);
        MyLogger.getInstance().log("MapBuilder", "placed Trap: " + trap.name + " in room: " + room.getRoomNumber() + ": " + pos.xWorld + "/"
                + pos.yWorld);
    }

    /**
     * rand 4 => check: n, w, s, e - check east = true : x+width, y+height/2 =>
     * 6 platz in jede Richtung north, south, east - if (check == true) build
     * East-Room - if buildOk then nextRoom else check next direction, if all 4
     * checked => choose random room
     *
     * @return true when
     */
    public boolean buildRooms() {
        boolean result = true;
        buildFirstRoom();
        MapUtils.writeRoomCells(map, firstRoom);
        Room currentRoom = firstRoom;

        for (int i = 0; i < 100; i++) {
            if (allRooms.size() >= maxRooms) {
                break;
            }
            Room nextRoom = null;
            List<Character> roomSides = new ArrayList<Character>(4);
            roomSides.add(NORTH);
            roomSides.add(EAST);
            roomSides.add(SOUTH);
            roomSides.add(WEST);
            while (roomSides.size() > 0 && nextRoom == null) {
                int rand = Math.abs(random.getNextRand(roomSides.size()));
                Character dir = roomSides.get(rand);
                if (EAST.equals(dir)) {
                    MyLogger.getInstance().log("MapBuilder", "- trying EAST");
                    if (checkEastRoom(currentRoom)) {
                        nextRoom = buildEastRoom(currentRoom);
                    }
                } else if (SOUTH.equals(dir)) {
                    MyLogger.getInstance().log("MapBuilder", "- trying SOUTH");
                    if (checkSouthRoom(currentRoom)) {
                        nextRoom = buildSouthRoom(currentRoom);
                    }
                } else if (WEST.equals(dir)) {
                    MyLogger.getInstance().log("MapBuilder", "- trying WEST");
                    if (checkWestRoom(currentRoom)) {
                        nextRoom = buildWestRoom(currentRoom);
                    }
                } else if (NORTH.equals(dir)) {
                    MyLogger.getInstance().log("MapBuilder", "- trying NORTH");
                    if (checkNorthRoom(currentRoom)) {
                        nextRoom = buildNorthRoom(currentRoom);
                    }
                }
                roomSides.remove(dir);
            }
            if (nextRoom != null) {
                // add the room to roomlist
                nextRoom.setRoomNumber(nextRoomNumber++);
                MyLogger.getInstance().log("MapBuilder", "build room:" + nextRoom.getRoomNumber());
                doorRoomList.add(nextRoom);
                currentRoom = nextRoom;
            } else {
                // no more possible rooms to build
                MyLogger.getInstance().log("MapBuilder", "No more possible exits for room: " + currentRoom.getRoomNumber());
                doorRoomList.remove(currentRoom);
                if (doorRoomList.size() == 0) {
                    MyLogger.getInstance().log("MapBuilder", "no more rooms. Build: " + allRooms.size() + " rooms.");
                    return false;
                }
                MyLogger.getInstance().log("MapBuilder", "NULL room");
                int rand = Math.abs(random.getNextRand(doorRoomList.size()));
                currentRoom = doorRoomList.get(rand);
            }
            if (nextRoom != null) {
                allRooms.add(nextRoom);
                nextRoom.setVisible(false);
                MapUtils.writeRoomCells(map, nextRoom);
            }
        }
        MyLogger.getInstance().log("MapBuilder", "Builded " + allRooms.size() + " rooms.");
        return result;
    }

    /**
     * This method builds the first room in the dungeon
     */
    private void buildFirstRoom() {
        for (int y = 0; y < map.getHeight(); y++) {
            for (int x = 0; x < map.getWidth(); x++) {
                Tile tile = new Tile(null, Tile.CELL_UNWALKABLE);
                map.setCell(x, y, tile);
            }
        }
        firstRoom = new Room();
        firstRoom.setTileX(25 + random.getNextRand(5));
        firstRoom.setTileY(25 + random.getNextRand(5));
        firstRoom.setTileCountX(ROOM_WIDTH + random.getNextRand(5));
        firstRoom.setTileCountY(ROOM_HEIGHT + random.getNextRand(5));
        firstRoom.setRoomNumber(nextRoomNumber++);
        allRooms.add(firstRoom);
        firstRoom.setVisible(true);
        map.setVisible(firstRoom, true);
    }

    private boolean checkNorthRoom(Room curRoom) {
        return curRoom.getExitNorth() == null && checkRoom(curRoom.getTileX(), curRoom.getTileY() - checkHeight);
    }

    private boolean checkSouthRoom(Room curRoom) {
        return curRoom.getExitSouth() == null
                && checkRoom(curRoom.getTileX(), curRoom.getTileY() + curRoom.getTileCountY());
    }

    private boolean checkWestRoom(Room curRoom) {
        return curRoom.getExitWest() == null && checkRoom(curRoom.getTileX() - checkHeight, curRoom.getTileY());
    }

    private boolean checkEastRoom(Room curRoom) {
        return curRoom.getExitEast() == null
                && checkRoom(curRoom.getTileX() + curRoom.getTileCountX(), curRoom.getTileY());
    }

    private boolean checkRoom(int x, int y) {
        boolean result = x >= 0 && x <= map.getWidth() - checkWidth && y >= 0 && y <= map.getHeight() - checkHeight;
        if (result == false) {
            return result;
        }
        for (int i = x + 1; i < x + checkWidth; i++) {
            for (int j = y + 1; j < y + checkHeight; j++) {
                if (map.getTileTypeAt(i, j) != Tile.CELL_UNWALKABLE) {
                    return false;
                }
            }
        }
        return true;
    }

    private Room buildEastRoom(Room curRoom) {
        Room nextRoom = new Room();
        nextRoom.setTileCountX(ROOM_WIDTH + random.getNextRand(5));
        nextRoom.setTileCountY(ROOM_HEIGHT + random.getNextRand(5));
        nextRoom.setTileX(curRoom.getTileX() + curRoom.getTileCountX() - 1);
        nextRoom.setTileY(curRoom.getTileY() + random.getNextRand(4));
        if (!checkRoomInMap(nextRoom)) {
            return null;
        }
        nextRoom = narrowRoomInMap(nextRoom, WEST);
        if (nextRoom == null) {
            return null;
        }
        int doorType = buildDoorType();
        int x = curRoom.getTileX() + curRoom.getTileCountX() - 1;
        int y = Math.max(curRoom.getTileY(), nextRoom.getTileY()) + 1;
        int yTo = Math
                .min(curRoom.getTileY() + curRoom.getTileCountY(), nextRoom.getTileY() + nextRoom.getTileCountY()) - 1;
        Door door = new Door(curRoom, nextRoom, doorType, x, y, 1, yTo - y);
        curRoom.setExitEast(door);
        nextRoom.setExitWest(door);
        return nextRoom;
    }

    private Room narrowRoomInMap(Room nextRoom, Character comeFrom) {
        Room room = nextRoom;
        int limit = 3;
        boolean isOk = true;
        int counter = 0;
        if (!NORTH.equals(comeFrom)) {
            // narrow north wall
            counter = 0;
            do {
                isOk = true;
                for (int i = 0; i < nextRoom.getTileCountX(); i++) {
                    int cell = map.getTileTypeAt(i + nextRoom.getTileX(), nextRoom.getTileY());
                    if (cell == Tile.CELL_FLOOR) {
                        isOk = false;
                        break;
                    }
                }
                if (!isOk) {
                    // try next row
                    nextRoom.setTileY(nextRoom.getTileY() + 1);
                    nextRoom.setTileCountY(nextRoom.getTileCountY() - 1);
                }
                counter++;
            } while (isOk == false && counter < limit);
            if (!isOk) {
                return null;
            }
        }
        if (!SOUTH.equals(comeFrom)) {
            // narrow north wall
            counter = 0;
            do {
                isOk = true;
                for (int i = 0; i < nextRoom.getTileCountX(); i++) {
                    int cell = map.getTileTypeAt(i + nextRoom.getTileX(),
                            nextRoom.getTileY() + nextRoom.getTileCountY());
                    if (cell == Tile.CELL_FLOOR) {
                        isOk = false;
                        break;
                    }
                }
                if (!isOk) {
                    // try next row
                    nextRoom.setTileCountY(nextRoom.getTileCountY() - 1);
                }
                counter++;
            } while (isOk == false && counter < limit);
            if (!isOk) {
                return null;
            }
        }
        if (!WEST.equals(comeFrom)) {
            // narrow north wall
            counter = 0;
            do {
                isOk = true;
                for (int i = 0; i < nextRoom.getTileCountY(); i++) {
                    int cell = map.getTileTypeAt(nextRoom.getTileX(), nextRoom.getTileY() + i);
                    if (cell == Tile.CELL_FLOOR) {
                        isOk = false;
                        break;
                    }
                }
                if (!isOk) {
                    // try next row
                    nextRoom.setTileX(nextRoom.getTileX() + 1);
                    nextRoom.setTileCountX(nextRoom.getTileCountX() - 1);
                }
                counter++;
            } while (isOk == false && counter < limit);
            if (!isOk) {
                return null;
            }
        }
        if (!EAST.equals(comeFrom)) {
            // narrow north wall
            counter = 0;
            do {
                isOk = true;
                for (int i = 0; i < nextRoom.getTileCountY(); i++) {
                    int cell = map.getTileTypeAt(nextRoom.getTileX() + nextRoom.getTileCountX(), nextRoom.getTileY()
                            + i);
                    if (cell == Tile.CELL_FLOOR) {
                        isOk = false;
                        break;
                    }
                }
                if (!isOk) {
                    // try next row
                    nextRoom.setTileCountX(nextRoom.getTileCountX() - 1);
                }
                counter++;
            } while (isOk == false && counter < limit);
            if (!isOk) {
                return null;
            }
        }
        if (nextRoom.getTileCountY() < 6 || nextRoom.getTileCountX() < 6) {
            return null;
        }
        return room;
    }

    /**
     * This method checks the new room is valid inside the map dimensions
     *
     * @param nextRoom
     * @return
     */
    private boolean checkRoomInMap(Room nextRoom) {
        if (nextRoom.getTileX() < 0 || nextRoom.getTileX() + nextRoom.getTileCountX() >= map.getWidth()
                || nextRoom.getTileY() < 0 || nextRoom.getTileY() + nextRoom.getTileCountY() >= map.getHeight()) {
            return false;
        } else {
            return true;
        }
    }

    private int buildDoorType() {
        int rand = random.getNextRand(32);
        if (rand == 31) {
            return Tile.CELL_HIDDENDOOR;
        } else if (rand >= 27) {
            return Tile.CELL_DOOR;
        } else {
            return Tile.CELL_DOOR_OPENED;
        }
    }

    private Room buildWestRoom(Room curRoom) {
        Room nextRoom = new Room();
        nextRoom.setTileCountX(ROOM_WIDTH + random.getNextRand(5));
        nextRoom.setTileCountY(ROOM_HEIGHT + random.getNextRand(5));
        nextRoom.setTileX(curRoom.getTileX() - nextRoom.getTileCountX() + 1);
        nextRoom.setTileY(curRoom.getTileY() + random.getNextRand(4));
        if (!checkRoomInMap(nextRoom)) {
            return null;
        }
        nextRoom = narrowRoomInMap(nextRoom, EAST);
        if (nextRoom == null) {
            return null;
        }
        int doorType = buildDoorType();
        int x = curRoom.getTileX();
        int y = Math.max(curRoom.getTileY(), nextRoom.getTileY()) + 1;
        int yTo = Math
                .min(curRoom.getTileY() + curRoom.getTileCountY(), nextRoom.getTileY() + nextRoom.getTileCountY()) - 1;
        Door door = new Door(curRoom, nextRoom, doorType, x, y, 1, yTo - y);
        curRoom.setExitWest(door);
        nextRoom.setExitEast(door);
        return nextRoom;
    }

    private Room buildNorthRoom(Room curRoom) {
        Room nextRoom = new Room();
        nextRoom.setTileCountX(ROOM_WIDTH + random.getNextRand(5));
        nextRoom.setTileCountY(ROOM_HEIGHT + random.getNextRand(5));
        nextRoom.setTileX(curRoom.getTileX() + random.getNextRand(4) - 1);
        nextRoom.setTileY(curRoom.getTileY() - nextRoom.getTileCountY() + 1);
        if (!checkRoomInMap(nextRoom)) {
            return null;
        }
        nextRoom = narrowRoomInMap(nextRoom, SOUTH);
        if (nextRoom == null) {
            return null;
        }
        int doorType = buildDoorType();
        int y = curRoom.getTileY();
        int x = Math.max(curRoom.getTileX(), nextRoom.getTileX()) + 1;
        int xTo = Math
                .min(curRoom.getTileX() + curRoom.getTileCountX(), nextRoom.getTileX() + nextRoom.getTileCountX()) - 1;
        Door door = new Door(curRoom, nextRoom, doorType, x, y, xTo - x, 1);
        curRoom.setExitNorth(door);
        nextRoom.setExitSouth(door);
        return nextRoom;
    }

    private Room buildSouthRoom(Room curRoom) {
        Room nextRoom = new Room();
        nextRoom.setTileCountX(ROOM_WIDTH + random.getNextRand(5));
        nextRoom.setTileCountY(ROOM_HEIGHT + random.getNextRand(5));
        nextRoom.setTileX(curRoom.getTileX() + random.getNextRand(4) - 1);
        nextRoom.setTileY(curRoom.getTileY() + curRoom.getTileCountY() - 1);
        if (!checkRoomInMap(nextRoom)) {
            return null;
        }
        nextRoom = narrowRoomInMap(nextRoom, NORTH);
        if (nextRoom == null) {
            return null;
        }
        int doorType = buildDoorType();
        int y = curRoom.getTileY() + curRoom.getTileCountY() - 1;
        int x = Math.max(curRoom.getTileX(), nextRoom.getTileX()) + 1;
        int xTo = Math
                .min(curRoom.getTileX() + curRoom.getTileCountX(), nextRoom.getTileX() + nextRoom.getTileCountX()) - 1;
        Door door = new Door(curRoom, nextRoom, doorType, x, y, xTo - x, 1);
        curRoom.setExitSouth(door);
        nextRoom.setExitNorth(door);
        return nextRoom;
    }

}
