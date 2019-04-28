package de.lambdamoo.gta.client.screens.hud;

import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ProgressBar;
import com.badlogic.gdx.scenes.scene2d.ui.Stack;
import com.badlogic.gdx.utils.Align;

import de.lambdamoo.gta.client.util.SpriteImages;

public class HealthBar extends Stack {

    private ProgressBar progressBarHealth = null;
    private Label labelHealth = null;

    /**
     * @param width  of the health bar
     * @param height of the health bar
     */
    public HealthBar(int width, int height, SpriteImages spriteImages) {
        super();
        labelHealth = new Label("", spriteImages.getLabelStyleDefault());
        labelHealth.setAlignment(Align.center);
        labelHealth.setTouchable(Touchable.disabled);

        progressBarHealth = new ProgressBar(0f, 1f, 0.01f, false, new ProgressBar.ProgressBarStyle());
        progressBarHealth.getStyle().background = spriteImages.getDrawableFromAtlasGameUI("red");//Utils.getColoredDrawable(width, height, Color.RED);
        progressBarHealth.getStyle().knob = spriteImages.getDrawableFromAtlasGameUI("zero_green");//Utils.getColoredDrawable(0, height, Color.GREEN);
        progressBarHealth.getStyle().knobBefore = spriteImages.getDrawableFromAtlasGameUI("green");//Utils.getColoredDrawable(width, height, Color.GREEN);
        progressBarHealth.setWidth(width);
        progressBarHealth.setHeight(height);
        progressBarHealth.setValue(0f);
        progressBarHealth.setAnimateDuration(0.25f);

        add(progressBarHealth);
        add(labelHealth);
    }

    public void renderPlayerStatus(int current, int total) {
        labelHealth.setText(current + "/" + total);
        float healthPercent = (float) current / (float) total;
        progressBarHealth.setValue(healthPercent);
    }
}

