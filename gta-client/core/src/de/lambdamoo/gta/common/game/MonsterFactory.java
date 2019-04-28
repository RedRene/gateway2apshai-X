package de.lambdamoo.gta.common.game;

import com.artemis.ComponentMapper;
import com.artemis.World;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

import de.lambdamoo.gta.client.systems.util.Utils;
import de.lambdamoo.gta.common.action.BaseItemEffect;
import de.lambdamoo.gta.common.action.TrapEffect;
import de.lambdamoo.gta.common.effects.items.AmuletEffectBase;
import de.lambdamoo.gta.common.effects.items.ArmorEffectBase;
import de.lambdamoo.gta.common.effects.items.CrossEffectBase;
import de.lambdamoo.gta.common.effects.items.GauntletEffectBase;
import de.lambdamoo.gta.common.effects.items.HealEffectBase;
import de.lambdamoo.gta.common.effects.items.HelmEffectBase;
import de.lambdamoo.gta.common.effects.items.MagicSwordCurseEffectBase;
import de.lambdamoo.gta.common.effects.items.ShieldEffectBase;
import de.lambdamoo.gta.common.effects.items.SpellCast;
import de.lambdamoo.gta.common.effects.items.WandEffectBase;
import de.lambdamoo.gta.common.effects.items.WeaponEffectBase;
import de.lambdamoo.gta.common.effects.traps.CursedTrapEffect;
import de.lambdamoo.gta.common.effects.traps.DeathTrapEffect;
import de.lambdamoo.gta.common.effects.traps.FreezeTrapEffect;
import de.lambdamoo.gta.common.effects.traps.PitTrapEffect;
import de.lambdamoo.gta.common.effects.traps.PoisonTrapEffect;
import de.lambdamoo.gta.common.effects.traps.StoneTrapEffect;
import de.lambdamoo.gta.common.effects.traps.TeleportTrapEffect;
import de.lambdamoo.gta.common.effects.traps.WeaknessTrapEffect;
import de.lambdamoo.gta.world.components.Attacking;
import de.lambdamoo.gta.world.components.Item;
import de.lambdamoo.gta.world.components.MapObject;
import de.lambdamoo.gta.world.components.Monster;
import de.lambdamoo.gta.world.components.Player;
import de.lambdamoo.gta.world.components.Position;
import de.lambdamoo.gta.world.components.Render;
import de.lambdamoo.gta.world.components.Status;
import de.lambdamoo.gta.world.components.Trap;
import de.lambdamoo.gta.world.components.Treasure;
import de.lambdamoo.gta.world.components.Velocity;

import static de.lambdamoo.gta.common.effects.items.SpellCast.Spell.Confuse;
import static de.lambdamoo.gta.common.effects.items.SpellCast.Spell.Death;
import static de.lambdamoo.gta.common.effects.items.SpellCast.Spell.Disarm;
import static de.lambdamoo.gta.common.effects.items.SpellCast.Spell.Fear;
import static de.lambdamoo.gta.common.effects.items.SpellCast.Spell.Map;
import static de.lambdamoo.gta.common.effects.items.SpellCast.Spell.Paralyze;
import static de.lambdamoo.gta.common.effects.items.SpellCast.Spell.Protect;
import static de.lambdamoo.gta.common.effects.items.SpellCast.Spell.Reflect;
import static de.lambdamoo.gta.common.effects.items.SpellCast.Spell.Shield;
import static de.lambdamoo.gta.common.effects.items.SpellCast.Spell.Stone;
import static de.lambdamoo.gta.common.effects.items.SpellCast.Spell.Stun;
import static de.lambdamoo.gta.common.effects.items.SpellCast.Spell.Teleport;

public class MonsterFactory {

