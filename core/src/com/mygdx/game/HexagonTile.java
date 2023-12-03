package com.mygdx.game;

import com.badlogic.gdx.math.Vector2;

public class HexagonTile {
    private float x, y; // Center position of the hexagon
    private static final float SIZE = 10; // The size of the hexagon, can be a constant

    public HexagonTile(float x, float y) {
        this.x = x;
        this.y = y;
    }

    public static float getSize() {
        return SIZE;
    }

    public Vector2[] getHexagonPoints() {
        Vector2[] points = new Vector2[6];
        for (int i = 0; i < 6; i++) {
            float angle = (float) (Math.PI / 3 * i);
            float pointX = x + SIZE * (float) Math.cos(angle);
            float pointY = y + SIZE * (float) Math.sin(angle);
            points[i] = new Vector2(pointX, pointY);
        }
        return points;
    }
}
