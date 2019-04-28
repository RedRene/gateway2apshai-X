package de.lambdamoo.gta.client.screens.hud;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;

class ActionBarImageButton extends ImageButton {
    private final ActionBarCooldownTimer cooldownTimer;
    private float maxCooldownTime;
    private float currentCooldown = 0.0f;
    private boolean cooldownStarted = false;

    public ActionBarImageButton(ImageButtonStyle imageButtonStyle, float maxCooldownTime) {
        super(imageButtonStyle);
        this.maxCooldownTime = maxCooldownTime;
        this.cooldownTimer = new ActionBarCooldownTimer(true, 92, 92);
        this.cooldownTimer.setPosition(0, 0);
        this.cooldownTimer.setColor(Color.WHITE);
        this.cooldownTimer.setVisible(false);
        addActor(this.cooldownTimer);
    }

    public boolean isOnCooldown() {
        return cooldownStarted;
    }

    public void update(float deltaTime) {
        if (cooldownStarted) {
            currentCooldown += deltaTime;
            if (currentCooldown > maxCooldownTime) {
                // cooldown is finished
                stopCooldown();
            } else {
                // cooldown is still runing
                cooldownTimer.update(getRemainingCooldownPercentage());
            }
        }
    }

    public float getRemainingCooldownPercentage() {
        return currentCooldown / maxCooldownTime;

    }

    public void stopCooldown() {
        cooldownTimer.setVisible(false);
        currentCooldown = 0.0f;
        cooldownStarted = false;
        setDisabled(false);
    }

    public void startCooldown() {
        cooldownTimer.setVisible(true);
        currentCooldown = 0.0f;
        cooldownStarted = true;
        setDisabled(true);
    }
}

