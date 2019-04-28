package de.lambdamoo.gta.client.util;

import java.util.List;

public interface PlayServices {
    void saveScore(String name, int score);

    List<HighScoreEntry> getListScore();

    void signIn(PlayServiceListener listener);

    void signOut(PlayServiceListener listener);

    boolean isSignedIn();

    void init();

    void loadLeaderboard(PlayServiceListener listener);

    enum ServiceEvents {LeaderboardLoaded, SignInComplete}

}