    final private String[] treasureQualities = {"Necklace", "Chest", "Coffer", "Chalice", "Sceptre", "Crown",
            "Plaque"};
    private final Item.ItemGroup[] listEquipable = {Item.ItemGroup.Armor, Item.ItemGroup.Shield, Item.ItemGroup.Weapon, Item.ItemGroup.Gauntlet, Item.ItemGroup.Helm, Item.ItemGroup.Wand, Item.ItemGroup.OtherEquipable};
    private final Item.ItemGroup[] listUsable = {Item.ItemGroup.Potion, Item.ItemGroup.Spell, Item.ItemGroup.OtherUsable};
    private ComponentMapper<Trap> mTrap = null;
    private ComponentMapper<MapObject> mMapObject = null;
    private ComponentMapper<Item> mItem = null;
    private ComponentMapper<Position> mPosition = null;
    private ComponentMapper<Render> mRender = null;
    private ComponentMapper<Velocity> mVelocity = null;
    private ComponentMapper<Attacking> mAttack = null;
    private ComponentMapper<Status> mStatus = null;
    private ComponentMapper<Player> mPlayer = null;
    private ComponentMapper<Monster> mMonster = null;
    private ComponentMapper<Treasure> mTreasure = null;
    private HashMap<String, TrapTemplate> trapTemplates = new HashMap<String, TrapTemplate>(10);
    private HashMap<Integer, ItemTemplate> itemTemplates = new HashMap<Integer, ItemTemplate>(10);
    private HashMap<Integer, MonsterTemplate> monsterTemplates = new HashMap<Integer, MonsterTemplate>(32);
    private HashMap<Integer, TreasureTemplate> treasureTemplates = new HashMap<Integer, TreasureTemplate>(32);

    public MonsterFactory() {
        super();
    }

