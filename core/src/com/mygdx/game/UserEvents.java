package com.mygdx.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.Ray;
import org.hexworks.mixite.core.api.Hexagon;

import java.util.ArrayList;
import java.util.Map;

import com.mygdx.game.HexMap;

public class UserEvents implements InputProcessor {
    private static float maxZoom;
    private static float minZoom;
    private static float zoomSpeed;
    private static float moveSpeed;
    public static float zoom;
    public static float modifiedMoveSpeed;
    private static int[] edges;
    public static Map<Integer, Boolean> keysHeld = new java.util.HashMap<>();

    public UserEvents(float zoomSpeed, float moveSpeed, float maxZoom, float minZoom, int[] edges) {
        UserEvents.zoomSpeed = zoomSpeed;
        UserEvents.moveSpeed = moveSpeed;
        UserEvents.maxZoom = maxZoom;
        UserEvents.minZoom = minZoom;
        UserEvents.edges = edges;
        zoom = 1;
    }

    public boolean touchDown(int clickX, int clickY, int pointer, int button) {
        processClick(clickX, clickY, button);
        return true;
    }

    public boolean keyDown(int keycode) {
        switch (keycode) {
            case Input.Keys.ESCAPE:
                Gdx.app.exit();
                break;
            case Input.Keys.SHIFT_LEFT:
            case Input.Keys.SHIFT_RIGHT:
                moveSpeed *= 3;
                break;
            default:
                keysHeld.put(keycode, true);
        }
        return true;
    }

    public boolean keyUp(int keycode) {
        switch (keycode) {
            case Input.Keys.SHIFT_LEFT:
            case Input.Keys.SHIFT_RIGHT:
                moveSpeed = moveSpeed / 3;
                break;
            default:
                keysHeld.put(keycode, false);
        }
        return true;
    }

    public boolean keyTyped(char character) {
        return false;
    }

    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        System.out.println("touchUp");
        return false;
    }

    public boolean touchDragged(int screenX, int screenY, int pointer) {
        return false;
    }

    public boolean mouseMoved(int screenX, int screenY) {
        return false;
    }

    public boolean touchCancelled(int screenX, int screenY, int pointer, int button) {
        return false;
    }

    public boolean scrolled(float amountX, float amountY) {
        return false;
    }

    public static void processClick(int x, int y, int mouseNum) {
        Ray ray = HexMap.cam.getPickRay(x, y);
        //Get vector2 of final value of ray where z = 0
        Vector3 point = new Vector3();
        float zOrigin = ray.origin.z;
        float zDirection = ray.direction.z;
        float t = -zOrigin / zDirection;
        point.set(ray.origin.x + t * ray.direction.x, ray.origin.y + t * ray.direction.y, 0);
        System.out.println("clickPoint" + point);
        Hexagon hex = DisplayFunctions.getHexFromPoint(new Vector2(point.x, point.y));
        CustomSatelliteData hexData = (CustomSatelliteData) hex.getSatelliteData().get();
        hexData.setColor(new Color(MathUtils.random(), MathUtils.random(), MathUtils.random(), 1));
    }
    public static void handleInputs() {
        modifiedMoveSpeed = moveSpeed / (float) Math.sqrt(zoom);
        if (Boolean.TRUE.equals(keysHeld.get(Input.Keys.W))) {
            HexMap.camPosition.y += modifiedMoveSpeed;
            if (HexMap.camPosition.y + HexMap.getYOffset() > edges[2]) {
                HexMap.camPosition.y = edges[2] - HexMap.getYOffset();
            }
        }
        if (Boolean.TRUE.equals(keysHeld.get(Input.Keys.S))) {
            HexMap.camPosition.y -= modifiedMoveSpeed;

            if (HexMap.camPosition.y + HexMap.getYOffset() < edges[3]) {
                HexMap.camPosition.y = edges[3] - HexMap.getYOffset();
            }
        }
        if (Boolean.TRUE.equals(keysHeld.get(Input.Keys.A))) {
            HexMap.camPosition.x -= modifiedMoveSpeed;

            if (HexMap.camPosition.x < edges[1]) {
                HexMap.camPosition.x = edges[1];
            }
        }
        if (Boolean.TRUE.equals(keysHeld.get(Input.Keys.D))) {
            HexMap.camPosition.x += modifiedMoveSpeed;
            if (HexMap.camPosition.x > edges[0]) {
                HexMap.camPosition.x = edges[0];
            }
        }
        if (Boolean.TRUE.equals(keysHeld.get(Input.Keys.Q))) {
            zoom += zoomSpeed;
            if (zoom > maxZoom * 3) {
                zoom = maxZoom * 3;
            }

            if (HexMap.camPosition.y + HexMap.getYOffset() < edges[3]) {
                HexMap.camPosition.y = edges[3] - HexMap.getYOffset();
            }

            HexMap.zoomCamera(HexMap.camPosition, (float) zoom);
        }
        if (Boolean.TRUE.equals(keysHeld.get(Input.Keys.E))) {
            zoom -= zoomSpeed;
            if (zoom < minZoom) {
                zoom = minZoom;
            }

            if (HexMap.camPosition.y + HexMap.getYOffset() > edges[2]) {
                HexMap.camPosition.y = edges[2] - HexMap.getYOffset();
            }

            HexMap.zoomCamera(HexMap.camPosition, (float) zoom);
        }
    }
}
