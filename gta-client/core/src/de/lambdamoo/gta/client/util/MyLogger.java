package de.lambdamoo.gta.client.util;

import com.badlogic.gdx.Gdx;

import java.util.ArrayList;
import java.util.List;

public class MyLogger {
    static private MyLogger instance = new MyLogger();
    private List<String> buffer = new ArrayList<String>();

    public static MyLogger getInstance() {
        return instance;
    }

    public void log(String tag, String message) {
        if (Gdx.app != null) {
            Gdx.app.log("GTA:" + tag, message);
        } else {
            buffer.add("GTA:" + tag + message);
        }
        flush();
    }

    private void flush() {
        if (Gdx.app != null && buffer.size() > 0) {
            for (int i = 0; i < buffer.size(); i++) {
                Gdx.app.log("Buffer", buffer.get(i));
            }
            buffer.clear();
        }
    }

    public void log(Throwable exc) {
        if (Gdx.app != null) {
            Gdx.app.log("GTA:" + "ERROR", "There is an error", exc);
        } else {
            buffer.add("GTA:" + "ERROR" + exc.getMessage());
        }
        flush();
    }

    public void log(String tag, String message, Throwable exc) {
        if (Gdx.app != null) {
            Gdx.app.log("GTA:" + tag, message, exc);
        } else {
            buffer.add("GTA:" + "tag" + message + " " + exc.getMessage());
        }
        flush();
    }
}