    public void init(World world) {
        mMapObject = world.getMapper(MapObject.class);
        mTrap = world.getMapper(Trap.class);
        mItem = world.getMapper(Item.class);
        mPosition = world.getMapper(Position.class);
        mRender = world.getMapper(Render.class);
        mVelocity = world.getMapper(Velocity.class);
        mAttack = world.getMapper(Attacking.class);
        mStatus = world.getMapper(Status.class);
        mPlayer = world.getMapper(Player.class);
        mMonster = world.getMapper(Monster.class);
        mTreasure = world.getMapper(Treasure.class);

        trapTemplates.clear();
        addTrapTemplate(new TrapTemplate("Pit Trap", 1, new PitTrapEffect()));
        addTrapTemplate(new TrapTemplate("Stone Trap", 2, new StoneTrapEffect()));
        addTrapTemplate(new TrapTemplate("Teleport Trap", 3, new TeleportTrapEffect()));
        addTrapTemplate(new TrapTemplate("Freeze Trap", 4, new FreezeTrapEffect()));
        addTrapTemplate(new TrapTemplate("Poison Needle", 5, new PoisonTrapEffect()));
        addTrapTemplate(new TrapTemplate("Curse Trap", 6, new CursedTrapEffect()));
        addTrapTemplate(new TrapTemplate("Weakness Trap", 7, new WeaknessTrapEffect()));
        addTrapTemplate(new TrapTemplate("Death Trap", 8, new DeathTrapEffect()));

        monsterTemplates.clear();
        addMonsterTemplate(new MonsterTemplate(0, 1, "Swamp Rat", 2, 2, 0, Monster.AI.Walk, 0.5f, 4, false, 2, 18, 10));
        addMonsterTemplate(new MonsterTemplate(1, 1, "Garter Snake", 3, 1, 0, Monster.AI.Walk, 0.4f, 3, false, 2, 18, 15));
        addMonsterTemplate(new MonsterTemplate(2, 1, "Fungus", 2, 3, 0, Monster.AI.Confuse, 2.4f, 1, false, 2, 18, 9));
        addMonsterTemplate(new MonsterTemplate(3, 1, "Bat", 1, 1, 0, Monster.AI.Confuse, 0.3f, 4, false, 6, 18, 9));

        addMonsterTemplate(new MonsterTemplate(4, 2, "Cave Rat", 3, 3, 1, Monster.AI.Walk, 0.5f, 4, false, 2, 18, 10));
        addMonsterTemplate(new MonsterTemplate(5, 2, "Large Bat", 2, 2, 1, Monster.AI.Randomly, 0.3f, 4, false, 6, 18, 9));
        addMonsterTemplate(new MonsterTemplate(6, 2, "Slime", 3, 6, 2, Monster.AI.Confuse, 3.6f, 1, false, 2, 18, 9));
        addMonsterTemplate(new MonsterTemplate(7, 2, "Spider", 5, 3, 1, Monster.AI.Walk, 0.9f, 3, false, 2, 18, 13));

        addMonsterTemplate(new MonsterTemplate(8, 3, "Ghoul", 3, 4, 2, Monster.AI.Confuse, 0.6f, 1, true, 2, 18, 17));
        addMonsterTemplate(new MonsterTemplate(9, 3, "Ogre", 3, 8, 3, Monster.AI.Walk, 2.4f, 3, false, 2, 18, 17));
        addMonsterTemplate(new MonsterTemplate(10, 3, "Goblin", 3, 3, 0, Monster.AI.Berzerk, 0.4f, 4, false, 2, 18, 17));
        addMonsterTemplate(new MonsterTemplate(11, 3, "Giant", 5, 8, 2, Monster.AI.Confuse, 3.6f, 1, false, 4, 16, 15));

        addMonsterTemplate(new MonsterTemplate(12, 4, "Asp", 6, 7, 2, Monster.AI.Walk, 0.6f, 4, false, 2, 18, 17));
        addMonsterTemplate(new MonsterTemplate(13, 4, "Poison Spider", 3, 8, 3, Monster.AI.Walk, 1.2f, 3, false, 2, 18, 17));
        addMonsterTemplate(new MonsterTemplate(14, 4, "Mummy", 5, 7, 2, Monster.AI.Walk, 1.2f, 3, true, 4, 18, 17));
        addMonsterTemplate(new MonsterTemplate(15, 4, "Priest", 7, 9, 1, Monster.AI.Walk, 2.4f, 3, false, 4, 16, 15));

        addMonsterTemplate(new MonsterTemplate(16, 5, "Rabid Rat", 7, 12, 2, Monster.AI.Walk, 1.8f, 5, false, 2, 18, 10));
        addMonsterTemplate(new MonsterTemplate(17, 5, "Mamba Snake", 10, 10, 2, Monster.AI.Berzerk, 0.6f, 4, false, 2, 18, 15));
        addMonsterTemplate(new MonsterTemplate(18, 5, "Giant Spider", 8, 15, 3, Monster.AI.Walk, 2.4f, 3, false, 2, 18, 13));
        addMonsterTemplate(new MonsterTemplate(19, 5, "Slime Mold", 8, 15, 1, Monster.AI.Confuse, 3.6f, 2, false, 2, 18, 9));

        addMonsterTemplate(new MonsterTemplate(20, 6, "Ghost", 2, 12, 0, Monster.AI.Walk, 0.9f, 2, true, 4, 18, 17));
        addMonsterTemplate(new MonsterTemplate(21, 6, "Vampire Bat", 15, 15, 1, Monster.AI.Randomly, 1.5f, 3, true, 6, 18, 9));
        addMonsterTemplate(new MonsterTemplate(22, 6, "Vampire", 12, 18, 2, Monster.AI.Berzerk, 1.8f, 3, true, 4, 16, 15));
        addMonsterTemplate(new MonsterTemplate(23, 6, "Ghoul", 12, 15, 3, Monster.AI.Walk, 2.4f, 3, true, 2, 18, 17));

        addMonsterTemplate(new MonsterTemplate(24, 7, "Thief", 18, 18, 2, Monster.AI.Confuse, 1.2f, 3, false, 4, 16, 15));
        addMonsterTemplate(new MonsterTemplate(25, 7, "Chef Thief", 20, 20, 3, Monster.AI.Walk, 1.5f, 4, false, 4, 16, 15));
        addMonsterTemplate(new MonsterTemplate(26, 7, "Lackey", 18, 15, 1, Monster.AI.Walk, 1.8f, 2, false, 4, 16, 15));
        addMonsterTemplate(new MonsterTemplate(27, 7, "Warrior", 20, 25, 5, Monster.AI.Berzerk, 1.5f, 4, false, 4, 16, 15));

        addMonsterTemplate(new MonsterTemplate(28, 8, "Dragon", 25, 30, 6, Monster.AI.Walk, 1.8f, 4, false, 2, 18, 12));
        addMonsterTemplate(new MonsterTemplate(29, 8, "Living Statue", 15, 25, 10, Monster.AI.Walk, 1.8f, 2, false, 4, 16, 15));
        addMonsterTemplate(new MonsterTemplate(30, 8, "Wizard", 15, 30, 5, Monster.AI.Walk, 1.5f, 5, false, 4, 16, 15));
        addMonsterTemplate(new MonsterTemplate(31, 8, "Blinking Bat", 15, 22, 2, Monster.AI.Teleport, 0.6f, 5, false, 14, 18, 9));


        treasureTemplates.clear();
        addTreasureTemplate(new TreasureTemplate("Lead", 0, false, 1, 0));
        addTreasureTemplate(new TreasureTemplate("Iron", 1, false, 2, 10));
        addTreasureTemplate(new TreasureTemplate("Bronze", 2, false, 3, 25));
        addTreasureTemplate(new TreasureTemplate("Silver", 3, false, 4, 50));
        addTreasureTemplate(new TreasureTemplate("Golden", 4, false, 5, 100));
        addTreasureTemplate(new TreasureTemplate("Jeweled", 5, true, 6, 1000));

        itemTemplates.clear();
        addItemTemplate(new ItemTemplate(0, "Potion", "Healing Salve", 1, false, 7, new HealEffectBase(), Item.ItemGroup.Potion));
        addItemTemplate(new ItemTemplate(1, "Potion", "Healing Potion", 2, false, 7, new HealEffectBase(), Item.ItemGroup.Potion));
        addItemTemplate(new ItemTemplate(2, "Amulet", "Healing Amulet", 5, false, 10, new HealEffectBase(), Item.ItemGroup.Potion));
        addItemTemplate(new ItemTemplate(3, "Spell", "Healing Spell", 7, false, 3, new HealEffectBase(), Item.ItemGroup.Spell));
        addItemTemplate(new ItemTemplate(4, "Spell", "Stun Spell", 1, true, 3, new SpellCast(Stun), Item.ItemGroup.Spell));
        addItemTemplate(new ItemTemplate(5, "Spell", "Map", 2, false, 3, new SpellCast(Map), Item.ItemGroup.Spell));
        addItemTemplate(new ItemTemplate(6, "Spell", "Confuse Spell", 2, false, 3, new SpellCast(Confuse), Item.ItemGroup.Spell));
        addItemTemplate(new ItemTemplate(7, "Spell", "Shield Spell", 3, false, 3, new SpellCast(Shield), Item.ItemGroup.Spell));
        addItemTemplate(new ItemTemplate(8, "Spell", "Fear Spell", 4, false, 3, new SpellCast(Fear), Item.ItemGroup.Spell));
        addItemTemplate(new ItemTemplate(9, "Spell", "Disarm Spell", 4, false, 3, new SpellCast(Disarm), Item.ItemGroup.Spell));
        addItemTemplate(new ItemTemplate(10, "Spell", "Paralyze Spell", 4, false, 3, new SpellCast(Paralyze), Item.ItemGroup.Spell));
        addItemTemplate(new ItemTemplate(11, "Spell", "Teleport Spell", 6, false, 3, new SpellCast(Teleport), Item.ItemGroup.Spell));
        addItemTemplate(new ItemTemplate(12, "Spell", "Protect Spell", 6, false, 3, new SpellCast(Protect), Item.ItemGroup.Spell));
        addItemTemplate(new ItemTemplate(13, "Spell", "Stone Spell", 8, false, 3, new SpellCast(Stone), Item.ItemGroup.Spell));
        addItemTemplate(new ItemTemplate(14, "Spell", "Death Spell", 8, true, 3, new SpellCast(Death), Item.ItemGroup.Spell));
        //addItemTemplate(new ItemTemplate(15, "Spell", "Blast Spell", 8, true, 3, new SpellCast(Blast), Item.ItemGroup.Spell));
        addItemTemplate(new ItemTemplate(16, "Spell", "Reflect Spell", 8, true, 3, new SpellCast(Reflect), Item.ItemGroup.Spell));
        addItemTemplate(new ItemTemplate(17, "Weapon", "Dagger", 0, false, 2, new WeaponEffectBase(1), Item.ItemGroup.Weapon));
        addItemTemplate(new ItemTemplate(18, "Weapon", "Short Sword", 1, false, 2, new WeaponEffectBase(2), Item.ItemGroup.Weapon));
        addItemTemplate(new ItemTemplate(19, "Weapon", "Long Sword", 3, false, 2, new WeaponEffectBase(3), Item.ItemGroup.Weapon));
        addItemTemplate(new ItemTemplate(20, "Weapon", "2-Hand Sword", 5, false, 2, new WeaponEffectBase(4), Item.ItemGroup.Weapon));
        addItemTemplate(new ItemTemplate(21, "Weapon", "Magic Sword", 7, true, 2, new WeaponEffectBase(5), Item.ItemGroup.Weapon));
        addItemTemplate(new ItemTemplate(22, "Weapon", "Magic Sword ", 7, true, 2, new MagicSwordCurseEffectBase(), Item.ItemGroup.Weapon));
        addItemTemplate(new ItemTemplate(23, "Armor", "Leather Armor", 0, false, 1, new ArmorEffectBase(1), Item.ItemGroup.Armor));
        addItemTemplate(new ItemTemplate(24, "Armor", "Chain Armor", 2, true, 1, new ArmorEffectBase(2), Item.ItemGroup.Armor));
        addItemTemplate(new ItemTemplate(25, "Armor", "Breast Armor", 5, false, 1, new ArmorEffectBase(3), Item.ItemGroup.Armor));
        addItemTemplate(new ItemTemplate(26, "Armor", "Plate Armor", 7, true, 1, new ArmorEffectBase(4), Item.ItemGroup.Armor));
        addItemTemplate(new ItemTemplate(27, "Armor", "Magic Armor", 8, true, 1, new ArmorEffectBase(5), Item.ItemGroup.Armor));
        addItemTemplate(new ItemTemplate(28, "Shield", "Small Shield", 2, false, 8, new ShieldEffectBase(1), Item.ItemGroup.Shield));
        addItemTemplate(new ItemTemplate(29, "Shield", "Large Shield", 3, false, 8, new ShieldEffectBase(2), Item.ItemGroup.Shield));
        addItemTemplate(new ItemTemplate(30, "Shield", "Magic Shield", 6, true, 8, new ShieldEffectBase(3), Item.ItemGroup.Shield));
        addItemTemplate(new ItemTemplate(31, "Helm", "Helm", 4, false, 11, new HelmEffectBase(1), Item.ItemGroup.Helm));
        addItemTemplate(new ItemTemplate(32, "Helm", "Magic Helm", 7, false, 11, new HelmEffectBase(2), Item.ItemGroup.Helm));
        addItemTemplate(new ItemTemplate(33, "Gauntlet", "Gauntlet", 5, false, 9, new GauntletEffectBase(1), Item.ItemGroup.Gauntlet));
        addItemTemplate(new ItemTemplate(34, "Weapon", "Bow", 1, false, 5, new WeaponEffectBase(0), Item.ItemGroup.Weapon));
        addItemTemplate(new ItemTemplate(35, "Arrow", "Arrows", 1, false, 6, null, 6, Item.ItemGroup.Arrow));
        addItemTemplate(new ItemTemplate(36, "Arrow", "Arrows", 4, true, 6, null, 6, Item.ItemGroup.Arrow));

        addItemTemplate(new ItemTemplate(37, "Amulet", "Strength Stone", 5, false, 10, new AmuletEffectBase("Strength"), Item.ItemGroup.OtherUsable));
        addItemTemplate(new ItemTemplate(38, "Amulet", "Agility Amulet", 3, false, 10, new AmuletEffectBase("Agility"), Item.ItemGroup.OtherUsable));
        addItemTemplate(new ItemTemplate(39, "Amulet", "Luck Charm", 3, true, 10, new AmuletEffectBase("Luck"), Item.ItemGroup.OtherUsable));
        addItemTemplate(new ItemTemplate(40, "Cross", "Cross", 6, false, 13, new CrossEffectBase(), Item.ItemGroup.OtherEquipable));
        addItemTemplate(new ItemTemplate(41, "Wand", "Wand", 6, true, 12, new WandEffectBase(1), Item.ItemGroup.Wand));
        addItemTemplate(new ItemTemplate(42, "Staff", "Staff", 6, true, 14, new WandEffectBase(3), Item.ItemGroup.Wand));
    }

