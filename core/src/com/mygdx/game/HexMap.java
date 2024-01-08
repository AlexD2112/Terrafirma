package com.mygdx.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import org.hexworks.mixite.core.api.*;
import org.hexworks.mixite.core.api.contract.SatelliteData;
import org.hexworks.mixite.core.api.HexagonalGridBuilder;
import org.hexworks.mixite.core.api.HexagonalGridLayout;
import org.hexworks.mixite.core.api.contract.SatelliteData;
import space.earlygrey.shapedrawer.ShapeDrawer;

import java.util.List;

public class HexMap {
    private TextureRegion whitePixelRegion;
    private HexagonalGridBuilder<SatelliteData> builder;
    private int doneCount = 0;
    private HexagonalGrid<SatelliteData> grid;
    private Color[][] hexColorArray;


    public HexMap(int width, int height, int hexDensity, int screenWidth) {
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

    public void renderMap(Batch batch, double width, double height, float recedeFactor, Vector2 screenCenter, double zoom, double maxZoom) {
        double greyScale = 0;
        if (zoom > maxZoom) {
            greyScale = (zoom - maxZoom) / maxZoom;
        }

        for (Hexagon<SatelliteData> hexagon : grid.getHexagons()) {
            List<Point> hexPoints = hexagon.getPoints();
            int numPoints = hexPoints.size();
            Vector2[] points = new Vector2[numPoints];

            for (int i = 0; i < numPoints; i++) {
                Point p = hexPoints.get(i);
                Vector2 transformedPoint = DisplayFunctions.transformPoint(new Vector2((float) p.getCoordinateX(), (float) p.getCoordinateY()), width, height, recedeFactor, screenCenter, zoom);
                // Check if transformed point values are not numbers
                if ((Float.isNaN(transformedPoint.x) || Float.isNaN(transformedPoint.y)) && doneCount == 0) {
                    transformedPoint.x = 0;
                    transformedPoint.y = (float) (-2 * height); //To avoid potential errors- will be off screen
                }
                points[i] = transformedPoint;
                points[i] = transformedPoint;
            }

            //Create an object equal to hexagon satellite data, error handling sets color to white if it doesnt exist
            SatelliteData satelliteData = hexagon.getSatelliteData().get();
            Color hexColor;
            if (satelliteData instanceof CustomSatelliteData) {
                //Clone getcolor
                hexColor = ((CustomSatelliteData) satelliteData).getColor().cpy();
                hexColor.a = (float) (1-greyScale);
            } else {
                hexColor = new Color(255, 255, 255, (float) (1-greyScale));
            }
            //Texture textureSolid = makeTextureBox();

            // Prepare an array to store the coordinates for the polygon
            float[] vertices = new float[points.length * 2];

            // Fill the array with the points' coordinates
            for (int i = 0, j = 0; i < points.length; i++) {
                vertices[j++] = points[i].x; // Add the x-coordinate
                vertices[j++] = points[i].y; // Add the y-coordinate
            }

            ShapeDrawer shapeDrawer = new ShapeDrawer(batch, whitePixelRegion);

            shapeDrawer.setColor(hexColor);

            shapeDrawer.filledPolygon(vertices);
        }
    }
}
