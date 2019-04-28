package de.lambdamoo.gta.client.util;

import java.util.Random;


public class RandomGen {
    private int _levelSeed = -1;
    private int _level = -1;
    private int _dungeon = -1;
    private Random random = null;

    public RandomGen(int level, int dungeon, int seed) {
        _level = level;
        _dungeon = dungeon;
        this._levelSeed = _level * _dungeon * seed;
    }

    public int getNextPositiveRand(int max) {
        int rand = Math.abs(getNextRand(max));
        return rand;
    }

    public int getNextRand(int max) {
        int value = getInstance().nextInt(max);
        return value;
    }

    private Random getInstance() {
        if (random == null) {
            random = new Random(_levelSeed);
        }
        return random;
    }

    public int getNextPositiveRand(int min, int max) {
        int rand = Math.abs(getNextRand(max - min)) + min;
        return rand;
    }
}
