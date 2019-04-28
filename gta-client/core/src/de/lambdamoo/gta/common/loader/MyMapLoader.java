package de.lambdamoo.gta.common.loader;

import com.badlogic.gdx.assets.AssetDescriptor;
import com.badlogic.gdx.assets.AssetLoaderParameters;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.AsynchronousAssetLoader;
import com.badlogic.gdx.assets.loaders.FileHandleResolver;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Array;

import de.lambdamoo.gta.client.util.MyLogger;


public class MyMapLoader extends AsynchronousAssetLoader {
    private MapLoader mapLoader = new MapLoader();
    private GameDefinitions gameDefinitions = null;

    public MyMapLoader(FileHandleResolver resolver) {
        super(resolver);
    }

    @Override
    public void loadAsync(AssetManager manager, String fileName, FileHandle file, AssetLoaderParameters parameter) {
        MyLogger.getInstance().log("MyMapLoader", "loadAsync");
        gameDefinitions = mapLoader.loadDefinitions();
    }

    @Override
    public Object loadSync(AssetManager manager, String fileName, FileHandle file, AssetLoaderParameters parameter) {
        MyLogger.getInstance().log("MyMapLoader", "loadSync");
        GameDefinitions def = this.gameDefinitions;
        this.gameDefinitions = null;
        return def;
    }

    @Override
    public Array<AssetDescriptor> getDependencies(String fileName, FileHandle file, AssetLoaderParameters parameter) {
        return null;
    }
}
