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
	private static final float recedeFactor = 0.65f; //CAN BE NO GREATER THAN 0.75

	private static final int worldWidth = 8;
	private static final int worldHeight = 14;
	private static final int hexDensity = 6;

	private static int rightEdge;
	private static int leftEdge;
	private static int topEdge;
	private static int bottomEdge;


	private HexMap hexMap;


	@Override
	public void create() {
		batch = new PolygonSpriteBatch();
		shapeRenderer = new ShapeRenderer();

		height = Gdx.graphics.getHeight();
		width = Gdx.graphics.getWidth();

		hexMap = new HexMap(worldWidth, worldHeight, hexDensity, width);

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
		float modifiedMoveSpeed = moveSpeed / (float) Math.sqrt(zoom);
		//If screen is clicked, log screencenter, movespeed and zoom
		if (Gdx.input.justTouched()) {
			processClick();
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
			if (screenCenter.x > rightEdge)	{
				screenCenter.x = rightEdge;
			}
		} else if(Gdx.input.isKeyPressed(Input.Keys.A)) {
			screenCenter.x -= modifiedMoveSpeed;
			if (screenCenter.x < leftEdge) {
				screenCenter.x = leftEdge;
			}
		}
		if(Gdx.input.isKeyPressed(Input.Keys.W)) {
			screenCenter.y += modifiedMoveSpeed;
			if (screenCenter.y > topEdge) {
				screenCenter.y = topEdge;
			}
		} else if(Gdx.input.isKeyPressed(Input.Keys.S)) {
			screenCenter.y -= modifiedMoveSpeed;
			if (screenCenter.y < bottomEdge) {
				screenCenter.y = bottomEdge;
			}
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

		hexMap.renderMap(batch, width, height, recedeFactor, screenCenter, zoom);

		batch.end();
	}


	@Override
	public void dispose () {
		batch.dispose();
		shapeRenderer.dispose();
		// Dispose other resources
	}

	private void processClick() {
		int x = Gdx.input.getX();
		int y = (Gdx.input.getY() - height) * -1;
		Vector2 point = new Vector2(x, y);
		point = DisplayFunctions.reverseTransformation(point, width, height, recedeFactor, screenCenter, zoom);
		Maybe<Hexagon<SatelliteData>> hexMaybe = hexMap.getGrid().getByPixelCoordinate(point.x, point.y);
		//Manually check if point is in hexagon
		if (hexMaybe.isPresent()) {
			Hexagon<SatelliteData> hex = hexMaybe.get();
			if (isPointInHexagon(point, hex, ((double) width / hexDensity) / Math.sqrt(3))) {
				CustomSatelliteData hexData = (CustomSatelliteData) hex.getSatelliteData().get();
				hexData.setColor(new Color(MathUtils.random(), MathUtils.random(), MathUtils.random(), 1));
			}
		}
		Gdx.app.log("Point: ", String.valueOf(point));
	}

	public static boolean isPointInHexagon(Vector2 point, Hexagon<SatelliteData> hex, double hexRadius) {
		//Divide hexagon into 6 triangles, and check if point is in any of them
		Vector2 center = new Vector2((float) hex.getCenterX(), (float) hex.getCenterY());
		for (int i = 0; i < 6; i++) {
			Vector2 point1 = new Vector2((float) (hex.getCenterX() + hexRadius * Math.cos(i * Math.PI / 3)), (float) (hex.getCenterY() + hexRadius * Math.sin(i * Math.PI / 3)));
			Vector2 point2 = new Vector2((float) (hex.getCenterX() + hexRadius * Math.cos((i + 1) * Math.PI / 3)), (float) (hex.getCenterY() + hexRadius * Math.sin((i + 1) * Math.PI / 3)));
			if (isPointInTriangle(point, center, point1, point2)) {
				return true;
			}
		}
		return false;
	}

	private static boolean isPointInTriangle(Vector2 checkPoint, Vector2 point1, Vector2 point2, Vector2 point3) {
		//Check if point is in triangle
		float d1, d2, d3;
		boolean has_neg, has_pos;

		d1 = sign(checkPoint, point1, point2);
		d2 = sign(checkPoint, point2, point3);
		d3 = sign(checkPoint, point3, point1);

		has_neg = (d1 < 0) || (d2 < 0) || (d3 < 0);
		has_pos = (d1 > 0) || (d2 > 0) || (d3 > 0);

		return !(has_neg && has_pos);
	}

	private static float sign(Vector2 p1, Vector2 p2, Vector2 p3) {
		return ((p1.x - p3.x) * (p2.y - p3.y) - (p2.x - p3.x) * (p1.y - p3.y));
	}
}
