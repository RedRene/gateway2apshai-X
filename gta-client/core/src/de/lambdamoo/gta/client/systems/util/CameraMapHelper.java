package de.lambdamoo.gta.client.systems.util;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector2;

import de.lambdamoo.gta.client.screens.manager.ScreenResolution;
import de.lambdamoo.gta.client.util.MyLogger;
import de.lambdamoo.gta.world.components.Position;

public class CameraMapHelper {
    private static final String TAG = CameraMapHelper.class.getName();
    private final float MAX_ZOOM_IN = 0.25f;
    private final float MAX_ZOOM_OUT = 2.0f;
    private OrthographicCamera cameraMap;
    private Vector2 position;
    private Position target;
    private int borderOffset = 100;
    private int sensity = 10;

    public CameraMapHelper() {
        position = new Vector2();
        cameraMap = new OrthographicCamera(ScreenResolution.getResolution().getVirtualWidth(), ScreenResolution.getResolution().getVirtualHeight());
        cameraMap.zoom = 0.5f;
        cameraMap.setToOrtho(false, ScreenResolution.getResolution().getVirtualWidth(), ScreenResolution.getResolution().getVirtualHeight());
    }

    public void zoomIn() {
        cameraMap.zoom -= 0.2f;
    }

    /**
     * This method moves the map
     *
     * @param velocityX
     * @param velocityY
     * @param mapWidthPixel
     * @param mapHeightPixel
     */
    public void moveMap(float velocityX, float velocityY, int mapWidthPixel, int mapHeightPixel) {
        float posX = cameraMap.position.x;
        float posY = cameraMap.position.y;

        posX -= velocityX / sensity;
        posY -= velocityY / sensity;

        MyLogger.getInstance().log("", cameraMap.viewportWidth + "");

        float originX = cameraMap.viewportWidth / 2;
        float originY = cameraMap.viewportHeight / 2;

        cameraMap.position.set(posX, posY, 0);
    }

    public void zoomOut() {
        cameraMap.zoom += 0.2f;
    }


    public void update(float deltaTime) {
        if (!hasTarget()) return;
        position.x = target.xWorld;
        position.y = target.yWorld;
    }

    public boolean hasTarget() {
        return target != null;
    }

    public Matrix4 getCombined() {
        return cameraMap.combined;
    }

    public void setPosition(float x, float y) {
        this.position.set(x, y);
    }

    public Vector2 getPosition() {
        return position;
    }

    public void addZoom(float amount) {
        setZoom(cameraMap.zoom + amount);
    }

    public float getZoom() {
        return cameraMap.zoom;
    }

    public void setZoom(float zoom) {
        cameraMap.zoom = MathUtils.clamp(zoom, MAX_ZOOM_IN, MAX_ZOOM_OUT);
        MyLogger.getInstance().log("CameraMapHelper", "New zoom=" + cameraMap.zoom);
    }

    public Position getTarget() {
        return target;
    }

    public void setTarget(Position target) {
        this.target = target;
    }

    public boolean hasTarget(Position pos) {
        return hasTarget() && this.target.equals(target);
    }

    public void applyTo() {
        cameraMap.position.x = position.x;
        cameraMap.position.y = position.y;
        cameraMap.update();
    }
}