    protected void addMonsterTemplate(MonsterTemplate template) {
        this.monsterTemplates.put(template.monsterTypeId, template);
    }

    protected void addTreasureTemplate(TreasureTemplate template) {
        this.treasureTemplates.put(template.quality, template);
    }

    protected void addItemTemplate(ItemTemplate template) {
        this.itemTemplates.put(template.typeId, template);
    }

    protected void addTrapTemplate(TrapTemplate template) {
        this.trapTemplates.put(template.trapName, template);
    }


    public int createMonster(int monsterTypeId, World world) {
        MonsterTemplate templ = monsterTemplates.get(monsterTypeId);

        int e = world.create();

        MapObject mapObj = mMapObject.create(e);
        mapObj.activated = false;
        mapObj.type = MapObject.MapObjectType.Monster;

        Position position = mPosition.create(e);
        position.xWorld = 0;
        position.yWorld = 0;
        position.widthWorld = templ.width;
        position.heightWorld = templ.height;
        position.updateBoundingBox();

        Render render = mRender.create(e);
        render.spriteIndex = monsterTypeId;
        render.maxSpriteIndex = templ.spriteCount;

        Status status = mStatus.create(e);
        status.powerArmor = templ.armorPower;
        status.healthMax = templ.health;
        status.healthCurrent = templ.health;
        status.level = templ.level;
        status.powerWeapon = templ.weaponPower;

        Attacking attack = mAttack.create(e);
        attack.maxAttackCycle = 1;


        Velocity velocity = mVelocity.create(e);
        velocity.velocityXPixel = 0;
        velocity.velocityYPixel = 0;
        velocity.moveSpeed = templ.moveSpeed;

        Monster monst = mMonster.create(e);
        monst.undead = templ.undead;
        monst.ai = templ.ai;
        monst.name = templ.name;

        return e;
    }

