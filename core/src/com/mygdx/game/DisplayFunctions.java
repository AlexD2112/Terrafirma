package com.mygdx.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;

public class DisplayFunctions {
    public static Vector2 transformPoint(Vector2 point, double width, double height, double recedeFactor, Vector2 screenCenter, double zoom) {
        point.x -= screenCenter.x;
        point.y += ((float) height / 2) - screenCenter.y;

        point.x *= zoom;
        point.y *= zoom;


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
            Gdx.app.log("CritPoint", String.valueOf(critPoint));
            Gdx.app.log("critVal", String.valueOf(critVal));
            Gdx.app.log("scaleFactorY", String.valueOf(scaleFactorY));
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
}
