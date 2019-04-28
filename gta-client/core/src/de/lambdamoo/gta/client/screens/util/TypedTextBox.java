package de.lambdamoo.gta.client.screens.util;

public class TypedTextBox {

    private float cursor = 0;
    private String fulltext = null;
    private float charsPerSecond = 20;
    private boolean finished = false;

    public TypedTextBox(String text) {
        this.fulltext = text;
    }

    public float getCharsPerSecond() {
        return charsPerSecond;
    }

    public void setCharsPerSecond(float charsPerSecond) {
        this.charsPerSecond = charsPerSecond;
    }

    public String getCursorText() {
        if (cursor > fulltext.length()) {
            cursor = fulltext.length();
        }
        return this.fulltext.substring(0, (int) cursor);
    }

    public boolean isFinished() {
        return finished;
    }

    public void act(float deltaTime) {
        if (!finished) {
            if (cursor < fulltext.length()) {
                cursor += deltaTime * charsPerSecond;
            } else {
                cursor = fulltext.length();
                finished = true;
            }
        }
    }

    public void reset() {
        finished = false;
    }

}
