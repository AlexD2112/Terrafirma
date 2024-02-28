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

import java.util.ArrayList;
import java.util.List;

public class TacticalMap extends HexMap{
    private ModelInstance instance;
    public TacticalMap(HexagonalGrid<SatelliteData> grid) {
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
    public void init(float hexSize) {
        cam.far = 1000f;
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

        List<GameHexagon> hexagons = new ArrayList<>(7);

        //Get hexagon being looked at- if present, use getGameHexagon to get the game hexagon of this object and of adjacent hexs
        grid.getByPixelCoordinate(camPosition.x, camPosition.y + yOffset).ifPresent(hex -> {
            hexagons.add(((CustomSatelliteData) hex.getSatelliteData().get()).getGameHexagon());
            for (Hexagon<SatelliteData> adjacentHex : grid.getNeighborsOf(hex)) {
                hexagons.add(((CustomSatelliteData) adjacentHex.getSatelliteData().get()).getGameHexagon());
            }
        });

        modelBatch.begin(cam);
        for (GameHexagon instance : hexagons) {
            instance.renderHex(modelBatch);
        }
        modelBatch.end();
    }
}