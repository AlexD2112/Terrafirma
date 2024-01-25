package com.mygdx.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.VertexAttributes;
import com.badlogic.gdx.graphics.g3d.*;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.environment.DirectionalLight;
import com.badlogic.gdx.graphics.g3d.utils.MeshPartBuilder;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.graphics.g3d.utils.shapebuilders.EllipseShapeBuilder;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import org.hexworks.mixite.core.api.Hexagon;
import org.hexworks.mixite.core.api.HexagonalGrid;
import org.hexworks.mixite.core.api.contract.SatelliteData;

public class ZoomMap {
    public PerspectiveCamera cam;
    public ModelBatch modelBatch;
    public Model model;
    public ModelInstance instance;

    public ZoomMap() {
        modelBatch = new ModelBatch();

        cam = new PerspectiveCamera(67, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        cam.position.set(0f, 10f, 10f);
        cam.lookAt(0,0,0);
        cam.near = 1f;
        cam.far = 300f;
        cam.update();
    }

    public void dispose() {
        modelBatch.dispose();
        model.dispose();
    }

    public void renderZoom(double width, double height, double recedeFactor, Vector2 screenCenter, double zoom, double maxZoom, HexMap hexMap) {
        Gdx.gl.glViewport(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);

        Hexagon<SatelliteData> hexagon = DisplayFunctions.getHexFromPoint(screenCenter, hexMap, width, height, recedeFactor, screenCenter, zoom, hexMap.getHexDensity());
        Vector2 hexPoint = new Vector2((float) hexagon.getCenterX(), (float) hexagon.getCenterY());

        //Seamlessly transition to draw the hexagon being looked at
        cam.position.set(hexPoint, 10);
        cam.lookAt(hexPoint.x, hexPoint.y, 0);
        cam.update();


        //Get the hexagon being looked at
        //Resolve SatelliteData to CustomSatteliteData
        CustomSatelliteData satelliteData = (CustomSatelliteData) hexagon.getSatelliteData().get();
        Color color = satelliteData.getColor();

        ModelBuilder modelBuilder = new ModelBuilder();
        modelBuilder.begin();
        MeshPartBuilder builder = modelBuilder.part("hexagon", GL20.GL_TRIANGLES, VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal, new Material(ColorAttribute.createDiffuse(color)));
        createPointyTopHexagon(builder, 1, new Vector3(hexPoint, 0));
        model = modelBuilder.end();
        instance = new ModelInstance(model);
        modelBatch.begin(cam);
        modelBatch.render(instance);
        modelBatch.end();
    }

    private void createPointyTopHexagon(MeshPartBuilder builder, float size, Vector3 center) {
        float radius = size / (float)Math.sqrt(3); // Calculate radius based on the size
        Vector3[] vertices = new Vector3[6];

        for (int i = 0; i < 6; i++) {
            float angle = (float)Math.toRadians(30 + i * 60); // 30 degree offset for pointy top
            float x = center.x + radius * MathUtils.cos(angle);
            float y = center.y + radius * MathUtils.sin(angle);
            vertices[i] = new Vector3(x, y, center.z);
        }

        for (int i = 0; i < 6; i++) {
            builder.vertex(vertices[i].x, vertices[i].y, vertices[i].z,
                    0, 0, 1, // Normal vector pointing up
                    Color.rgb888(1, 2, 3), // Color (white)
                    0, 0); // UV coordinates (unused)
            if (i > 0) {
                builder.triangle((short)0, (short)i, (short)(i + 1));
            }
        }

        // Connect the last vertex back to the first one to complete the hexagon
        builder.triangle((short)0, (short)6, (short)1);
    }
}