    public Item createItemByTypeId(int typeId) {
        Item result = null;
        ItemTemplate templ = itemTemplates.get(typeId);
        if (templ != null) {
            result = createItem(new Item(), templ);
        }
        return result;
    }

    /**
     * This method creates an item without a trap and without adding it to the world.
     *
     * @param template
     * @return the Item object
     */
    private Item createItem(Item item, ItemTemplate template) {
        item.typeId = template.typeId;
        item.effect = template.effect;
        item.level = template.level;
        item.trapped = template.trapped;
        item.typeName = template.typeName;
        if (item.typeName.equals("Arrow")) {
            item.amount = 5;
        }
        item.spriteIndex = template.spriteIndex;
        item.name = template.itemName;
        item.itemGroup = template.itemGroup;
        item.usable = Utils.arrayContains(listUsable, item.itemGroup);
        if (item.name.equals("Bow")) {
            item.isEquippable = false;
        } else {
            item.isEquippable = Utils.arrayContains(listEquipable, item.itemGroup);
        }

        return item;
    }

    /**
     * This method searches the template with the item name and creates a new item without adding it to the world
     *
     * @param name
     * @return
     */
    public Item createItemByName(String name) {
        Item result = null;
        Iterator<ItemTemplate> iter = this.itemTemplates.values().iterator();
        while (iter.hasNext()) {
            ItemTemplate templ = iter.next();
            if (templ.itemName.equalsIgnoreCase(name)) {
                result = createItem(new Item(), templ);
                break;
            }
        }
        return result;
    }

