package de.lambdamoo.gta.client.util;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;

import java.util.Hashtable;
import java.util.Random;

public class SoundManager {

    private Hashtable<Sounds, Sound> loadedSounds = new Hashtable<Sounds, Sound>(15);
    private Random random = new Random();
    private float soundVolume = 1.0f;

    public SoundManager() {
        loadedSounds.put(Sounds.Secretdoor_detect, loadSound("search_spell.mp3"));
        loadedSounds.put(Sounds.Secretdoor_found, loadSound("secretdoor.mp3"));
        loadedSounds.put(Sounds.Trap_detect, loadSound("search_spell.mp3"));
        loadedSounds.put(Sounds.Trap_found, loadSound("locate_trap.mp3"));
        loadedSounds.put(Sounds.Encounter, loadSound("encounter_mob.mp3"));
        loadedSounds.put(Sounds.Opendoor, loadSound("open_door.mp3"));
        loadedSounds.put(Sounds.Pickitem, loadSound("pick_item.mp3"));
        loadedSounds.put(Sounds.Picktreasure, loadSound("pick_treasure.mp3"));
        loadedSounds.put(Sounds.Attack1, loadSound("attack1-pock.mp3"));
        loadedSounds.put(Sounds.Attack2, loadSound("attack2-slash.mp3"));
        loadedSounds.put(Sounds.Attack3, loadSound("attack3-shortslash.mp3"));
        loadedSounds.put(Sounds.Attack4, loadSound("attack4-longslash.mp3"));
        loadedSounds.put(Sounds.Attack5, loadSound("attack5-long.mp3"));
    }

    private Sound loadSound(String name) {
        return Gdx.audio.newSound(Gdx.files.internal("sounds/" + name));
    }

    public float getSoundVolume() {
        return this.soundVolume;
    }

    public void setSoundVolume(float value) {
        this.soundVolume = value;
    }

    public void playRandomAttackSound() {
        int rand = random.nextInt(5);
        Sounds play = null;
        switch (rand) {
            case 0:
                play = Sounds.Attack1;
                break;
            case 1:
                play = Sounds.Attack2;
                break;
            case 2:
                play = Sounds.Attack3;
                break;
            case 3:
                play = Sounds.Attack4;
                break;
            case 4:
                play = Sounds.Attack5;
                break;
        }

        playSound(play);
    }

    public void playSound(Sounds play) {
        Sound sound = loadedSounds.get(play);
        if (sound != null) {
            sound.play(soundVolume);
        }
    }

    public enum Sounds {Secretdoor_detect, Secretdoor_found, Trap_detect, Trap_found, Encounter, Opendoor, Pickitem, Picktreasure, Attack1, Attack2, Attack3, Attack4, Attack5}

}
