package com.mygdx.game;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.PolygonSpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector3;
import org.hexworks.mixite.core.api.*;
import org.hexworks.mixite.core.api.contract.SatelliteData;
import com.badlogic.gdx.graphics.Color;

public class MyGame extends ApplicationAdapter {
	private ShapeRenderer shapeRenderer;
	PolygonSpriteBatch batch;
	// ... other variables like ShapeRenderer
	private int height;
	private int width;
	private Vector2 mapScalarPoint;
	private double zoom = 1;
	private static final float maxZoom = 2;
	private static final float minZoom = 0.6F;

	private static final float moveSpeed = 5f;
	private static final float zoomSpeed = 0.03f;
	private static final float recedeFactor = 0.6f; //CAN BE NO GREATER THAN 0.75

	private static final int worldWidth = 28;
	private static final int worldHeight = 13;
	private static final int hexDensity = 6;

	private static int rightEdge;
	private static int leftEdge;
	private static int topEdge;
	private static int bottomEdge;
	private static int[] edges;

	private Vector2[] vectors;

	private StrategicMap strategicMap;
	private TacticalMap tacticalMap;
	private UserEvents userEvents;

	private HexagonalGrid<SatelliteData> grid;
	boolean zoomed = false;


	@Override
	public void create() {
		batch = new PolygonSpriteBatch();
		shapeRenderer = new ShapeRenderer();

		height = Gdx.graphics.getHeight();
		width = Gdx.graphics.getWidth();

		HexagonalGridBuilder<SatelliteData> builder = new HexagonalGridBuilder<>()
				.setGridHeight(worldHeight)
				.setGridWidth(worldWidth)
				.setGridLayout(HexagonalGridLayout.RECTANGULAR)
				.setOrientation(HexagonOrientation.POINTY_TOP)
				.setRadius(((double) width / hexDensity) / Math.sqrt(3)); //Radius is the distance from the center to corner

		grid = builder.build();

		for (Hexagon<SatelliteData> hexagon : grid.getHexagons()) {
			hexagon.setSatelliteData(new CustomSatelliteData(new Color(MathUtils.random(), MathUtils.random(), MathUtils.random(), 1)));
		}

		strategicMap = new StrategicMap(grid);
		tacticalMap = new TacticalMap(grid);

		//Initialize all edges to 0
		rightEdge = 0;
		leftEdge = 0;
		topEdge = 0;
		bottomEdge = 0;

		for (Hexagon<SatelliteData> hexagon : grid.getHexagons()) {
			Point p = hexagon.getPoints().get(0);
			if (p.getCoordinateX() > rightEdge) {
				rightEdge = (int) p.getCoordinateX();
			}
			if (p.getCoordinateX() < leftEdge) {
				leftEdge = (int) p.getCoordinateX();
			}
			if (p.getCoordinateY() > topEdge) {
				topEdge = (int) p.getCoordinateY();
			}
			if (p.getCoordinateY() < bottomEdge) {
				bottomEdge = (int) p.getCoordinateY();
			}
		}
		edges = new int[]{rightEdge, leftEdge, topEdge, bottomEdge};

		mapScalarPoint = new Vector2(width / 2f, height / 2f);
		strategicMap.init((float) width /hexDensity);

		userEvents = new UserEvents(zoomSpeed, moveSpeed, maxZoom, minZoom, edges);
		Gdx.input.setInputProcessor(userEvents);

		DisplayFunctions.initDisplayFunctions(grid, width, height, hexDensity);
	}

	@Override
	public void render () {
		UserEvents.handleInputs();
		zoom = UserEvents.zoom;

		//Make color white
		Gdx.gl.glClearColor(1, 1, 1, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);


		if (zoom < maxZoom*2) {
			if (zoomed) {
				zoomed = false;
				strategicMap.init((float) width /hexDensity);
			}
			strategicMap.render();
		} else {
			if (!zoomed) {
				zoomed = true;
				tacticalMap.init((float) width /hexDensity);
			}
			tacticalMap.render();
		}
	}


	@Override
	public void dispose () {
		batch.dispose();
		shapeRenderer.dispose();
		tacticalMap.dispose();
		strategicMap.dispose();
		// Dispose other resources
	}
}
