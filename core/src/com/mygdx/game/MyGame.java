package com.mygdx.game;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.mygdx.game.HexagonTile;
import com.mygdx.game.HexagonalGrid;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

public class MyGame extends ApplicationAdapter {
	private ShapeRenderer shapeRenderer;
	SpriteBatch batch;
	HexagonalGrid grid;
	// ... other variables like ShapeRenderer

	@Override
	public void create () {
		batch = new SpriteBatch();
		grid = new HexagonalGrid(10, 10); // Create a grid of hexagons
		shapeRenderer = new ShapeRenderer();
		// ... other initializations
	}

	@Override
	public void render () {
		Gdx.gl.glClearColor(1, 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		batch.begin();
		shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
		for (HexagonTile tile : grid.getTiles()) {
			drawHexagon(tile);
		}
		shapeRenderer.end();
		batch.end();
	}

	private void drawHexagon(HexagonTile tile) {
		Vector2[] hexPoints = tile.getHexagonPoints();
		for (int i = 0, j = hexPoints.length - 1; i < hexPoints.length; j = i++) {
			shapeRenderer.line(hexPoints[j], hexPoints[i]);
		}
	}

	@Override
	public void dispose () {
		batch.dispose();
		// Dispose other resources
	}
}
