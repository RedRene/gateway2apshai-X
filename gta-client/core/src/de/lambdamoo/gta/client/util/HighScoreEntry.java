package de.lambdamoo.gta.client.util;

public class HighScoreEntry {
    private String name = null;
    private long score = 0;

    public HighScoreEntry(String name, long score) {
        this.name = name;
        this.score = score;
    }

    public HighScoreEntry() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getScore() {
        return score;
    }

    public void setScore(long score) {
        this.score = score;
    }
}
