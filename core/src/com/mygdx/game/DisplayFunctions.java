package com.mygdx.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;

public class DisplayFunctions {
    public static Vector2 transformPoint(Vector2 point, double width, double height, double recedeFactor, Vector2 screenCenter, double zoom) {
        point.x -= screenCenter.x;
        point.y += ((float) height / 2) - screenCenter.y;

        boolean overEvenPoint = false;

        double yFactor = point.y / height;

        if (yFactor > 1.2) {
            overEvenPoint = true;
        }

        double scaleFactorX;
        double scaleFactorY;
        //Function to avoid crossing 0 point on y axis for function that gets integrated
        if (overEvenPoint) {
            //Scaling factor x = (1287 / 1600) * (1 / (yFactor + 0.449990808237))
            scaleFactorX = (1287f / 1600f) * (1f / (yFactor + 0.449990808237f));
            //Integral of scaleFactorX
            scaleFactorY = (1287f / 1600f) * Math.log(yFactor + 0.449990808237f) + 0.4497;
        } else {
            //Scaling factor x = 1 - recedeFactor * ln (yFactor + 1)
            scaleFactorX = 1 - recedeFactor * Math.log(yFactor + 1);
            //Integral of scaleFactorX
            scaleFactorY = (1 + recedeFactor) * yFactor - recedeFactor * yFactor * Math.log(yFactor + 1) - recedeFactor * Math.log(yFactor + 1);
        }
        //Scale factor but slowed down
        //Transformed x is the x value of the point, minus the x value of the screen center, times the scale factor, plus the x value of the screen center
        double transformedY = scaleFactorY * height;
        double transformedX = scaleFactorX * point.x + (width / 2f);

        transformedX = (transformedX - (width / 2f)) * zoom + (width / 2f);
        transformedY *= zoom;

        return new Vector2((float) transformedX, (float) transformedY);
    }
}
