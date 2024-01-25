package com.mygdx.game;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.PolygonSpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import org.hexworks.mixite.core.api.Point;
import org.hexworks.mixite.core.api.contract.SatelliteData;
import org.hexworks.mixite.core.api.Hexagon;
import com.badlogic.gdx.graphics.Color;
import org.hexworks.mixite.core.vendor.Maybe;

public class MyGame extends ApplicationAdapter {
	private ShapeRenderer shapeRenderer;
	PolygonSpriteBatch batch;
	// ... other variables like ShapeRenderer
	private int height;
	private int width;
	private Vector2 screenCenter;
	private double zoom = 1;
	private static final double maxZoom = 2;
	private static final double minZoom = 0.6;

	private static final float moveSpeed = 20f;
	private static final float zoomSpeed = 0.03f;
	private static final float recedeFactor = 0.6f; //CAN BE NO GREATER THAN 0.75

	private static final int worldWidth = 28;
	private static final int worldHeight = 13;
	private static final int hexDensity = 6;

	private static int rightEdge;
	private static int leftEdge;
	private static int topEdge;
	private static int bottomEdge;

	private Vector2[] vectors;

	private HexMap hexMap;
	private ZoomMap zoomMap;


	@Override
	public void create() {
		batch = new PolygonSpriteBatch();
		shapeRenderer = new ShapeRenderer();

		height = Gdx.graphics.getHeight();
		width = Gdx.graphics.getWidth();

		hexMap = new HexMap(worldWidth, worldHeight, hexDensity, width);
		zoomMap = new ZoomMap();

		//Get edges from a hexMap method
		int[] edges = hexMap.getEdges();
		rightEdge = edges[0];
		leftEdge = edges[1];
		topEdge = edges[2];
		bottomEdge = edges[3];

		screenCenter = new Vector2(width / 2f, height / 2f);
	}

	@Override
	public void render () {
		zoom = UserEvents.checkInput(width, height, recedeFactor, screenCenter, zoom, hexMap, hexDensity, moveSpeed, minZoom, maxZoom, zoomSpeed, rightEdge, leftEdge, topEdge, bottomEdge);

		//Make color white
		Gdx.gl.glClearColor(1, 1, 1, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);


		if (zoom < maxZoom*2) {
			batch.begin();
			hexMap.renderMap(batch, width, height, (float) recedeFactor, screenCenter, zoom, maxZoom);
			batch.end();
		} else {
			zoomMap.renderZoom(width, height, recedeFactor, screenCenter, zoom, maxZoom, hexMap);
		}
	}


	@Override
	public void dispose () {
		batch.dispose();
		shapeRenderer.dispose();
		zoomMap.dispose();
		// Dispose other resources
	}
}
