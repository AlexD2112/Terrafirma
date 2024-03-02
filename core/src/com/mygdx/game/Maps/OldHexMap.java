package com.mygdx.game.Maps;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.mygdx.game.CustomSatelliteData;
import org.hexworks.mixite.core.api.*;
import org.hexworks.mixite.core.api.contract.SatelliteData;
import space.earlygrey.shapedrawer.ShapeDrawer;

import java.util.List;

public class OldHexMap {
    private TextureRegion whitePixelRegion;
    private HexagonalGridBuilder<SatelliteData> builder;
    private int doneCount = 0;
    private HexagonalGrid<SatelliteData> grid;
    private Color[][] hexColorArray;
    private int hexDensity;


    public OldHexMap(int width, int height, int hexDensity, int screenWidth) {
        this.hexDensity = hexDensity;
        builder = new HexagonalGridBuilder<>()
                .setGridHeight(height)
                .setGridWidth(width)
                .setGridLayout(HexagonalGridLayout.RECTANGULAR)
                .setOrientation(HexagonOrientation.POINTY_TOP)
                .setRadius(((double) screenWidth / hexDensity) / Math.sqrt(3)); //Radius is the distance from the center to corner

        grid = builder.build();

        for (Hexagon<SatelliteData> hexagon : grid.getHexagons()) {
            hexagon.setSatelliteData(new CustomSatelliteData(new Color(MathUtils.random(), MathUtils.random(), MathUtils.random(), 1)));
        }


        Pixmap pixmap = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        pixmap.setColor(Color.WHITE);
        pixmap.fill();
        Texture whiteTexture = new Texture(pixmap); // don't forget to dispose of this later
        pixmap.dispose();
        whitePixelRegion = new TextureRegion(whiteTexture);
    }

    public HexagonalGrid<SatelliteData> getGrid() {
        return grid;
    }

    public int[] getEdges() {
        //Get edges from hexagon grid
        int[] edges = new int[4];
        for (Hexagon<SatelliteData> hexagon : grid.getHexagons()) {
            Point p = hexagon.getPoints().get(0);
            if (p.getCoordinateX() > edges[0]) {
                edges[0] = (int) p.getCoordinateX();
            }
            if (p.getCoordinateX() < edges[1]) {
                edges[1] = (int) p.getCoordinateX();
            }
            if (p.getCoordinateY() > edges[2]) {
                edges[2] = (int) p.getCoordinateY();
            }
            if (p.getCoordinateY() < edges[3]) {
                edges[3] = (int) p.getCoordinateY();
            }
        }

        return edges;
    }

    public int getHexDensity() {
        return hexDensity;
    }
}
