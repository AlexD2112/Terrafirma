package com.mygdx.game;

import com.badlogic.gdx.math.Vector2;
import org.hexworks.mixite.core.api.Hexagon;
import org.hexworks.mixite.core.api.HexagonalGrid;
import org.hexworks.mixite.core.api.contract.SatelliteData;
import org.hexworks.mixite.core.vendor.Maybe;

public class DisplayFunctions {
    private static final int inversionAccuracy = 10; //How many terms of the base equation to compare to find the closest value
    private static HexagonalGrid<SatelliteData> hexGrid;
    private static double width, height;
    private static int hexDensity;

    public static void initDisplayFunctions(HexagonalGrid<SatelliteData> hexGrid, double width, double height, int hexDensity) {
        DisplayFunctions.hexGrid = hexGrid;
        DisplayFunctions.width = width;
        DisplayFunctions.height = height;
        DisplayFunctions.hexDensity = hexDensity;
    }

    public static Hexagon<SatelliteData> getHexFromPoint(Vector2 point) {
        //Get hexagon from point
        Maybe<Hexagon<SatelliteData>> hexMaybe = hexGrid.getByPixelCoordinate(point.x, point.y);
        //Manually check if point is in hexagon
        if (hexMaybe.isPresent()) {
            Hexagon<SatelliteData> hex = hexMaybe.get();
            if (DisplayFunctions.isPointInHexagon(point, hex, ((double) width / hexDensity) / Math.sqrt(3))) {
                return hex;
            }
        }
        return null;
    }

    public static float getHexWidth() {
        return (float) width / hexDensity;
    }

    public static boolean isPointInHexagon(Vector2 point, Hexagon<SatelliteData> hex, double hexRadius) {
        //Divide hexagon into 6 triangles, and check if point is in any of them
        Vector2 center = new Vector2((float) hex.getCenterX(), (float) hex.getCenterY());
        for (int i = 0; i < 6; i++) {
            //Might seem reversed with usage of cosine and sine. This way because of pointy top hexagons
            Vector2 point1 = new Vector2((float) (hex.getCenterX() + hexRadius * Math.sin(i * Math.PI / 3)), (float) (hex.getCenterY() + hexRadius * Math.cos(i * Math.PI / 3)));
            Vector2 point2 = new Vector2((float) (hex.getCenterX() + hexRadius * Math.sin((i + 1) * Math.PI / 3)), (float) (hex.getCenterY() + hexRadius * Math.cos((i + 1) * Math.PI / 3)));
            if (isPointInTriangle(point, center, point1, point2)) {
                return true;
            }
        }
        return false;
    }

    private static boolean isPointInTriangle(Vector2 checkPoint, Vector2 point1, Vector2 point2, Vector2 point3) {
        //Check if point is in triangle
        float d1, d2, d3;
        boolean has_neg, has_pos;

        d1 = sign(checkPoint, point1, point2);
        d2 = sign(checkPoint, point2, point3);
        d3 = sign(checkPoint, point3, point1);

        has_neg = (d1 < 0) || (d2 < 0) || (d3 < 0);
        has_pos = (d1 > 0) || (d2 > 0) || (d3 > 0);

        return !(has_neg && has_pos);
    }

    private static float sign(Vector2 p1, Vector2 p2, Vector2 p3) {
        return ((p1.x - p3.x) * (p2.y - p3.y) - (p2.x - p3.x) * (p1.y - p3.y));
    }
}
