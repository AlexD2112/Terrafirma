package com.mygdx.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.VertexAttributes;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.utils.MeshPartBuilder;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import org.hexworks.mixite.core.api.CubeCoordinate;
import org.hexworks.mixite.core.api.Hexagon;
import org.hexworks.mixite.core.api.HexagonalGrid;
import org.hexworks.mixite.core.api.contract.SatelliteData;

import java.util.ArrayList;

public class StrategicMap extends HexMap{
    public StrategicMap(HexagonalGrid<SatelliteData> grid) {
        super(grid);
        modelBatch = new ModelBatch();

        cam = new PerspectiveCamera(67, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        cam.position.set(0f, 10f, 10f);
        cam.lookAt(0,0,0);
        cam.near = 0.01f;
        cam.far = 4000f;
        cam.update();
    }   

    @Override
    public void init(double width, double height, double zoom, double maxZoom, float hexSize) {
        super.hexSize = hexSize;
        initInstances();
    }

    public void dispose() {
        modelBatch.dispose();
        model.dispose();
    }

    @Override
    public void render() {
        Gdx.gl.glViewport(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);

        //Get first hexagon
//        System.out.println(firstHex.getCenterX() + " " + firstHex.getCenterY());
//        System.out.println(camPosition.x + " " + camPosition.y + " " + camPosition.z);


        cam.position.set(camPosition);
        cam.lookAt(camPosition.x, camPosition.y + yOffset, 0);
        cam.update();

        System.out.println("Camera position: " + cam.position);
        System.out.println("Camera look-at point: " + new Vector3(camPosition.x, camPosition.y + yOffset, 0));

        modelBatch.begin(cam);
        for (ModelInstance instance : instances) {
            modelBatch.render(instance);
        }
        modelBatch.end();
    }
}