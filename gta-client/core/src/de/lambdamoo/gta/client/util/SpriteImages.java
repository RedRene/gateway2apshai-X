package de.lambdamoo.gta.client.util;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.FileHandleResolver;
import com.badlogic.gdx.assets.loaders.resolvers.InternalFileHandleResolver;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGeneratorLoader;
import com.badlogic.gdx.graphics.g2d.freetype.FreetypeFontLoader;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Slider;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.ui.Touchpad;
import com.badlogic.gdx.scenes.scene2d.ui.Window;
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Array;

import java.util.HashMap;

import de.lambdamoo.gta.common.loader.GameDefinitions;
import de.lambdamoo.gta.common.loader.MyMapLoader;


public class SpriteImages {
    public final static int TILESET_ITEM = 1;
    public final static int TILESET_MONSTER = 3;
    private final static String IMAGE_ROOT = "images/";
    private TextureRegion[][] tileSetLevelObjects = new TextureRegion[8][3];
    private Pixmap[][] pixmapTileSetLevelObjects = new Pixmap[8][3];
    private Array<TextureAtlas.AtlasRegion> tilesetItems = null;

    private TextureRegion arrowEast = null;
    private TextureRegion arrowWest = null;
    private TextureRegion arrowNorth = null;
    private TextureRegion arrowSouth = null;

    private TextureRegion[] tileSetNonLevelObjects = new TextureRegion[4];
    private Pixmap[] pixmapTileSetNonLevelObjects = new Pixmap[4];
    private HashMap<Integer, Array<TextureAtlas.AtlasRegion>> tilesetMonsters = new HashMap<Integer, Array<TextureAtlas.AtlasRegion>>(32);
    private Array<TextureAtlas.AtlasRegion> playerWest = null;
    private Array<TextureAtlas.AtlasRegion> playerEast = null;
    private Array<TextureAtlas.AtlasRegion> playerNorth = null;
    private Array<TextureAtlas.AtlasRegion> playerSouth = null;
    private Array<TextureAtlas.AtlasRegion> playerAttackWest = null;
    private Array<TextureAtlas.AtlasRegion> playerAttackEast = null;
    private Array<TextureAtlas.AtlasRegion> playerAttackNorth = null;
    private Array<TextureAtlas.AtlasRegion> playerAttackSouth = null;
    private Array<TextureAtlas.AtlasRegion> playerBowWest = null;
    private Array<TextureAtlas.AtlasRegion> playerBowEast = null;
    private Array<TextureAtlas.AtlasRegion> playerBowNorth = null;
    private Array<TextureAtlas.AtlasRegion> playerBowSouth = null;
    private TextureRegionDrawable btnCtrlFire = null;
    private TextureRegionDrawable btnDelete = null;
    private TextureRegionDrawable btnEquip = null;
    private TextField.TextFieldStyle textFieldStyle = null;
    private Label.LabelStyle labelStyle32 = null;
    private Label.LabelStyle labelStyle48 = null;
    private Label.LabelStyle labelStyle64 = null;
    private Label.LabelStyle labelStyleHeadline = null;
    private Label.LabelStyle labelStyleHeadlineSmall = null;
    private Slider.SliderStyle sliderStyle = null;
    private TextureAtlas atlasGameUI = null;
    private TextureAtlas atlasDialog = null;
    private TextButton.TextButtonStyle textButtonStyle = null;
    private AssetManager manager = null;
    private Window.WindowStyle windowStyle = new Window.WindowStyle();
    private Touchpad.TouchpadStyle touchpadStyle = null;
    private ImageButton.ImageButtonStyle imageButtonStyle = null;
    private ImageButton.ImageButtonStyle imageButtonStyleChecked = null;
    private NinePatchDrawable ninePatchOpaqueBackground = null;
    private NinePatchDrawable ninePatchOpaqueBackgroundStrong = null;
    private NinePatchDrawable ninePatchTranslucent65Background = null;
    private GameDefinitions gameDefinitions = null;
    private FontSize fontSize = FontSize.Medium;
    private Label.LabelStyle labelStyleDefault = null;

