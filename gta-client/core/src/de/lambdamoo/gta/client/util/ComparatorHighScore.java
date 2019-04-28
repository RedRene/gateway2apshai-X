package de.lambdamoo.gta.client.util;

import java.util.Comparator;

public class ComparatorHighScore implements Comparator<HighScoreEntry> {

    @Override
    public int compare(HighScoreEntry h1, HighScoreEntry h2) {
        return (int) (h2.getScore() - h1.getScore());
    }

}
