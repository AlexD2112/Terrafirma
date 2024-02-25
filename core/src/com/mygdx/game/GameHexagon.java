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

public class GameHexagon extends ModelInstance {
    private float size;
    private Color color;

    // Private constructor that accepts a ModelBuilder
    private GameHexagon(ModelBuilder modelBuilder, float size, Color color) {
        super(modelBuilder.end()); // Call to super must be the first statement
        this.size = size;
        this.color = color;
    }

    public float getRadius() {
        return size / (float) Math.sqrt(3);
    }

    // Static Builder class
    public static class Builder {
        private float size;
        private Color color;

        public Builder setSize(float size) {
            this.size = size + 0.1F; // Adjust size as in the original design
            return this;
        }

        public Builder setColor(Color color) {
            this.color = color;
            return this;
        }

        public GameHexagon build() {
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


            // Use the ModelBuilder to construct the GameHexagon
            return new GameHexagon(modelBuilder, size, color);
        }
    }
}