    public int createItemByTypeId(int typeId, World world) {
        int result = -1;
        ItemTemplate templ = itemTemplates.get(typeId);
        if (templ != null) {
            result = createItem(templ, world);
        }
        return result;
    }

    /**
     * This method creates an item without a trap. It should be directly called only for development purposes.
     * Use the one with level and random parameters instead.
     *
     * @param template
     * @param world
     * @return
     */
    private int createItem(ItemTemplate template, World world) {
        int ent = world.create();

        MapObject mapObj = mMapObject.create(ent);
        mapObj.activated = false;
        mapObj.type = MapObject.MapObjectType.Item;

        Item item = createItem(mItem.create(ent), template);

        Position position = mPosition.create(ent);
        position.xWorld = 0;
        position.yWorld = 0;
        position.widthWorld = 16;
        position.heightWorld = 8;
        position.updateBoundingBox();

        Render render = mRender.create(ent);
        render.spriteIndex = template.spriteIndex;
        render.maxSpriteIndex = template.spriteCount;

        return ent;
    }

    public int createItemByTypeId(int typeId, World world, int level, Random random) {
        int result = -1;
        ItemTemplate templ = itemTemplates.get(typeId);
        if (templ != null) {
            result = createItem(templ, world, level, random);
        }
        return result;
    }

