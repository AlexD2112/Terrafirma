package com.mygdx.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g3d.*;
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
//Import epsilonequals
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Matrix4;


import java.util.ArrayList;

public class HexMap {
    public PerspectiveCamera cam;
    public ModelBatch modelBatch;
    public Model model;
    public static Vector3 camPosition = new Vector3(0, 0, 0);
    protected HexagonalGrid<SatelliteData> grid;
    protected float hexSize;
    private static int origYOffset = 200;
    protected static float yOffset = 200;
    protected static float zOffset = 400;
    protected static ArrayList<ModelInstance> instances;

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

    public void init(double width, double height, double zoom, double maxZoom, float hexSize) {}

    public void dispose() {
        modelBatch.dispose();
        model.dispose();
    }

    public void render() {}

    protected void createPointyTopHexagon(MeshPartBuilder builder, float size, Vector3 center) {
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

    protected static void zoomCamera(Vector3 camPosition, float zoom) {
        camPosition.z = zOffset / zoom;
        yOffset = (origYOffset / zoom);
    }

    protected void initInstances() {
        instances = new ArrayList<>();
        for (Hexagon<SatelliteData> hexagon : grid.getHexagons()) {
            if (hexagon.getSatelliteData().isPresent()) {
                CustomSatelliteData satelliteData = (CustomSatelliteData) hexagon.getSatelliteData().get();
                Vector2 hexCenter = new Vector2((float) hexagon.getCenterX(), (float) hexagon.getCenterY());
                Color color = satelliteData.getColor();
                ModelInstance instance = new PointyTopHexagon(hexSize, color).getModelInstance();
                instance.transform.setToTranslation(new Vector3(hexCenter.x, hexCenter.y, 0));
                System.out.println(instance.transform.getTranslation(new Vector3()));
                //Append to instances
                instances.add(instance);
            }
        }
    }

    public boolean compareHexagons(ModelInstance hex1, ModelInstance hex2) {
        // Compare transformations
        if (!epsilonEquals(hex1.transform, hex2.transform, 0.0001f)) {
            System.out.println(hex1.transform);
            System.out.println("HEYO");
            System.out.println(hex2.transform);
            System.out.println("Differences in transformations.");
            return false;
        }

        // Compare materials and attributes
        for (int i = 0; i < hex1.materials.size; i++) {
            Material mat1 = hex1.materials.get(i);
            Material mat2 = hex2.materials.get(i); // Ensure hex2 has the same number of materials

            for (Attribute attr1 : mat1) {
                Attribute attr2 = mat2.get(attr1.type);
                if (!attr1.equals(attr2)) {
                    System.out.println("Differences in material attributes.");
                    return false;
                }
            }
        }

        // Compare vertex data (more complex, requires accessing the Mesh and its vertices)
        Mesh mesh1 = hex1.model.meshes.first();
        Mesh mesh2 = hex2.model.meshes.first(); // Simplification, assumes single mesh

        // You need to extract and compare vertex data here, which can be complex due to different formats

        return true; // No differences found
    }

    public boolean epsilonEquals(Matrix4 matrix1, Matrix4 matrix2, float epsilon) {
        for (int i = 0; i < 16; i++) {
            if (Math.abs(matrix1.val[i] - matrix2.val[i]) > epsilon) {
                return false;
            }
        }
        return true;
    }
}