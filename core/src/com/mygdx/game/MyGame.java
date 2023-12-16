package com.mygdx.game;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Graphics;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.sun.crypto.provider.BlowfishKeyGenerator;
import org.hexworks.mixite.core.api.HexagonOrientation;
import org.hexworks.mixite.core.api.HexagonalGrid;
import org.hexworks.mixite.core.api.HexagonalGridBuilder;
import org.hexworks.mixite.core.api.HexagonalGridLayout;
import org.hexworks.mixite.core.api.contract.SatelliteData;
import org.hexworks.mixite.core.api.Hexagon;
import org.hexworks.mixite.core.api.Point;
import com.badlogic.gdx.graphics.Color;

import java.util.List;

public class MyGame extends ApplicationAdapter {
	private ShapeRenderer shapeRenderer;
	SpriteBatch batch;
	// ... other variables like ShapeRenderer
	private int height;
	private int width;
	private Vector2 screenCenter;
	private double zoom = 1;
	private static final double maxZoom = 2;
	private static final double minZoom = 0.6;

	private static final float moveSpeed = 20f;
	private static final float zoomSpeed = 0.03f;
	private static final float recedeFactor = 0.65f; //CAN BE NO GREATER THAN 0.75

	private static final int worldWidth = 8;
	private static final int worldHeight = 14;

	private HexMap hexMap;


	@Override
	public void create() {
		batch = new SpriteBatch();
		shapeRenderer = new ShapeRenderer();

		height = Gdx.graphics.getHeight();
		width = Gdx.graphics.getWidth();

		hexMap = new HexMap(worldWidth, worldHeight, width);

		screenCenter = new Vector2(width / 2f, height / 2f);
	}

	@Override
	public void render () {
		float modifiedMoveSpeed = moveSpeed / (float) zoom;
		//If screen is clicked, log screencenter, movespeed and zoom
		if (Gdx.input.isTouched()) {
			System.out.println("Screen center: " + screenCenter + " Move speed: " + modifiedMoveSpeed + " Zoom: " + zoom);
		}
		if (Gdx.input.isKeyPressed(Input.Keys.ESCAPE)) {
			Gdx.app.exit();
		}
		if (Gdx.input.isKeyPressed(Input.Keys.SHIFT_LEFT) || Gdx.input.isKeyPressed(Input.Keys.SHIFT_RIGHT)) {
			modifiedMoveSpeed *= 3;
		}
		modifiedMoveSpeed /= (float) zoom;
		if(Gdx.input.isKeyPressed(Input.Keys.D)) {
			screenCenter.x += modifiedMoveSpeed;
		} else if(Gdx.input.isKeyPressed(Input.Keys.A)) {
			screenCenter.x -= modifiedMoveSpeed;
		}
		if(Gdx.input.isKeyPressed(Input.Keys.W)) {
			screenCenter.y += modifiedMoveSpeed;
		} else if(Gdx.input.isKeyPressed(Input.Keys.S)) {
			screenCenter.y -= modifiedMoveSpeed;
		}
		if(Gdx.input.isKeyPressed(Input.Keys.Q)) {
			zoom += zoomSpeed;
			if (zoom > maxZoom) {
				zoom = maxZoom;
			}
		} else if(Gdx.input.isKeyPressed(Input.Keys.E)) {
			zoom -= zoomSpeed;
			if (zoom < minZoom) {
				zoom = minZoom;
			}
		}

		Gdx.gl.glClearColor(1, 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		batch.begin();

		hexMap.renderMap(shapeRenderer, width, height, recedeFactor, screenCenter, zoom);

		batch.end();
	}


	@Override
	public void dispose () {
		batch.dispose();
		shapeRenderer.dispose();
		// Dispose other resources
	}
}