    /**
     * This method creates an item with a trap
     *
     * @param template
     * @param world
     * @param level
     * @param random
     * @return
     */
    protected int createItem(ItemTemplate template, World world, int level, Random random) {
        int entId = createItem(template, world);
        // check for trap
        if (template.trapped) {
            Item item = mItem.get(entId);
            TrapTemplate trapTempl = getRandomTrapTemplateByLevel(level, random);
            item.trapId = createTrap(trapTempl, world);
        }
        return entId;
    }

    protected int createTrap(TrapTemplate template, World world) {
        int ent = world.create();

        MapObject mapObj = mMapObject.create(ent);
        mapObj.activated = false;
        mapObj.type = MapObject.MapObjectType.Trap;

        Trap trap = mTrap.create(ent);
        trap.name = template.trapName;
        trap.effect = template.effect;
        trap.level = template.level;

        Position position = mPosition.create(ent);
        position.xWorld = 0;
        position.yWorld = 0;
        position.widthWorld = 16;
        position.heightWorld = 8;
        position.updateBoundingBox();

        Render render = mRender.create(ent);
        render.spriteIndex = template.spriteIndex;
        render.maxSpriteIndex = template.spriteCount;

        return ent;
    }

    /**
     * This method returns a random trap of the dungeon level or lesser.
     *
     * @param level
     * @param random
     * @return
     */
    protected TrapTemplate getRandomTrapTemplateByLevel(int level, Random random) {
        List<TrapTemplate> traps = new ArrayList<TrapTemplate>();
        for (TrapTemplate templ : trapTemplates.values()) {
            if (templ.level == level) {
                traps.add(templ);
            }
        }
        int index = random.nextInt(traps.size());
        return traps.get(index);
    }

    public List<Integer> createTrapsByLevel(int level, World world) {
        List<Integer> traps = new ArrayList<Integer>();
        for (TrapTemplate templ : trapTemplates.values()) {
            if (templ.level == level) {
                int item = createTrap(templ, world);
                traps.add(item);
            }
        }
        return traps;
    }

    public List<Integer> createItemsByLevel(int level, World world, Random random) {
        List<Integer> items = new ArrayList<Integer>();
        for (ItemTemplate templ : itemTemplates.values()) {
            if (templ.level == level) {
                int item = createItem(templ, world, level, random);
                items.add(item);
            }
        }
        return items;
    }

