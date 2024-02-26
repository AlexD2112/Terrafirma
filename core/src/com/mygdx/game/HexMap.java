package com.mygdx.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g3d.*;
import com.badlogic.gdx.graphics.g3d.utils.MeshPartBuilder;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import org.hexworks.mixite.core.api.Hexagon;
import org.hexworks.mixite.core.api.HexagonalGrid;
import org.hexworks.mixite.core.api.contract.SatelliteData;
//Import epsilonequals
import com.badlogic.gdx.math.Matrix4;


import java.util.ArrayList;

public class HexMap {
    public static PerspectiveCamera cam;
    public ModelBatch modelBatch;
    public Model model;
    public static Vector3 camPosition = new Vector3(0, 0, 0);
    protected HexagonalGrid<SatelliteData> grid;
    protected float hexSize;
    private static int origYOffset = 200;
    protected static float yOffset = 200;
    protected static float zOffset = 400;
    protected static ArrayList<GameHexagon> hexagons;

    public HexMap(HexagonalGrid<SatelliteData> grid) {
        modelBatch = new ModelBatch();
        cam = new PerspectiveCamera(67, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        cam.position.set(0f, 10f, 10f);
        cam.lookAt(0,0,0);
        cam.near = 0.01f;
        cam.far = 300f;
        cam.update();
        this.grid = grid;
        Hexagon<SatelliteData> hexagon = grid.getHexagons().iterator().next();
        Vector2 hexPoint = new Vector2((float) hexagon.getCenterX(), (float) hexagon.getCenterY());

        HexMap.camPosition = new Vector3(hexPoint.x, hexPoint.y - yOffset, zOffset);
    }

    public void init(float hexSize) {}

    public void dispose() {
        modelBatch.dispose();
        model.dispose();
    }

    public void render() {}

    protected static void zoomCamera(Vector3 camPosition, float zoom) {
        camPosition.z = zOffset / zoom;
        yOffset = (origYOffset / zoom);
    }

    protected void initHexagons() {
        hexagons = new ArrayList<>();
        for (Hexagon<SatelliteData> hexagon : grid.getHexagons()) {
            if (hexagon.getSatelliteData().isPresent()) {
                CustomSatelliteData satelliteData = (CustomSatelliteData) hexagon.getSatelliteData().get();
                Vector2 hexCenter = new Vector2((float) hexagon.getCenterX(), (float) hexagon.getCenterY());
                Color color = satelliteData.getColor();
                GameHexagon.Builder builder = new GameHexagon.Builder();
                GameHexagon instance = builder.setSize(hexSize).setColor(color).build();
                instance.transform.setToTranslation(new Vector3(hexCenter.x, hexCenter.y, 0));
                //Append to instances
                hexagons.add(instance);
            }
        }
    }

    private Vector3 positionCheck = new Vector3();
    protected boolean isVisible(final Camera cam, final GameHexagon instance) {
        instance.transform.getTranslation(positionCheck);
        return cam.frustum.sphereInFrustum(positionCheck, instance.getRadius());
    }

    /**
     * Gets y offset
     *
     * @return yOffset
     */
    public static float getYOffset() {
        return yOffset;
    }
}