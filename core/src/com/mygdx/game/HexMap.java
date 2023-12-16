package com.mygdx.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import org.hexworks.mixite.core.api.*;
import org.hexworks.mixite.core.api.contract.SatelliteData;
import org.hexworks.mixite.core.api.HexagonalGridBuilder;
import org.hexworks.mixite.core.api.HexagonalGridLayout;
import org.hexworks.mixite.core.api.contract.SatelliteData;

import java.util.List;

public class HexMap {
    private HexagonalGridBuilder<SatelliteData> builder;
    private int doneCount = 0;
    private HexagonalGrid<SatelliteData> grid;
    private Color[][] hexColorArray;


    public HexMap(int width, int height, int screenWidth) {
        builder = new HexagonalGridBuilder<>()
                .setGridHeight(height)
                .setGridWidth(width)
                .setGridLayout(HexagonalGridLayout.RECTANGULAR)
                .setOrientation(HexagonOrientation.POINTY_TOP)
                .setRadius(((double) screenWidth / 6) / Math.sqrt(3)); //Radius is the distance from the center to corner

        grid = builder.build();

        for (Hexagon<SatelliteData> hexagon : grid.getHexagons()) {
            hexagon.setSatelliteData(new CustomSatelliteData(new Color(MathUtils.random(), MathUtils.random(), MathUtils.random(), 1)));
        }
    }

    public HexagonalGrid<SatelliteData> getGrid() {
        return grid;
    }

    public void renderMap(ShapeRenderer shapeRenderer, double width, double height, float recedeFactor, Vector2 screenCenter, double zoom) {
        boolean problem = false;
        int problemIndex = 0;
        for (Hexagon<SatelliteData> hexagon : grid.getHexagons()) {
            List<Point> hexPoints = hexagon.getPoints();
            int numPoints = hexPoints.size();
            Vector2[] points = new Vector2[numPoints];

            for (int i = 0; i < numPoints; i++) {
                problem = false;
                Point p = hexPoints.get(i);
                Vector2 transformedPoint = DisplayFunctions.transformPoint(new Vector2((float) p.getCoordinateX(), (float) p.getCoordinateY()), width, height, recedeFactor, screenCenter, zoom);
                //Check if transformed point values are not numbers
                if ((Float.isNaN(transformedPoint.x) || Float.isNaN(transformedPoint.y)) && doneCount == 0) {
                    transformedPoint.x = 0;
                    transformedPoint.y = (float) (-2 * height); //To avoid potential errors- will be off screen
                }
                points[i] = transformedPoint;
            }

            //Create an object equal to hexagon satellite data, error handling sets color to white if it doesnt exist
            SatelliteData satelliteData = hexagon.getSatelliteData().get();
            Color hexColor;
            if (satelliteData instanceof CustomSatelliteData) {
                hexColor = ((CustomSatelliteData) satelliteData).getColor();
            } else {
                hexColor = new Color(255, 255, 255, 1);
            }
            //Texture textureSolid = makeTextureBox();

            shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
            shapeRenderer.setColor(hexColor);

            // Prepare an array to store the coordinates for the polygon
            float[] vertices = new float[points.length * 2];

            // Fill the array with the points' coordinates
            for (int i = 0, j = 0; i < points.length; i++) {
                vertices[j++] = points[i].x; // Add the x-coordinate
                vertices[j++] = points[i].y; // Add the y-coordinate
            }

            // Draw the polygon


            //Draw polygon shape
            shapeRenderer.polygon(new float[]{points[0].x, points[0].y, points[1].x, points[1].y, points[2].x, points[2].y, points[3].x, points[3].y, points[4].x, points[4].y, points[5].x, points[5].y});

            shapeRenderer.end();
        }
    }
}