    public int createTreasure(int quality, int type, World world, Random random, int level) {
        TreasureTemplate templ = treasureTemplates.get(quality);

        int ent = world.create();

        MapObject mapObj = mMapObject.create(ent);
        mapObj.activated = false;
        mapObj.type = MapObject.MapObjectType.Treasure;

        Position position = mPosition.create(ent);
        position.xWorld = 0;
        position.yWorld = 0;
        position.widthWorld = 16;
        position.heightWorld = 8;
        position.updateBoundingBox();

        Render render = mRender.create(ent);
        render.spriteIndex = templ.spriteIndex;
        render.maxSpriteIndex = templ.spriteCount;

        Treasure treasure = mTreasure.create(ent);
        treasure.points = templ.points;
        treasure.quality = quality;
        treasure.qualityName = templ.qualityName;
        treasure.score = templ.score;
        treasure.traped = templ.traped;
        // create 50% trapped for golden
        if (quality == 4) {
            if (random.nextBoolean()) {
                TrapTemplate trapTempl = getRandomTrapTemplateByLevel(level, random);
                treasure.trapId = createTrap(trapTempl, world);
            }
        }
        // create trapped treasure for jewelry
        if (quality == 5) {
            TrapTemplate trapTempl = getRandomTrapTemplateByLevel(level, random);
            treasure.trapId = createTrap(trapTempl, world);
        }
        treasure.type = type;
        String name = treasureQualities[random.nextInt(treasureQualities.length)];
        treasure.name = name;

        return ent;
    }

    class MonsterTemplate {
        int monsterTypeId;
        int level;
        String name;
        int health;
        int weaponPower;
        int armorPower;
        int width;
        int height;
        Monster.AI ai;
        float moveSpeed;
        boolean undead = false;
        int spriteCount;

        public MonsterTemplate(int monsterTypeId, int level, String name, int health, int weaponPower, int armorPower,
                               Monster.AI ai, float attackSpeed, float moveSpeed, boolean undead, int spriteCount, int width, int height) {
            super();
            this.monsterTypeId = monsterTypeId;
            this.level = level;
            this.name = name;
            this.health = health;
            this.weaponPower = weaponPower;
            this.armorPower = armorPower;
            this.ai = ai;
            this.moveSpeed = moveSpeed * 10;
            this.undead = undead;
            this.spriteCount = spriteCount;
            this.width = width;
            this.height = height;
        }
    }

    class TreasureTemplate {
        int quality;
        boolean traped;
        int points;
        int score;
        int spriteIndex;
        int spriteCount;
        String name;
        String qualityName;

        public TreasureTemplate(String qualityname, int quality, boolean traped, int points, int score) {
            super();
            this.qualityName = qualityname;
            this.quality = quality;
            this.traped = traped;
            this.points = points;
            this.score = score;
            this.spriteCount = 1;
            this.spriteIndex = 15;
        }
    }

    class TrapTemplate {
        String trapName;
        int level;
        TrapEffect effect;
        int spriteIndex;
        int spriteCount;

        public TrapTemplate(String trapName, int level, TrapEffect effect) {
            this.trapName = trapName;
            this.level = level;
            this.effect = effect;
            this.spriteCount = 1;
            this.spriteIndex = 16;
        }
    }

    class ItemTemplate {
        int typeId;
        String typeName;
        String itemName;
        int level;
        boolean trapped;
        BaseItemEffect effect;
        int spriteIndex;
        int spriteCount;
        int amount;
        Item.ItemGroup itemGroup;

        public ItemTemplate(int type, String typeName, String itemName, int level, boolean trapped, int spriteIndex,
                            BaseItemEffect effect, int amount, Item.ItemGroup itemGroup) {
            this(type, typeName, itemName, level, trapped, spriteIndex, effect, itemGroup);
            this.amount = amount;
        }

        public ItemTemplate(int typeId, String typeName, String itemName, int level, boolean trapped, int spriteIndex,
                            BaseItemEffect effect, Item.ItemGroup itemGroup) {
            super();
            this.typeId = typeId;
            this.typeName = typeName;
            this.itemName = itemName;
            this.level = level;
            this.trapped = trapped;
            this.effect = effect;
            this.spriteIndex = spriteIndex;
            this.spriteCount = 1;
            this.itemGroup = itemGroup;
        }
    }
}
