package de.lambdamoo.gta.world.components;

import com.artemis.Component;

import de.lambdamoo.gta.common.action.ItemEffect;

public class Item extends Component {

    public boolean consumed = false;
    public ItemEffect effect = null;
    public int level = -1;
    public String name = null;
    public int spriteIndex = -1;
    public boolean trapped = false;
    public int amount = 1;
    public int typeId;
    public String typeName = null;
    public ItemGroup itemGroup = null;
    public boolean usable = false;
    public boolean isEquippable = false;
    public int trapId = -1;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Item item = (Item) o;
        return name.equals(item.name);
    }

    @Override
    public int hashCode() {
        return name.hashCode();
    }

    public enum ItemGroup {Weapon, Armor, Helm, Shield, Gauntlet, Wand, Spell, Potion, Arrow, OtherUsable, OtherEquipable}
}
