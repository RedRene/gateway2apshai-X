package de.lambdamoo.gta.client.screens.hud;

import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Stack;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;

public class StackButton extends Stack {
    private ImageButton button = null;
    private Image image = null;
    private Label label = null;

    public StackButton(ImageButton button, Drawable overlay) {
        this.button = button;
        this.image = new Image(overlay);
        this.add(button);
        this.add(image);
        image.setTouchable(Touchable.disabled);
    }

    public void setDisabled(boolean disabled) {
        this.button.setDisabled(disabled);
    }

    public void addLabel(Label label) {
        this.label = label;
        this.addActorAfter(image, label);
        this.label.setTouchable(Touchable.disabled);
    }

    public void setImage(Image newImage) {
        if (image != null) {
            this.removeActor(image);
        }
        image = newImage;
        this.addActorAfter(button, image);
        image.setTouchable(Touchable.disabled);
    }

    public void setOffsetImage(int x, int y) {
        this.image.setPosition(x, y);
    }

}
