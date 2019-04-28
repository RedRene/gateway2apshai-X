package de.lambdamoo.gta.client.screens.hud;

import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.Align;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import de.lambdamoo.gta.client.util.SpriteImages;

public class MessageTable extends Table {

    private Label[] labelList = new Label[3];
    private List<MyMessage> messages = new ArrayList<MyMessage>(5);

    /**
     * This is the default duration time for showing the messages on the screen
     */
    private float defaultVisibleDuration = 5.0f;


    public void update(float deltaTime) {
        boolean removed = false;
        Iterator<MyMessage> iter = messages.iterator();
        while (iter.hasNext()) {
            MyMessage msg = iter.next();
            msg.duration -= deltaTime;
            if (msg.duration < 0.0f) {
                iter.remove();
                removed = true;
            }
        }
        if (removed) {
            updateLabels();
        }
    }

    /**
     * This method updates the status labels on the screen
     */
    private void updateLabels() {
        int size = messages.size();
        for (int i = 0; i < 3; i++) {
            if (size > i) {
                labelList[i].setText(messages.get(i).message);
            } else {
                labelList[i].setText("");
            }
        }
    }

    /**
     * This method adds a message
     *
     * @param str
     */
    public void addMessage(String str) {
        // add the message str to the list of messages
        messages.add(new MyMessage(defaultVisibleDuration, str));
        if (messages.size() > 3) {
            shrinkMessageList();
        }
        updateLabels();
    }

    /**
     * This method removes the oldest messages to the max. remaining three messages
     */
    private void shrinkMessageList() {
        int toomany = messages.size() - 3;
        for (int i = 0; i < toomany; i++) {
            messages.remove(0);
        }
    }

    public void build2D(SpriteImages spriteImages) {
        align(Align.topLeft);
        for (int i = 0; i < 3; i++) {
            Label label = new Label("", spriteImages.getLabelStyleDefault());
            labelList[i] = label;
            add(label).align(Align.left);
            row();
        }
    }

    class MyMessage {
        float duration = 0.0f;
        String message = "";

        public MyMessage(float duration, String message) {
            this.duration = duration;
            this.message = message;
        }
    }
}
