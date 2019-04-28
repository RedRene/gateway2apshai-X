package de.lambdamoo.gta.common.effects.player;

import de.lambdamoo.gta.common.effects.items.SpellCast;

public abstract class PlayerEffect {
    public boolean infinite = false;
    public SpellCast.Spell spell = null;
    public String name = null;
    private float durationSeconds = 0;
    private float durationLeftSeconds = 0;

    public PlayerEffect(String name, float durationSeconds, SpellCast.Spell spell) {
        this.name = name;
        this.durationSeconds = durationSeconds;
        this.spell = spell;
        this.durationLeftSeconds = durationSeconds;
    }

    public void resetDuration() {
        this.durationLeftSeconds = durationSeconds;
    }

    /**
     * This method applies the effect. It returns true if the effect is consumed and no other effects are necessary
     *
     * @return
     */
    abstract public boolean applyEffect();

    abstract public boolean removeEffect();

    public void reduceDurationLeft(float time) {
        this.durationLeftSeconds -= time;
    }

    public boolean isExpired() {
        return !infinite && durationLeftSeconds < 0;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PlayerEffect that = (PlayerEffect) o;
        return name.equals(that.name);
    }

    @Override
    public int hashCode() {
        return name.hashCode();
    }
}
