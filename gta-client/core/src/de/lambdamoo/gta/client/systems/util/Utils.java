package de.lambdamoo.gta.client.systems.util;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;

public class Utils {
    /**
     * Creates an image of determined size filled with determined color.
     *
     * @param width  of an image.
     * @param height of an image.
     * @param color  of an image fill.
     * @return {@link Drawable} of determined size filled with determined color.
     * @deprecated use the textureregion from the Atlas to avoid additional texture bindings
     */
    public static Drawable getColoredDrawable(int width, int height, Color color) {
        Pixmap pixmap = new Pixmap(width, height, Pixmap.Format.RGBA8888);
        pixmap.setColor(color);
        pixmap.fill();

        TextureRegionDrawable drawable = new TextureRegionDrawable(new TextureRegion(new Texture(pixmap)));

        pixmap.dispose();

        return drawable;
    }

    public static boolean arrayContains(Object[] arr, Object targetValue) {
        for (Object s : arr) {
            if (s.equals(targetValue))
                return true;
        }
        return false;
    }

}
