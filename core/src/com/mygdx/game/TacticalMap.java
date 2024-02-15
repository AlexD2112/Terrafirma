package com.mygdx.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.VertexAttributes;
import com.badlogic.gdx.graphics.g3d.*;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.utils.MeshPartBuilder;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import org.hexworks.mixite.core.api.Hexagon;
import org.hexworks.mixite.core.api.HexagonalGrid;
import org.hexworks.mixite.core.api.contract.SatelliteData;

public class TacticalMap extends HexMap{
    private ModelInstance instance;
    private Hexagon[] hexagons = new Hexagon[1];
    private Vector2[] hexPoints = new Vector2[1];
    public TacticalMap(HexagonalGrid<SatelliteData> grid) {
        super(grid);
        modelBatch = new ModelBatch();

        cam = new PerspectiveCamera(67, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        cam.position.set(0f, 10f, 10f);
        cam.lookAt(0,0,0);
        cam.near = 0.01f;
        cam.far = 1000f;
        cam.update();
    }

    @Override
    public void init(double width, double height, double zoom, double maxZoom, float hexSize) {
        super.hexSize = hexSize;
    }

    public void dispose() {
        modelBatch.dispose();
        model.dispose();
    }

    @Override
    public void render() {
        Gdx.gl.glViewport(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);

        //Seamlessly transition to draw the hexagon being looked at
        cam.position.set(camPosition);
        cam.update();
        cam.lookAt(camPosition.x, camPosition.y + yOffset, 0);
        cam.update();


        //Get the hexagon being looked at
        //Resolve SatelliteData to CustomSatteliteData
        modelBatch.begin(cam);
        modelBatch.render(instances.get(0));
        modelBatch.end();
    }
}