package de.lambdamoo.gta.client;

import com.badlogic.gdx.Gdx;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import de.lambdamoo.gta.client.util.ComparatorHighScore;
import de.lambdamoo.gta.client.util.HighScoreEntry;
import de.lambdamoo.gta.client.util.MyLogger;
import de.lambdamoo.gta.client.util.PlayServiceListener;
import de.lambdamoo.gta.client.util.PlayServices;
import mk.gdx.firebase.GdxFIRApp;
import mk.gdx.firebase.GdxFIRAuth;
import mk.gdx.firebase.GdxFIRDatabase;
import mk.gdx.firebase.annotations.MapConversion;
import mk.gdx.firebase.auth.GdxFirebaseUser;
import mk.gdx.firebase.callbacks.AuthCallback;
import mk.gdx.firebase.callbacks.DataCallback;
import mk.gdx.firebase.database.FilterType;
import mk.gdx.firebase.database.OrderByMode;
import mk.gdx.firebase.deserialization.FirebaseMapConverter;

public class AndroidPlayServices implements PlayServices {

    private List<HighScoreEntry> listHighscores = Collections.synchronizedList(new ArrayList<HighScoreEntry>(50));
    private ComparatorHighScore comparatorHighScore = new ComparatorHighScore();

    public AndroidPlayServices() {
    }

    @Override
    public void signIn(PlayServiceListener listener) {
        GdxFIRAuth.instance().google().signIn(new AuthCallback() {
            @Override
            public void onSuccess(GdxFirebaseUser user) {
                // Deal with with current user.
                String userDisplayName = user.getUserInfo().getDisplayName();
            }

            @Override
            public void onFail(Exception e) {
                // handle failure
                MyLogger.getInstance().log(e);
            }
        });
    }

    public void loadLeaderboard(final PlayServiceListener listener) {
        GdxFIRDatabase.instance().inReference("highscores")
                .filter(FilterType.LIMIT_FIRST, 20)
                .orderBy(OrderByMode.ORDER_BY_KEY, null)
                .readValue(List.class, new DataCallback<List<HighScoreEntry>>() {
                    @MapConversion(HighScoreEntry.class)
                    @Override
                    public void onData(List<HighScoreEntry> list) {
                        MyLogger.getInstance().log("loadLeaderboard", "success");
                        listHighscores.clear();
                        listHighscores.addAll(list);
                        Collections.sort(listHighscores, comparatorHighScore);
                        Gdx.app.postRunnable(new Runnable() {
                            @Override
                            public void run() {
                                listener.onComplete();
                            }
                        });
                    }

                    @Override
                    public void onError(Exception e) {
                        MyLogger.getInstance().log(e);
                    }
                });
    }

    @Override
    public void signOut(PlayServiceListener listener) {

    }

    @Override
    public boolean isSignedIn() {
        return true;
    }

    public void saveScore(String name, int score) {
        GdxFIRDatabase.instance().inReference("highscores/" + name).push().setValue(new HighScoreEntry(name, score));
    }

    @Override
    public List<HighScoreEntry> getListScore() {
        return listHighscores;
    }

    /**
     * This method inits the google Firebase
     */
    public void init() {
        GdxFIRApp.instance().configure();

        GdxFIRDatabase.instance().setMapConverter(new FirebaseMapConverter() {
            @Override
            public <T> T convert(Map<String, Object> map, Class<T> wantedType) {
                return (T) parseHighScore(map);
            }

            @Override
            public Map<String, Object> unConvert(Object object) {
                return null;
            }
        });
    }

    private HighScoreEntry parseHighScore(Map mapEntry) {
        HighScoreEntry result = new HighScoreEntry();
        result.setName((String) mapEntry.get("name"));
        result.setScore((Long) mapEntry.get("score"));
        return result;
    }
}
