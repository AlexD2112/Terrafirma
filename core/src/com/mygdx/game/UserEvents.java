package com.mygdx.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import org.hexworks.mixite.core.api.Hexagon;
import org.hexworks.mixite.core.api.contract.SatelliteData;
import org.hexworks.mixite.core.vendor.Maybe;

public class UserEvents {
    private static int width;
    private static int height;
    private static double recedeFactor;
    private static Vector2 screenCenter;
    private static double zoom;
    private static HexMap hexMap;
    private static int hexDensity;
    private static float moveSpeed;
    private static float zoomSpeed;
    private static double minZoom;
    private static double maxZoom;
    private static int rightEdge;
    private static int leftEdge;
    private static int topEdge;
    private static int bottomEdge;


    public static double checkInput(int receivedWidth, int receivedHeight, double receivedRecedeFactor, Vector2 givenScreenCenter, double receivedZoom, HexMap receivedHexMap, int receivedHexDensity, float receivedMoveSpeed, double receivedMinZoom, double receivedMaxZoom, float receivedZoomSpeed, int receivedRightEdge, int receivedLeftEdge, int receivedTopEdge, int receivedBottomEdge) {
        screenCenter = givenScreenCenter;
        width = receivedWidth;
        height = receivedHeight;
        recedeFactor = receivedRecedeFactor;
        zoom = receivedZoom;
        hexMap = receivedHexMap;
        hexDensity = receivedHexDensity;
        moveSpeed = receivedMoveSpeed;
        zoomSpeed = receivedZoomSpeed;
        minZoom = receivedMinZoom;
        maxZoom = receivedMaxZoom;
        rightEdge = receivedRightEdge;
        leftEdge = receivedLeftEdge;
        topEdge = receivedTopEdge;
        bottomEdge = receivedBottomEdge;

        float modifiedMoveSpeed = moveSpeed / (float) Math.sqrt(zoom);

        if (Gdx.input.justTouched()) {
            processClick();
        }
        if (Gdx.input.isKeyPressed(Input.Keys.ESCAPE)) {
            Gdx.app.exit();
        }
        if (Gdx.input.isKeyPressed(Input.Keys.SHIFT_LEFT) || Gdx.input.isKeyPressed(Input.Keys.SHIFT_RIGHT)) {
            modifiedMoveSpeed *= 3;
        }
        modifiedMoveSpeed /= (float) zoom;
        if(Gdx.input.isKeyPressed(Input.Keys.D)) {
            screenCenter.x += modifiedMoveSpeed;
            if (screenCenter.x > rightEdge)	{
                screenCenter.x = rightEdge;
            }
        } else if(Gdx.input.isKeyPressed(Input.Keys.A)) {
            screenCenter.x -= modifiedMoveSpeed;
            if (screenCenter.x < leftEdge) {
                screenCenter.x = leftEdge;
            }
        }
        if(Gdx.input.isKeyPressed(Input.Keys.W)) {
            screenCenter.y += modifiedMoveSpeed;
            if (screenCenter.y > topEdge) {
                screenCenter.y = topEdge;
            }
        } else if(Gdx.input.isKeyPressed(Input.Keys.S)) {
            screenCenter.y -= modifiedMoveSpeed;
            if (screenCenter.y < bottomEdge) {
                screenCenter.y = bottomEdge;
            }
        }
        if(Gdx.input.isKeyPressed(Input.Keys.Q)) {
            zoom += zoomSpeed;
        } else if(Gdx.input.isKeyPressed(Input.Keys.E)) {
            zoom -= zoomSpeed;
            if (zoom < minZoom) {
                zoom = minZoom;
            }
        }

        return zoom;
    }

    public static void processClick() {
        int x = Gdx.input.getX();
        int y = (Gdx.input.getY() - height) * -1;
        Vector2 point = new Vector2(x, y);
        Hexagon hex = DisplayFunctions.getHexFromPoint(point, hexMap, width, height, recedeFactor, screenCenter, zoom, hexDensity);
        CustomSatelliteData hexData = (CustomSatelliteData) hex.getSatelliteData().get();
        hexData.setColor(new Color(MathUtils.random(), MathUtils.random(), MathUtils.random(), 1));
    }
}
