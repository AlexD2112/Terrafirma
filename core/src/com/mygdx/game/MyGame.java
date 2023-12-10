package com.mygdx.game;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Graphics;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.sun.crypto.provider.BlowfishKeyGenerator;
import org.hexworks.mixite.core.api.HexagonOrientation;
import org.hexworks.mixite.core.api.HexagonalGrid;
import org.hexworks.mixite.core.api.HexagonalGridBuilder;
import org.hexworks.mixite.core.api.HexagonalGridLayout;
import org.hexworks.mixite.core.api.contract.SatelliteData;
import org.hexworks.mixite.core.api.defaults.DefaultSatelliteData;
import org.hexworks.mixite.core.api.Hexagon;
import org.hexworks.mixite.core.api.Point;

import java.util.List;

public class MyGame extends ApplicationAdapter {
	private ShapeRenderer shapeRenderer;
	SpriteBatch batch;
	HexagonalGridBuilder<SatelliteData> builder;
	HexagonalGrid<SatelliteData> grid;
	// ... other variables like ShapeRenderer

	private int height;
	private int width;
	private Vector2 screenCenter;

	private static final float moveSpeed = 10f;
	private static final float recedeFactor = 0.65f; //CAN BE NO GREATER THAN 0.75

	@Override
	public void create () {
		batch = new SpriteBatch();
		shapeRenderer = new ShapeRenderer();

		height = Gdx.graphics.getHeight();
		width = Gdx.graphics.getWidth();

		builder = new HexagonalGridBuilder<>()
				.setGridHeight(14)
				.setGridWidth(8)
				.setGridLayout(HexagonalGridLayout.RECTANGULAR)
				.setOrientation(HexagonOrientation.POINTY_TOP)
				.setRadius(((double) width / 8) / Math.sqrt(3)); //Radius is the distance from the center to corner
		grid = builder.build();
		// ... other initializations

		screenCenter = new Vector2(width / 2f, height / 2f);
	}

	@Override
	public void render () {
		if(Gdx.input.isKeyPressed(Input.Keys.D)) {
			screenCenter.x += moveSpeed;
		} else if(Gdx.input.isKeyPressed(Input.Keys.A)) {
			screenCenter.x -= moveSpeed;
		}
		if(Gdx.input.isKeyPressed(Input.Keys.W)) {
			screenCenter.y += moveSpeed;
		} else if(Gdx.input.isKeyPressed(Input.Keys.S)) {
			screenCenter.y -= moveSpeed;
		}

		Gdx.gl.glClearColor(1, 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		batch.begin();

		shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
		shapeRenderer.setColor(1, 1, 1, 1); // White color for the hexagons

		int m = 0;
		for (Hexagon<SatelliteData> hexagon : grid.getHexagons()) {
			List<Point> hexPoints = hexagon.getPoints();
			int numPoints = hexPoints.size();
			Vector2[] points = new Vector2[numPoints];

			for (int i = 0; i < numPoints; i++) {
				Point p = hexPoints.get(i);
				Vector2 transformedPoint = transformPoint(new Vector2((float) p.getCoordinateX(), (float) p.getCoordinateY()));
				//Vector2 transformedPoint = new Vector2((float) p.getCoordinateX() - screenCenter.x, (float) p.getCoordinateY() + ((float) height / 2) - screenCenter.y);
				//Just send in the plain point
				points[i] = transformedPoint;
			}

			for (int i = 0; i < points.length; i++) {
				Vector2 p1 = points[i];
				Vector2 p2 = points[(i + 1) % points.length];
				shapeRenderer.line(p1.x, p1.y, p2.x, p2.y);
			}
			m++;
		}

		shapeRenderer.end();

		batch.end();
	}

	private Vector2 transformPoint(Vector2 point) {
		point.x -= screenCenter.x;
		point.y += ((float) height / 2) - screenCenter.y;

		double yFactor = point.y / height;

		//Scaling factor x = 1 - recedeFactor * log (yFactor + 1)
		double scaleFactorX = 1 - recedeFactor * Math.log(yFactor + 1);

		//Integral of scaleFactorX
		double scaleFactorY = (1 + recedeFactor) * yFactor - recedeFactor * yFactor * Math.log(yFactor + 1) - recedeFactor * Math.log(yFactor + 1);
		//Scale factor but slowed down
		//Transformed x is the x value of the point, minus the x value of the screen center, times the scale factor, plus the x value of the screen center
		double transformedY = scaleFactorY * height;
		double transformedX = scaleFactorX * point.x + (width / 2f);

		return new Vector2((float) transformedX, (float) transformedY);
	}


	@Override
	public void dispose () {
		batch.dispose();
		shapeRenderer.dispose();
		// Dispose other resources
	}
}
