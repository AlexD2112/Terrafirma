package com.mygdx.game;

import com.badlogic.gdx.graphics.Color;
import org.hexworks.mixite.core.api.defaults.DefaultSatelliteData;

//Implement satellite data
public class CustomSatelliteData extends DefaultSatelliteData {
    private Color color;

    public CustomSatelliteData(Color color) {
        this.color = color;
    }

    public Color getColor() {
        if (color == null) {
            return Color.WHITE;
        }
        return color;
    }
}
