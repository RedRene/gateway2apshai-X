package de.lambdamoo.gta.client.dialog;

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Dialog;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;

public class DialogBox extends Dialog {

    private MyDialogListener dialogListener = null;
    private ClickListener clickListener = new ClickListener() {
        @Override
        public void clicked(InputEvent event, float x, float y) {
            onClick(event, x, y);
        }
    };
    private TextButton btnOk = null;
    private TextButton btnYes = null;
    private TextButton btnNo = null;
    private Label label = null;

    public DialogBox(String title, WindowStyle windowStyle, TextButton.TextButtonStyle textButtonStyle, Label.LabelStyle labelStyle) {
        super(title, windowStyle);
        pad(48);
        getButtonTable().padTop(32).align(Align.center);
        getButtonTable().defaults().height(80).padRight(20);
        setModal(true);
        setMovable(false);
        setResizable(false);

        if (labelStyle != null) {
            label = new Label("", labelStyle);
            getContentTable().add(label).align(Align.topLeft);
        }
        btnOk = new TextButton("Close", textButtonStyle);
        btnOk.setUserObject(MyDialogListener.Result.Ok);
        btnOk.addListener(clickListener);
        btnYes = new TextButton("Yes", textButtonStyle);
        btnYes.setUserObject(MyDialogListener.Result.Yes);
        btnYes.addListener(clickListener);
        btnNo = new TextButton("No", textButtonStyle);
        btnNo.setUserObject(MyDialogListener.Result.No);
        btnNo.addListener(clickListener);
    }

    public void onClick(InputEvent event, float x, float y) {
        boolean closeOk = true;
        if (dialogListener != null) {
            MyDialogListener.Result result = (MyDialogListener.Result) event.getListenerActor().getUserObject();
            closeOk = dialogListener.onPerform(result);
        }
        if (closeOk) {
            hide();
        }
    }

    public void setDialogListener(MyDialogListener listener) {
        this.dialogListener = listener;
    }

    public void setText(String text) {
        this.label.setText(text);
    }

    public void showButtonOk() {
        getButtonTable().clear();
        getButtonTable().add(btnOk);
    }

    public void showButtonOk(String btnText) {
        getButtonTable().clear();
        getButtonTable().add(btnOk);
        btnOk.setText(btnText);
    }

    public void showButtonYesNo() {
        getButtonTable().clear();
        getButtonTable().add(btnYes);
        getButtonTable().add(btnNo);
    }

}
