package com.mygdx.game;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import org.hexworks.mixite.core.api.Hexagon;
import org.hexworks.mixite.core.api.contract.SatelliteData;
import org.hexworks.mixite.core.api.defaults.DefaultSatelliteData;

//Implement satellite data
public class CustomSatelliteData extends DefaultSatelliteData {
    private Color color;
    private GameHexagon hexagon;

    public CustomSatelliteData(Color color) {
        this.color = color;
    }

    public Color getColor() {
        if (color == null) {
            return Color.WHITE;
        }
        return color;
    }

    public void setGameHexagon(GameHexagon hexagon) {
        this.hexagon = hexagon;
    }

    public void setColor(Color color) {
        this.color = color;
    }

    public GameHexagon getGameHexagon() {
        return hexagon;
    }

    public void refreshGameHexagon(float hexSize, Hexagon<SatelliteData> myHex) {
        hexagon.setSize(hexSize);
        hexagon.setColor(color);
        hexagon.setPosition(new Vector2((float) myHex.getCenterX(), (float) myHex.getCenterY()));
        hexagon.refreshInstance();
    }
}
