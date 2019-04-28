package de.lambdamoo.gta.client.screens.util;

import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.Align;

public class FormatUtil {
    public static int PAD_OUTER_BORDER = 50;
    public static int PAD_INNER_ROW = 10;
    public static int PAD_INNER_COL = 25;

    public static Table addButtonTable(Table table, int colspan, Button... buttons) {
        Table grp = FormatUtil.createButtonTable(buttons);
        table.add(grp).padTop(50).colspan(colspan).expandX().fillX().align(Align.center);
        return grp;
    }

    public static Table createButtonTable(Button... buttons) {
        Table btnGroup = new Table();
        for (Button btn : buttons) {
            btnGroup.add(btn).padRight(20).height(80);
        }
        btnGroup.align(Align.center);
        return btnGroup;
    }

    public static Table addButtonTable(Table table, int colspan, int padTop, Button... buttons) {
        Table grp = FormatUtil.createButtonTable(buttons);
        table.add(grp).padTop(padTop).colspan(colspan).expandX().fillX().align(Align.center);
        return grp;
    }

}
