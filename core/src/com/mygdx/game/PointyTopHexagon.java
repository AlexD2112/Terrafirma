package com.mygdx.game;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.VertexAttributes;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.utils.MeshPartBuilder;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector3;

public class PointyTopHexagon {
    private ModelInstance instance;

    public PointyTopHexagon(float size, Color color) {
        ModelBuilder modelBuilder = new ModelBuilder();
        modelBuilder.begin();
        MeshPartBuilder builder = modelBuilder.part("hexagon", GL20.GL_TRIANGLES, VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal, new Material(ColorAttribute.createDiffuse(color)));
        float radius = size / (float)Math.sqrt(3); // Calculate radius based on the size
        Vector3[] vertices = new Vector3[6];

        for (int i = 0; i < 6; i++) {
            float angle = (float)Math.toRadians(30 + i * 60); // 30 degree offset for pointy top
            float x = radius * MathUtils.cos(angle);
            float y = radius * MathUtils.sin(angle);
            vertices[i] = new Vector3(x, y, 0);
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

        instance = new ModelInstance(modelBuilder.end());
    }

    public ModelInstance getModelInstance() {
        return instance;
    }
}