    public SpriteImages() {
        manager = new AssetManager();
        manager.setLoader(GameDefinitions.class, new MyMapLoader(new InternalFileHandleResolver()));
        // queue the asset loading
        manager.load("images/game-ui.atlas", TextureAtlas.class);
        manager.load("images/dialogs.atlas", TextureAtlas.class);

        FileHandleResolver resolver = new InternalFileHandleResolver();
        manager.setLoader(FreeTypeFontGenerator.class, new FreeTypeFontGeneratorLoader(resolver));
        manager.setLoader(BitmapFont.class, ".ttf", new FreetypeFontLoader(resolver));

        loadFont(32);
        loadFont(48);
        loadFont(64);

        FreetypeFontLoader.FreeTypeFontLoaderParameter headlineFont = new FreetypeFontLoader.FreeTypeFontLoaderParameter();
        headlineFont.fontFileName = "font/Roboto-Medium.ttf";
        headlineFont.fontParameters.size = 48;
        manager.load("fontMedium48.ttf", BitmapFont.class, headlineFont);

        FreetypeFontLoader.FreeTypeFontLoaderParameter headlineSmallFont = new FreetypeFontLoader.FreeTypeFontLoaderParameter();
        headlineSmallFont.fontFileName = "font/Roboto-Medium.ttf";
        headlineSmallFont.fontParameters.size = 32;
        manager.load("fontMedium32.ttf", BitmapFont.class, headlineSmallFont);

        manager.load(IMAGE_ROOT + "background/title.jpg", Texture.class);
        manager.load(IMAGE_ROOT + "background/title_text.png", Texture.class);
        manager.load(IMAGE_ROOT + "background/background.jpg", Texture.class);
        manager.load(IMAGE_ROOT + "background/dungeon1.jpg", Texture.class);

        // custom loader of Game Definitions
        manager.load("gameDef", GameDefinitions.class);

    }

    private void loadFont(int size) {
        FreetypeFontLoader.FreeTypeFontLoaderParameter font = new FreetypeFontLoader.FreeTypeFontLoaderParameter();
        font.fontFileName = "font/Roboto-Regular.ttf";
        font.fontParameters.size = size;
        manager.load("font" + size + ".ttf", BitmapFont.class, font);
    }

    public NinePatchDrawable getNinePatchOpaqueBackground() {
        return ninePatchOpaqueBackground;
    }

    public NinePatchDrawable getNinePatchOpaqueBackgroundStrong() {
        return ninePatchOpaqueBackgroundStrong;
    }

    public NinePatchDrawable getNinePatchTranslucent65Background() {
        return ninePatchTranslucent65Background;
    }

    public Label.LabelStyle getLabelStyleHeadlineSmall() {
        return labelStyleHeadlineSmall;
    }

    public Label.LabelStyle getLabelStyleHeadline() {
        return labelStyleHeadline;
    }

    public Touchpad.TouchpadStyle getTouchpadStyle() {
        return touchpadStyle;
    }

    public TextureRegionDrawable getBtnDelete() {
        return btnDelete;
    }

    public TextureRegionDrawable getBtnEquip() {
        return btnEquip;
    }

    public Window.WindowStyle getWindowStyle() {
        return windowStyle;
    }

    public boolean isInitialized() {
        return manager.update();
    }

    public ImageButton.ImageButtonStyle getImageButtonStyle() {
        return imageButtonStyle;
    }

    public ImageButton.ImageButtonStyle getImageButtonStyleChecked() {
        return imageButtonStyleChecked;
    }

    public GameDefinitions getGameDefinitions() {
        return gameDefinitions;
    }

