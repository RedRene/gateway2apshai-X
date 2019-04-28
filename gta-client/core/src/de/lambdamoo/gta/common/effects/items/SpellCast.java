package de.lambdamoo.gta.common.effects.items;

import de.lambdamoo.gta.common.action.BaseItemEffect;
import de.lambdamoo.gta.common.effects.player.PlayerEffect;
import de.lambdamoo.gta.world.GameWorld;
import de.lambdamoo.gta.world.components.Item;
import de.lambdamoo.gta.world.components.Monster;

public class SpellCast extends BaseItemEffect {
    private Spell spell = null;

    public SpellCast(Spell spellName) {
        super();
        this.spell = spellName;
    }

    public boolean use(Item item, GameWorld mg) {
        mg.addMessage("You are casting " + spell);
        switch (spell) {
            case Stun:
                mg.applyAI(Monster.AI.Static);
                break;
            case Map:
                mg.showMap();
                break;
            case Confuse:
                mg.applyAI(Monster.AI.Confuse);
                break;
            case Shield:
                mg.getActionSystem().playerAddEffect(new PlayerEffect("protect", 10, Spell.Shield) {
                    @Override
                    public boolean applyEffect() {
                        return true;
                    }

                    @Override
                    public boolean removeEffect() {
                        return false;
                    }
                });
                break;
            case Fear:
                mg.applyAI(Monster.AI.Fear);
                break;
            case Disarm:
                mg.getActionSystem().disarmTraps();
                break;
            case Paralyze:
                mg.applyAI(Monster.AI.Static);
                break;
            case Teleport:
                // Random room
                mg.teleportPlayerRandom();
                break;
            case Protect:
                mg.getActionSystem().playerAddEffect(new PlayerEffect("protect", 10, Spell.Protect) {
                    @Override
                    public boolean applyEffect() {
                        return true;
                    }

                    @Override
                    public boolean removeEffect() {
                        return false;
                    }
                });
                break;
            case Stone:
                mg.applyAI(Monster.AI.Static);
                break;
            case Death:
                mg.killActiveMonsters();
                break;
            case Blast:
                break;
            case Reflect:
                mg.getActionSystem().playerAddEffect(new PlayerEffect("reflect", 5, Spell.Reflect) {
                    @Override
                    public boolean applyEffect() {
                        return true;
                    }

                    @Override
                    public boolean removeEffect() {
                        return false;
                    }
                });
                break;
        }

        return false;
    }


    public enum Spell {Stun, Map, Confuse, Shield, Fear, Disarm, Paralyze, Teleport, Protect, Stone, Death, Blast, Reflect, Poison}
}