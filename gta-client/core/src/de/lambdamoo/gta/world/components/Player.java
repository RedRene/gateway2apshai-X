package de.lambdamoo.gta.world.components;

import com.artemis.Component;

import java.util.ArrayList;
import java.util.List;

import de.lambdamoo.gta.common.effects.player.PlayerEffect;
import de.lambdamoo.gta.world.subcomponents.HeroInventory;

public class Player extends Component {
    public final static int LEVEL_TIME = 6 * 60;
    public String name = null;
    public Boolean trainer = false;
    public int lifes = 5;
    public float time = LEVEL_TIME;
    public int score = 0;
    public boolean amuletStrengthConsumed = false;
    public boolean amuletAgilityConsumed = false;
    public boolean amuletLuckConsumed = false;

    public int baseLuck = 3;

    public int strength = 3;
    public int agility = 3;
    public int luck = 3;
    public int killCount = 0;
    public List<PlayerEffect> listEffects = new ArrayList<PlayerEffect>(5);
    public HeroInventory inventory = new HeroInventory();
    private boolean effectsChanged = false;

    public boolean isEffectsChanged() {
        return effectsChanged;
    }

    public void resetEffectsChanged() {
        this.effectsChanged = false;
    }

    public void addEffect(PlayerEffect effect) {
        int index = this.listEffects.indexOf(effect);
        if (index == -1) {
            // effect is new
            this.listEffects.add(effect);
            this.effectsChanged = true;
        } else {
            // player has effect already
            PlayerEffect oldEff = this.listEffects.get(index);
            oldEff.resetDuration();
        }
    }

    public void removeEffect(PlayerEffect effect) {
        this.listEffects.remove(effect);
        this.effectsChanged = true;
    }


    /**
     * This method raises an ability of the player by one point
     *
     * @param ability
     */
    public String raiseAbility(int ability) {
        String result = null;
        switch (ability) {
            case 0:
                strength++;
                result = "Greater strength";
                break;
            case 1:
                agility++;
                result = "Higher agility";
                break;
            case 2:
                luck++;
                result = "Better luck";
                break;
        }
        return result;
    }

    public void addScore(int score) {
        this.score += score;
    }

    public void decreaseTime(float delta) {
        this.time -= delta;
    }

    public void incKillCount() {
        this.killCount++;
    }

}
