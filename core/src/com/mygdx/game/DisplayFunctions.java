package com.mygdx.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import org.hexworks.mixite.core.api.Hexagon;
import org.hexworks.mixite.core.api.contract.SatelliteData;
import org.hexworks.mixite.core.vendor.Maybe;

import static com.badlogic.gdx.math.Rectangle.tmp;

public class DisplayFunctions {
    private static final int inversionAccuracy = 10; //How many terms of the base equation to compare to find the closest value

    public static Hexagon getHexFromPoint(Vector2 point, HexMap hexMap, double width, double height, double recedeFactor, Vector2 screenCenter, double zoom, int hexDensity) {
        //Get hexagon from point
        point = DisplayFunctions.reverseTransformation(point, width, height, recedeFactor, screenCenter, zoom);
        Maybe<Hexagon<SatelliteData>> hexMaybe = hexMap.grid.getByPixelCoordinate(point.x, point.y);
        //Manually check if point is in hexagon
        if (hexMaybe.isPresent()) {
            Hexagon<SatelliteData> hex = hexMaybe.get();
            if (DisplayFunctions.isPointInHexagon(point, hex, ((double) width / hexDensity) / Math.sqrt(3))) {
                return hex;
            }
        }
        return null;
    }
    public static Vector2 transformPoint(Vector2 inputPoint, double width, double height, double recedeFactor, Vector2 screenCenter, double zoom) {
        Vector2 point = new Vector2(inputPoint);
        point.x -= screenCenter.x;
        point.y += ((float) height / 2) - screenCenter.y;

        point.x *= (float) zoom;
        point.y *= (float) zoom;


        double yFactor = point.y / height; //yFactor is the value of the point, adjusted to be over the screen

        double scaleFactorX; //X point (adjusted in respect to screen center) is multiplied by this value
        double scaleFactorY; //This is just the yFactor, or value in respect to the screen, but later modified.

        //Critical point is zero of main equations scale factor x, or e ^ (1 / recedeFactor) - 1
        double critPoint = Math.exp(1.0 / recedeFactor) - 1;


        //Checks if past the critical point
        if (yFactor > critPoint) { //Will create an unmodified graph off screen which should never be seen
            //Critical val is crit point plugged into the integral
            double critVal = (1 + recedeFactor) * critPoint - recedeFactor * critPoint * Math.log(critPoint + 1) - recedeFactor * Math.log(critPoint + 1);
            scaleFactorY = yFactor + critVal - critPoint;
            scaleFactorX = 1;
        } else if (yFactor > (-1)) { //MAIN EQUATION
            // 1 - 0.65 * ln (x + 1)
            scaleFactorX = 1 - recedeFactor * Math.log(yFactor + 1);
            //Integral of scaleFactorX
            scaleFactorY = (1 + recedeFactor) * yFactor - recedeFactor * yFactor * Math.log(yFactor + 1) - recedeFactor * Math.log(yFactor + 1);
        } else { //Avoids NaN errors
            scaleFactorY = -(1 + recedeFactor) + yFactor;
            scaleFactorX = 1;
        }

        //Transformed x is the x value of the point, minus the x value of the screen center, times the scale factor, plus the x value of the screen center
        double transformedY = scaleFactorY * height;
        double transformedX = scaleFactorX * point.x + (width / 2f);

        return new Vector2((float) transformedX, (float) transformedY);
    }
    public static Vector2 reverseTransformation(Vector2 point, double width, double height, double recedeFactor, Vector2 screenCenter, double zoom) {
        double scaleFactorX;

        double yFactor = point.y / height;

        //Make an array of 11 points from 0 to 1 (inclusive) to find the closest point to the yFactor
        double[] yFactors = new double[11];
        for (int i = 0; i < 11; i++) {
            yFactors[i] = yDerivative(0, recedeFactor, i / 10.0);
        }

        //Find the closest point to the yFactor
        double closestYFactor = yFactors[0];
        double closestX = 0;
        boolean found = false;
        for (int i = 1; i < (1 + inversionAccuracy); i++) {
            if (Math.abs(yFactor - yFactors[i]) < Math.abs(yFactor - closestYFactor)) {
                closestYFactor = yFactors[i];
                closestX = i / 10.0;
                found = true;
            } else if (found) {
                break;
            }
        }

        //Create taylor series coefficients
        double[] taylorCoefficients = new double[5];
        taylorCoefficients[0] = closestYFactor;
        taylorCoefficients[1] = yDerivative(1, recedeFactor, closestX);
        taylorCoefficients[2] = yDerivative(2, recedeFactor, closestX);
        taylorCoefficients[3] = yDerivative(3, recedeFactor, closestX);
        taylorCoefficients[4] = yDerivative(4, recedeFactor, closestX);


        //Create approximate inverse of taylor series using series reversion. First calculating a1-a3, than calculating A1-A3, and finally calculating adjusted yFactor
        double a1 = taylorCoefficients[1]; //Coefficient of first x term in taylor series
        double a2 = taylorCoefficients[2] / 2; //Coefficient of second x term in taylor series
        double a3 = taylorCoefficients[3] / 6; //Coefficient of third x term in taylor series

        double A1 = 1 / a1;
        double A2 = -a2 / (a1 * a1 * a1);
        double A3 = ((2 * a2 * a2) - (a1 * a3)) / (a1 * a1 * a1 * a1 * a1);
        yFactor =
                A1 * (yFactor - taylorCoefficients[0])
                + A2 * (yFactor - taylorCoefficients[0]) * (yFactor - taylorCoefficients[0])
                + A3 * (yFactor - taylorCoefficients[0]) * (yFactor - taylorCoefficients[0]) * (yFactor - taylorCoefficients[0])
                + closestX;

        scaleFactorX = 1 - recedeFactor * Math.log(yFactor + 1);

        //Invert modification on point.x
        double transformedX =  (point.x - (width / 2f)) / scaleFactorX;
        double transformedY = yFactor * height;
        transformedX /= zoom;
        transformedY /= zoom;
        transformedX += screenCenter.x;
        transformedY -= ((float) height / 2) - screenCenter.y;

        return new Vector2((float) transformedX, (float) transformedY);
    }

    //Precondition: x is greater than or equal to 0
    public static double yDerivative(int degree, double recedeFactor, double x) {
        //Used to calc taylor series- based on (1 + recedeFactor) * x - recedeFactor * x * Math.log(x + 1) - recedeFactor * Math.log(x + 1)
        if (x < 0) {
            //Throw error with gdx
            Gdx.app.error("DisplayFunctions", "yDerivative: x must be greater than or equal to 0");
            return -404;
        }
        switch (degree) {
            case 0:
                return (1 + recedeFactor) * x - recedeFactor * x * Math.log(x + 1) - recedeFactor * Math.log(x + 1);
            case 1:
                return 1 - recedeFactor * Math.log(x + 1);
            default:
                int mult = -1;
                for (int i = 1; i < (degree-1); i++) {
                    mult *= -i;
                }
                return mult * (recedeFactor / Math.pow(x + 1, degree-1));
        }
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
