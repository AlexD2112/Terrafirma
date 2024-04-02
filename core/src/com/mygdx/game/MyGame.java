package com.mygdx.game;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.PolygonSpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.mygdx.game.Maps.StrategicMap;
import com.mygdx.game.Maps.TacticalMap;
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
	private static final float maxZoom = 4.4f;
	private static final float minZoom = 0.6F;

	private static final float moveSpeed = 5f;
	private static final float zoomSpeed = 0.03f;
	private static final float recedeFactor = 0.6f; //CAN BE NO GREATER THAN 0.75

	private static final int worldWidth = 28;
	private static final int worldHeight = 13;
	private static final int hexDensity = 6;
	private static final float cloudBegin = 0.66f;
	private static final int tacticalCloudShift = 4; //Higher numbers speed up zoom in on tactical mode, allowing a slightly more zoomed out tactical without cloud effect

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
			for (Point p : hexagon.getPoints()) {
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
		}
		edges = new int[]{rightEdge, leftEdge, topEdge, bottomEdge};

		mapScalarPoint = new Vector2(width / 2f, height / 2f);
		strategicMap.init((float) width /hexDensity);

		userEvents = new UserEvents(zoomSpeed, moveSpeed, maxZoom, minZoom, edges, grid);
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


		if (zoom < maxZoom) { //CODE FOR ZOOMED OUT- STRATEGIC MAP
			if (zoomed) { //If we just zoomed out, we need to reinitialize the strategic map
				zoomed = false;
				strategicMap.init((float) width /hexDensity);
			}
			if (zoom > cloudBegin * maxZoom) { //If we are in the cloud zone, we need to adjust the cloud factor
				strategicMap.setCloud((float) ((zoom - cloudBegin * maxZoom) / (maxZoom - cloudBegin * maxZoom)));
			} else {
				strategicMap.setCloud(0);
			}
			strategicMap.render();
		} else { //CODE FOR ZOOMED IN- TACTICAL MAP
			if (!zoomed) { //If we just zoomed in, we need to reinitialize the tactical map
				zoomed = true;
				tacticalMap.init((float) width /hexDensity);
			}
			float adjustedCloudFactor = 1 + (cloudBegin / tacticalCloudShift); //Don't want to make it so hard to zoom out in tactical mode
			if (zoom < maxZoom + adjustedCloudFactor * maxZoom) { //If we are in the cloud zone, we need to adjust the cloud factor
				tacticalMap.setCloud((float) (zoom - adjustedCloudFactor * maxZoom) / (maxZoom - adjustedCloudFactor * maxZoom));
			} else {
				tacticalMap.setCloud(0);
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
