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
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import space.earlygrey.shapedrawer.ShapeDrawer;

public class CloudCover {
    private float width;
    private float height;
    private Color color;
    private Vector2 position;
    private ShapeRenderer renderer;

    // Private constructor that accepts a ModelBuilder
    public CloudCover(float height, float width) {
        this.height = height;
        this.width = width;
        this.color = Color.WHITE;
        renderer = new ShapeRenderer();
        position = new Vector2(0, 0);
    }

    public void setSize(float width, float height) {
        this.width = width;
        this.height = height;
    }

    public void renderCloudCover(float opacity) {
        renderer.begin(ShapeRenderer.ShapeType.Filled);
        renderer.setColor(color.r, color.g, color.b, opacity);
        renderer.rect(position.x, position.y, width, height);
        renderer.end();
    }
}
