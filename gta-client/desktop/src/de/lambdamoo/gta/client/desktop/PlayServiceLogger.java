package de.lambdamoo.gta.client.desktop;

import java.util.ArrayList;
import java.util.List;

import de.lambdamoo.gta.client.util.ComparatorHighScore;
import de.lambdamoo.gta.client.util.HighScoreEntry;
import de.lambdamoo.gta.client.util.MyLogger;
import de.lambdamoo.gta.client.util.PlayServiceListener;
import de.lambdamoo.gta.client.util.PlayServices;

public class PlayServiceLogger implements PlayServices {

    private List<HighScoreEntry> listScore = new ArrayList<HighScoreEntry>();
    private ComparatorHighScore comparatorHighScore = new ComparatorHighScore();

    @Override
    public void loadLeaderboard(PlayServiceListener listener) {
        listener.onComplete();
    }

    @Override
    public void saveScore(String name, int score) {
        MyLogger.getInstance().log("PlayServiceLogger", name + "=" + score);
        listScore.add(new HighScoreEntry(name, score));
        listScore.sort(comparatorHighScore);
    }

    @Override
    public void signIn(PlayServiceListener listener) {
        listener.onComplete();
    }

    @Override
    public void signOut(PlayServiceListener listener) {
        listener.onComplete();
    }

    @Override
    public boolean isSignedIn() {
        return true;
    }

    @Override
    public void init() {

    }

    public List<HighScoreEntry> getListScore() {
        return listScore;
    }
}
