package com.mygdx.game.GameObjects;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
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

public class GameHexagon {
    private ModelInstance instance;
    private float size;
    private Color color;
    private Vector2 position;

    // Private constructor that accepts a ModelBuilder
    public GameHexagon(float size, Color color, Vector2 position) {
        this.size = size;
        this.color = color;
        this.position = position;
        refreshInstance();
    }

    public void setSize(float size) {
        this.size = size;
    }
    public void setColor(Color color) {
        this.color = color;
    }

    public void setPosition(Vector2 position) {
        this.position = position;
    }

    public void renderHex(ModelBatch modelBatch) {
        modelBatch.render(instance);
    }

    public Vector3 getTranslation(Vector3 positionCheck) {
        return instance.transform.getTranslation(positionCheck);
    }

    public float getRadius() {
        return size / (float) Math.sqrt(3);
    }

    public void refreshInstance() {
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

        for (int i = 0; i < 5; i++) { // Fix to loop to 5 to avoid IndexOutOfBoundsException
            builder.vertex(vertices[i].x, vertices[i].y, vertices[i].z,
                    0, 0, 1, // Normal vector pointing up
                    Color.rgb888(color.r, color.g, color.b), // Use the color from the builder
                    0, 0); // UV coordinates (unused)
            builder.triangle((short)0, (short)(i + 1), (short)(i + 2));
        }

        // Connect the last vertex back to the first one to complete the hexagon
        builder.vertex(vertices[5].x, vertices[5].y, vertices[5].z,
                0, 0, 1,
                Color.rgb888(color.r, color.g, color.b),
                0, 0);
        builder.triangle((short)0, (short)6, (short)1);

        instance = new ModelInstance(modelBuilder.end());
        instance.transform.setToTranslation(new Vector3(position.x, position.y, 0));
    }
}