    /**
     * This method build the structures after the Assetmanager has finished the loading
     */
    public void init() {
        atlasGameUI = manager.get("images/game-ui.atlas", TextureAtlas.class);
        atlasDialog = manager.get("images/dialogs.atlas", TextureAtlas.class);

        labelStyle32 = new Label.LabelStyle(manager.get("font32.ttf", BitmapFont.class), Color.WHITE);
        labelStyle48 = new Label.LabelStyle(manager.get("font48.ttf", BitmapFont.class), Color.WHITE);
        labelStyle64 = new Label.LabelStyle(manager.get("font64.ttf", BitmapFont.class), Color.WHITE);

        labelStyleHeadline = new Label.LabelStyle(manager.get("fontMedium48.ttf", BitmapFont.class), Color.WHITE);
        labelStyleHeadlineSmall = new Label.LabelStyle(manager.get("fontMedium32.ttf", BitmapFont.class), Color.WHITE);

        gameDefinitions = manager.get("gameDef", GameDefinitions.class);

        // styles
        textButtonStyle = new TextButton.TextButtonStyle();
        textButtonStyle.up = getDrawableFromAtlasDialogs("button_H22@2x");
        textButtonStyle.down = getDrawableFromAtlasDialogs("button_H22-pressed@2x");
        textButtonStyle.disabled = getDrawableFromAtlasDialogs("button_H22-disabled@2x");
        textButtonStyle.checked = getDrawableFromAtlasDialogs("button_H22-active-pressed@2x");

        textFieldStyle = new TextField.TextFieldStyle();
        textFieldStyle.background = getDrawableFromAtlasDialogs("textfield2");
        textFieldStyle.fontColor = Color.WHITE;
        textFieldStyle.cursor = getDrawableFromAtlasDialogs("cursor");
        textFieldStyle.selection = getDrawableFromAtlasDialogs("cursor");

        ninePatchOpaqueBackground = get9PatchDrawableFromAtlasDialogs("lite_opaque_background", 32);
        ninePatchTranslucent65Background = get9PatchDrawableFromAtlasDialogs("translucent65_background", 32);
        ninePatchOpaqueBackgroundStrong = get9PatchDrawableFromAtlasDialogs("strong_opaque-background", 64);

        windowStyle.titleFontColor = Color.WHITE;

        setFontSize(FontSize.Medium);


        sliderStyle = new Slider.SliderStyle();
        sliderStyle.knob = getDrawableFromAtlasDialogs("slider@2x");
        sliderStyle.knobOver = getDrawableFromAtlasDialogs("slider-active@2x");
        sliderStyle.knobDown = getDrawableFromAtlasDialogs("slider-pressed@2x");
        sliderStyle.background = getDrawableFromAtlasDialogs("slider");

        touchpadStyle = new Touchpad.TouchpadStyle();
        touchpadStyle.background = getDrawableFromAtlasGameUI("touchpad_background");
        touchpadStyle.knob = getDrawableFromAtlasGameUI("touchpad_knob");

        imageButtonStyle = new ImageButton.ImageButtonStyle();
        imageButtonStyle.imageUp = getDrawableFromAtlasGameUI("button_selectable_100_base-active");
        imageButtonStyle.imageDown = getDrawableFromAtlasGameUI("button_selectable_100_base-touched");
        imageButtonStyle.imageDisabled = imageButtonStyle.imageDown;

        imageButtonStyleChecked = new ImageButton.ImageButtonStyle();
        imageButtonStyleChecked.imageUp = imageButtonStyle.imageUp;
        imageButtonStyleChecked.imageDown = imageButtonStyle.imageDown;
        imageButtonStyleChecked.imageChecked = getDrawableFromAtlasGameUI("button_selectable_100_base-checked");
        imageButtonStyleChecked.imageDisabled = imageButtonStyle.imageDown;

        // build the object structure for fast access
        for (int level = 1; level <= 8; level++) {
            tileSetLevelObjects[level - 1][0] = getSpriteFromAtlasGameUI("level_item" + level + "wall");
            tileSetLevelObjects[level - 1][1] = getSpriteFromAtlasGameUI("level_item" + level + "secret");
            tileSetLevelObjects[level - 1][2] = getSpriteFromAtlasGameUI("level_item" + level + "door");
        }
        tileSetNonLevelObjects[0] = getSpriteFromAtlasGameUI("floor_known");
        tileSetNonLevelObjects[1] = getSpriteFromAtlasGameUI("floor_unknown");
        tileSetNonLevelObjects[2] = getSpriteFromAtlasGameUI("floor_question");
        tileSetNonLevelObjects[3] = getSpriteFromAtlasGameUI("floor_gray");

        tilesetMonsters.put(0, getSpritesFromAtlasGameUI("mobs1")); // rat
        tilesetMonsters.put(1, getSpritesFromAtlasGameUI("mobs2")); // snake
        tilesetMonsters.put(2, getSpritesFromAtlasGameUI("mobs3")); // slime
        tilesetMonsters.put(3, getSpritesFromAtlasGameUI("mobs4")); // bat
        tilesetMonsters.put(4, getSpritesFromAtlasGameUI("mobs5")); // rat
        tilesetMonsters.put(5, getSpritesFromAtlasGameUI("mobs6")); // bat
        tilesetMonsters.put(6, getSpritesFromAtlasGameUI("mobs7")); // slime
        tilesetMonsters.put(7, getSpritesFromAtlasGameUI("mobs8")); // spider
        tilesetMonsters.put(8, getSpritesFromAtlasGameUI("mobs9")); // green ghoul
        tilesetMonsters.put(9, getSpritesFromAtlasGameUI("mobs10")); // grey ogre
        tilesetMonsters.put(10, getSpritesFromAtlasGameUI("mobs11")); // kobold
        tilesetMonsters.put(11, getSpritesFromAtlasGameUI("mobs12")); // giant
        tilesetMonsters.put(12, getSpritesFromAtlasGameUI("mobs13")); // asp
        tilesetMonsters.put(13, getSpritesFromAtlasGameUI("mobs14")); // pink spider
        tilesetMonsters.put(14, getSpritesFromAtlasGameUI("mobs15")); // men
        tilesetMonsters.put(15, getSpritesFromAtlasGameUI("mobs16")); // men
        tilesetMonsters.put(16, getSpritesFromAtlasGameUI("mobs17")); // rat
        tilesetMonsters.put(17, getSpritesFromAtlasGameUI("mobs18")); // mamba
        tilesetMonsters.put(18, getSpritesFromAtlasGameUI("mobs19")); // spider
        tilesetMonsters.put(19, getSpritesFromAtlasGameUI("mobs20")); // slime
        tilesetMonsters.put(20, getSpritesFromAtlasGameUI("mobs21")); // ghost
        tilesetMonsters.put(21, getSpritesFromAtlasGameUI("mobs22")); // bat
        tilesetMonsters.put(22, getSpritesFromAtlasGameUI("mobs23")); // men
        tilesetMonsters.put(23, getSpritesFromAtlasGameUI("mobs24")); // ogre
        tilesetMonsters.put(24, getSpritesFromAtlasGameUI("mobs25")); // men
        tilesetMonsters.put(25, getSpritesFromAtlasGameUI("mobs26")); // men
        tilesetMonsters.put(26, getSpritesFromAtlasGameUI("mobs27")); // men
        tilesetMonsters.put(27, getSpritesFromAtlasGameUI("mobs28")); // men
        tilesetMonsters.put(28, getSpritesFromAtlasGameUI("mobs29")); // dragon
        tilesetMonsters.put(29, getSpritesFromAtlasGameUI("mobs30")); // men
        tilesetMonsters.put(30, getSpritesFromAtlasGameUI("mobs31")); // men
        tilesetMonsters.put(31, getSpritesFromAtlasGameUI("mobs32")); // blink bat

        tilesetItems = getSpritesFromAtlasGameUI("item");

        playerSouth = getSpritesFromAtlasGameUI("hero_run1");
        playerEast = getSpritesFromAtlasGameUI("hero_run2");
        playerNorth = getSpritesFromAtlasGameUI("hero_run3");
        playerWest = getSpritesFromAtlasGameUI("hero_run4");
        playerAttackSouth = getSpritesFromAtlasGameUI("hero_fight1");
        playerAttackEast = getSpritesFromAtlasGameUI("hero_fight2");
        playerAttackWest = getSpritesFromAtlasGameUI("hero_fight3");
        playerAttackNorth = getSpritesFromAtlasGameUI("hero_fight4");
        playerBowSouth = getSpritesFromAtlasGameUI("hero_bow_south");
        playerBowEast = getSpritesFromAtlasGameUI("hero_bow_east");
        playerBowWest = getSpritesFromAtlasGameUI("hero_bow_west");
        playerBowNorth = getSpritesFromAtlasGameUI("hero_bow_north");

        arrowEast = getSpriteFromAtlasGameUI("pfeil16 east");
        arrowWest = getSpriteFromAtlasGameUI("pfeil16 west");
        arrowSouth = getSpriteFromAtlasGameUI("pfeil16 south");
        arrowNorth = getSpriteFromAtlasGameUI("pfeil16 north");

        btnCtrlFire = new TextureRegionDrawable(atlasGameUI.findRegion("shadedDarkFire"));

        btnDelete = new TextureRegionDrawable(atlasGameUI.findRegion("icons8-delete-64"));
        btnEquip = new TextureRegionDrawable(atlasGameUI.findRegion("icons8-clothes-48"));

        // prepare and create pixmaps
        Texture texture = tileSetNonLevelObjects[0].getTexture();
        if (!texture.getTextureData().isPrepared()) {
            texture.getTextureData().prepare();
        }
        Pixmap pixmapAtlas = texture.getTextureData().consumePixmap();
        for (int i = 0; i < 4; i++) {
            int srcX = tileSetNonLevelObjects[i].getRegionX();
            int srcY = tileSetNonLevelObjects[i].getRegionY();
            pixmapTileSetNonLevelObjects[i] = copyRegionToPixmap(pixmapAtlas, srcX, srcY, 8, 8);
        }
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 3; j++) {
                int srcX = tileSetLevelObjects[i][j].getRegionX();
                int srcY = tileSetLevelObjects[i][j].getRegionY();
                pixmapTileSetLevelObjects[i][j] = copyRegionToPixmap(pixmapAtlas, srcX, srcY, 8, 8);
            }
        }
    }

    private Array<TextureAtlas.AtlasRegion> getSpritesFromAtlasGameUI(String name) {
        Array<TextureAtlas.AtlasRegion> result = atlasGameUI.findRegions(name);
        return result;
    }

    public TextureRegion getSpriteFromAtlasGameUI(String name) {
        TextureRegion region = atlasGameUI.findRegion(name);
        return region;
    }

    private Pixmap copyRegionToPixmap(Pixmap source, int srcX, int srcY, int width, int height) {
        Pixmap result = new Pixmap(width, height, Pixmap.Format.RGB888);
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                int colorInt = source.getPixel(srcX + x, srcY + y);
                // you could now draw that color at (x, y) of another pixmap of the size (regionWidth, regionHeight)
                result.drawPixel(x, y, colorInt);
            }
        }
        return result;
    }

    public TextureRegionDrawable getDrawableFromAtlasGameUI(String name) {
        TextureRegion region = atlasGameUI.findRegion(name);
        if (region == null) {
            throw new RuntimeException("Drawable " + name + " was not found!");
        }
        return new TextureRegionDrawable(region);
    }

    public TextureRegionDrawable getDrawableFromAtlasDialogs(String name) {
        TextureRegion region = atlasDialog.findRegion(name);
        if (region == null) {
            throw new RuntimeException("Drawable " + name + " was not found!");
        }
        return new TextureRegionDrawable(region);
    }

    /**
     * This method loads a nine patch from the atlas with the offset at the edges
     *
     * @param name
     * @param offset
     * @return
     */
    private NinePatchDrawable get9PatchDrawableFromAtlasDialogs(String name, int offset) {
        //final Texture t = new Texture(Gdx.files.internal("images/" + name + ".9.png"));
        //return new NinePatchDrawable(new NinePatch(new TextureRegion(t, 1, 1, t.getWidth() - 2, t.getHeight() - 2), 10, 10, 10, 10));
        TextureRegion region = atlasDialog.findRegion(name);
        MyLogger.getInstance().log("SpriteImages", name);
        NinePatchDrawable draw = new NinePatchDrawable(new NinePatch(region, offset, offset, offset, offset));
        return draw;
    }

    public FontSize getFontSize() {
        return fontSize;
    }

    /**
     * Value
     *
     * @param size
     */
    public void setFontSize(FontSize size) {
        this.fontSize = size;
        switch (size) {
            case Small:
                labelStyleDefault = labelStyle32;
                break;
            case Medium:
                labelStyleDefault = labelStyle48;
                break;
            case Large:
                labelStyleDefault = labelStyle64;
                break;
        }
        textButtonStyle.font = labelStyleDefault.font;
        textFieldStyle.font = labelStyleDefault.font;
        windowStyle.titleFont = labelStyleDefault.font;
    }

    public TextureRegion getArrowWest() {
        return arrowWest;
    }

    public TextureRegion getArrowNorth() {
        return arrowNorth;
    }

    public TextureRegion getArrowSouth() {
        return arrowSouth;
    }

    public TextButton.TextButtonStyle getTextButtonStyle() {
        return textButtonStyle;
    }

    public TextField.TextFieldStyle getTextFieldStyle() {
        return textFieldStyle;
    }

    public Slider.SliderStyle getSliderStyle() {
        return sliderStyle;
    }

    public TextureRegionDrawable getButtonControlFire() {
        return btnCtrlFire;
    }

    /**
     * This method returns the default size of the label style defined in the options dialog.
     *
     * @return
     */
    public Label.LabelStyle getLabelStyleDefault() {
        return labelStyleDefault;
    }

    public Label.LabelStyle getLabelStyleSmall() {
        return labelStyle32;
    }

    public TextureRegion getLevelObject(int level, int i) {
        return (tileSetLevelObjects[level - 1][i]);
    }

    public Pixmap getPixmapLevelObject(int level, int i) {
        return (pixmapTileSetLevelObjects[level - 1][i]);
    }

    public TextureRegion getItemSprite(int index) {
        return tilesetItems.get(index);
    }

    public Array<TextureAtlas.AtlasRegion> getMonsterSprites(int index) {
        return tilesetMonsters.get(index);
    }

    public TextureRegion getTextureNonLevel(int index) {
        TextureRegion result = tileSetNonLevelObjects[index];
        return result;
    }

    public TextureRegion getArrowEast() {
        return arrowEast;
    }

    public Pixmap getPixmapTextureNonLevel(int index) {
        return pixmapTileSetNonLevelObjects[index];
    }

    public Array<TextureAtlas.AtlasRegion> getPlayerWest() {
        return playerWest;
    }

    public Array<TextureAtlas.AtlasRegion> getPlayerEast() {
        return playerEast;
    }

    public Array<TextureAtlas.AtlasRegion> getPlayerNorth() {
        return playerNorth;
    }

    public Array<TextureAtlas.AtlasRegion> getPlayerSouth() {
        return playerSouth;
    }

    public Array<TextureAtlas.AtlasRegion> getPlayerAttackWest() {
        return playerAttackWest;
    }

    public Array<TextureAtlas.AtlasRegion> getPlayerAttackEast() {
        return playerAttackEast;
    }

    public Array<TextureAtlas.AtlasRegion> getPlayerAttackNorth() {
        return playerAttackNorth;
    }

    public Array<TextureAtlas.AtlasRegion> getPlayerBowSouth() {
        return playerBowSouth;
    }

    public Array<TextureAtlas.AtlasRegion> getPlayerBowWest() {
        return playerBowWest;
    }

    public Array<TextureAtlas.AtlasRegion> getPlayerBowEast() {
        return playerBowEast;
    }

    public Array<TextureAtlas.AtlasRegion> getPlayerBowNorth() {
        return playerBowNorth;
    }

    public Array<TextureAtlas.AtlasRegion> getPlayerAttackSouth() {
        return playerAttackSouth;
    }

    public Texture getBackground(MapBackground bk) {
        String name = null;
        switch (bk) {
            case Logo:
                name = "logo.png";
                break;
            case Background:
                name = "background.jpg";
                break;
            case Title:
                name = "title.jpg";
                break;
            case TitleText:
                name = "title_text.png";
                break;
            case Dungeon:
                name = "dungeon1.jpg";
                break;
        }
        return manager.get(IMAGE_ROOT + "background/" + name, Texture.class);

    }

    public enum FontSize {
        Small, Medium, Large;
    }

    public enum MapBackground {
        Title, TitleText, Background, Logo, Dungeon;
    }
}
