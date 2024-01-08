package com.mygdx.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import org.hexworks.mixite.core.api.*;
import org.hexworks.mixite.core.api.contract.SatelliteData;
import org.hexworks.mixite.core.vendor.Maybe;
import space.earlygrey.shapedrawer.ShapeDrawer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ZoomMap {
    private TextureRegion whitePixelRegion;
    HexMap hexMap;

    ArrayList<Hexagon<SatelliteData>> hexArray;
    public ZoomMap(HexMap hexMap) {
        //Get edges from hexagon grid
        this.hexMap = hexMap;

        Pixmap pixmap = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        pixmap.setColor(Color.WHITE);
        pixmap.fill();

        Texture whiteTexture = new Texture(pixmap); // don't forget to dispose of this later
        pixmap.dispose();
        whitePixelRegion = new TextureRegion(whiteTexture);
    }

    public void renderMap(Batch batch, double width, double height, float recedeFactor, Vector2 screenCenter, double zoom) {
        //Find current hexagon, as well as "nearest hexagons" if they exist, and render them
        HexagonalGrid<SatelliteData> grid = hexMap.getGrid();
        //Make a copy of screen center
        Vector2 currentCenter = new Vector2((float) (width / 2), (float) (height / 2));
        currentCenter = DisplayFunctions.reverseTransformation(currentCenter, width, height, recedeFactor, screenCenter, zoom);

        Maybe<Hexagon<SatelliteData>> maybeHex = hexMap.getGrid().getByPixelCoordinate(currentCenter.x, currentCenter.y);
        if (maybeHex.isPresent()) {
            Hexagon<SatelliteData> hex = maybeHex.get();
            hexArray = new ArrayList<>();
            hexArray.add(hex);
        } else {
            //Check each corner of the screen to see if it's in a hexagon
            Vector2[] screenCorners = new Vector2[4];
            screenCorners[0] = new Vector2(0, 0);
            screenCorners[1] = new Vector2((float) width, 0);
            screenCorners[2] = new Vector2(0, (float) height);
            screenCorners[3] = new Vector2((float) width, (float) height);

            for (Vector2 corner : screenCorners) {
                corner = DisplayFunctions.reverseTransformation(corner, width, height, recedeFactor, screenCenter, zoom);
                Maybe<Hexagon<SatelliteData>> maybeHex2 = hexMap.getGrid().getByPixelCoordinate(corner.x, corner.y);
                if (maybeHex2.isPresent()) {
                    hexArray.add(maybeHex2.get());
                    break;
                }
            }
        }
        if (hexArray.isEmpty()) {
            return;
        }
        hexArray.addAll(grid.getNeighborsOf(hexArray.get(0)));

        //Log all coordinates in hexArray
        int j = 0;
        for (Hexagon<SatelliteData> hex : hexArray) {
            System.out.println("Hexagon" + j + ": " + hex.getGridX() + ", " + hex.getGridY() + ", " + hex.getGridZ());
            j++;
        }

        ShapeDrawer shapeDrawer = new ShapeDrawer(batch, whitePixelRegion);

        shapeDrawer.setColor(Color.WHITE);
        for (Hexagon<SatelliteData> hexagon : hexArray) {
            List<Point> hexPoints = hexagon.getPoints();
            int numPoints = hexPoints.size();
            float[] vertices = new float[numPoints * 2];

            for (int i = 0; i < numPoints; i++) {
                Point p = hexPoints.get(i);
                Vector2 transformedPoint = DisplayFunctions.transformPoint(new Vector2((float) p.getCoordinateX(), (float) p.getCoordinateY()), width, height, recedeFactor, screenCenter, zoom);
                vertices[i * 2] = transformedPoint.x;
                vertices[i * 2 + 1] = transformedPoint.y;
            }
            shapeDrawer.setColor(((CustomSatelliteData) hexagon.getSatelliteData().get()).getColor());
            shapeDrawer.filledPolygon(vertices);
        }
    }
}
