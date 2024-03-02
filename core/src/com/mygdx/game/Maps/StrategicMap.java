package com.mygdx.game.Maps;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.mygdx.game.GameObjects.CloudCover;
import com.mygdx.game.GameObjects.GameHexagon;
import org.hexworks.mixite.core.api.HexagonalGrid;
import org.hexworks.mixite.core.api.contract.SatelliteData;
import space.earlygrey.shapedrawer.ShapeDrawer;

public class StrategicMap extends HexMap {
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
    public void init(float hexSize) {
        cam.far = 2000f;
        super.hexSize = hexSize;
        initHexagons();
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

        modelBatch.begin(cam);
        for (GameHexagon instance : hexagons) {
            if (isVisible(cam, instance)) {
                instance.renderHex(modelBatch);
            }
        }
        modelBatch.end();

        Gdx.gl.glEnable(GL20.GL_BLEND);
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);

        if (cloudCover > 0) {
            cloud.renderCloudCover(cloudCover);
        }

        Gdx.gl.glDisable(GL20.GL_BLEND);
    }
}