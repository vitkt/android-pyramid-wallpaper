package ru.vitkt.pyramidwallpaper;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import org.jbox2d.callbacks.DebugDraw;
import org.jbox2d.collision.shapes.EdgeShape;
import org.jbox2d.collision.shapes.PolygonShape;
import org.jbox2d.common.IViewportTransform;
import org.jbox2d.common.OBBViewportTransform;
import org.jbox2d.common.Transform;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Body;
import org.jbox2d.dynamics.BodyDef;
import org.jbox2d.dynamics.BodyType;
import org.jbox2d.dynamics.Filter;
import org.jbox2d.dynamics.Fixture;
import org.jbox2d.dynamics.FixtureDef;
import org.jbox2d.dynamics.World;

import ru.vitkt.pyramidwallpaper.FrameStorage.Frame;

import android.app.Activity;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.GestureDetector.OnGestureListener;
import android.view.View;
import android.widget.ImageView;

public class BoxView extends View {
	World world;

	Activity _act;
	AnimalSensor sensor;

	// AndroidDebugDraw dd;
	ArrayList<Body> dynamicBodies;
	ArrayList<Body> staticBodies;

	void addStaticBody(float x, float y, float w, float h) {
		Body groundBody;
		BodyDef groundBodyDef = new BodyDef();
		groundBodyDef.position.set(x, y);

		// Call the body factory which allocates memory for the ground body
		// from a pool and creates the ground box shape (also from a pool).
		// The body is also added to the world.
		groundBody = world.createBody(groundBodyDef);

		// Define the ground box shape.
		PolygonShape groundBox = new PolygonShape();

		// The extents are the half-widths of the box.
		groundBox.setAsBox(w, h);

		FixtureDef fixtureDefGround = new FixtureDef();
		fixtureDefGround.shape = groundBox;

		// Set the box density to be non-zero, so it will be dynamic.
		fixtureDefGround.density = 0.0f;

		// Override the default friction.
		fixtureDefGround.friction = 0.000f;
		fixtureDefGround.restitution = 0.5f;

		// Add the shape to the body.
		// body.createFixture(fixtureDefGround);

		// Add the ground fixture to the ground body.
		groundBody.createFixture(fixtureDefGround);
		staticBodies.add(groundBody);
	}

	void addDynamicBody(float x, float y, float w, float h) {
		Body body;
		BodyDef bodyDef = new BodyDef();

		bodyDef.type = BodyType.DYNAMIC;
		bodyDef.position.set(x, y);
		body = world.createBody(bodyDef);

		// Define another box shape for our dynamic body.
		PolygonShape dynamicBox = new PolygonShape();
		dynamicBox.setAsBox(w, h);

		// Define the dynamic body fixture.
		FixtureDef fixtureDef = new FixtureDef();
		fixtureDef.shape = dynamicBox;

		// fixtureDef.restitution =

		// Set the box density to be non-zero, so it will be dynamic.
		Random r = new Random();

		fixtureDef.density = 20.0f + (r.nextFloat() / 2f);

		// Override the default friction.

		fixtureDef.friction = 0.0f;

		fixtureDef.restitution = 0.5f;

		// Add the shape to the body.
		body.createFixture(fixtureDef);
		dynamicBodies.add(body);
	}

	void create2BoxScene() {

		addStaticBody(0f, 10f, 20f, 10f);
		addStaticBody(0f, 70f, 20f, 10f);

		addStaticBody(0f, -30f, 50f, 1f);

		addStaticBody(0f, 100f, 50f, 1f);

		addStaticBody(0f, 50f, 1f, 40f);

		addStaticBody(50f, 50f, 1f, 80f);

		addStaticBody(-40f, 50f, 1f, 80f);

		addDynamicBody(-5f, 30f, 2f, 2f);

		addDynamicBody(7f, 30f, 2f, 2f);

	}

	void createPyramidScene() {
		addStaticBody(0f, -30f, 50f, 1f);

		addStaticBody(0f, 49f, 50f, 1f);

		addStaticBody(10f, 50f, 1f, 80f);

		addStaticBody(-40f, 50f, 1f, 80f);

		float k = 2.0f;
		{

			float a = .5f * k;
			PolygonShape shape = new PolygonShape();
			shape.setAsBox(a, a);

			Vec2 x = new Vec2(-7.0f * k, -10.75f * k);
			Vec2 y;
			Vec2 deltaX = new Vec2(0.5625f * k, 1.25f * k);
			Vec2 deltaY = new Vec2(1.125f * k, 0.0f * k);
			int e_count = 10;
			for (int i = 0; i < e_count; ++i) {
				y = x.clone();

				for (int j = i; j < e_count; ++j) {
					BodyDef bd = new BodyDef();
					bd.type = BodyType.DYNAMIC;
					bd.position = y;
					Body body = world.createBody(bd);

					FixtureDef fixture = new FixtureDef();
					fixture.shape = shape;
					fixture.density = 0.0f;
					fixture.friction = 0.0f;
					fixture.restitution = 0.5f;

					// body.createFixture(shape, 5.0f);
					body.createFixture(fixture);
					dynamicBodies.add(body);
					y.x += deltaY.x;
					y.y += deltaY.y;
				}

				x.x += deltaX.x;
				x.y += deltaX.y;
			}
		}
	}

	Timer gameTimer;

	void createWorld() {
		Vec2 gravity = new Vec2(0.0f, -10.0f);
		// if(world!=null)

		world = new World(gravity);
		// create2BoxScene();
		createPyramidScene();
	}

	public BoxView(Activity act, AnimalSensor _sensor) {

		super(act);
		dynamicBodies = new ArrayList<Body>();
		staticBodies = new ArrayList<Body>();
		sensor = _sensor;
		// TODO Auto-generated method stub
		p.setColor(Color.BLUE);
		p.setStrokeWidth(5.0f);
		_act = act;

		createWorld();
		// Define the dynamic body. We set its position and call the body
		// factory.
		// TODO Auto-generated constructor stub

		setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (mode == ViewMode.physic)
					mode = ViewMode.timeLapse;
				else {
					// if (FrameStorage.isBegin())
					mode = ViewMode.physic;
				}

			}
		});

		gameTimer = new Timer();

		gameTimer.schedule(tickTask(), 1000 / 60);
		// d.setSeconds(d.getSeconds()+2);

	}

	TimerTask tickTask() {
		return new TimerTask() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				tick();
			}
		};

	}

	Paint p = new Paint();
	

	@Override
	public void draw(Canvas canvas) {
		// TODO Auto-generated method stub
		super.draw(canvas);

		canvas.drawColor(Color.BLACK);
		if (mode == ViewMode.physic) {
			drawPhysics(canvas);
		} else if (mode == ViewMode.timeLapse) {
			drawTimeLapse(canvas);
		}

	}

	private void drawTimeLapse(Canvas canvas) {

		Frame frame = FrameStorage.GetFrame();
		for (ArrayList<float[]> figure : frame.figures) {
			for (float[] line : figure) {
				canvas.drawLine(line[0], line[1], line[2], line[3], p);
			}
		}

	}

	private void drawPhysics(Canvas canvas) {
		FrameStorage.BeginPushFrame();
		for (Body body : dynamicBodies) {
			drawCube(canvas, body);
		}
		for (Body groundBody : staticBodies) {
			drawCube(canvas, groundBody);
		}
		FrameStorage.EndPushFrame();
	}

	private void drawCube(Canvas canvas, Body _body) {
		Transform t = _body.getTransform();
		Fixture f = _body.getFixtureList();
		drawShape(canvas, t, f);

	}

	private void drawShape(Canvas canvas, Transform t, Fixture f) {
		PolygonShape poly = (PolygonShape) f.getShape();
		int vertexCount = poly.m_count;
		Vec2[] vertices = new Vec2[vertexCount];

		// Log.i("pyramid", "DRAW CUBE________________________");
		for (int i = 0; i < vertexCount; ++i) {
			vertices[i] = Transform.mul(t, poly.m_vertices[i]);
			// Log.i("pyramid", "vertex"+i+" ="+vertices[i]);
		}
		// Log.i("pyramid","______________________");
		drawCubeFromBoxToView(canvas, vertices, vertexCount);
		//
		// m_debugDraw->DrawSolidPolygon(vertices, vertexCount, color);

	}

	private void drawCubeFromBoxToView(Canvas canvas, Vec2[] vertices,
			int vertexCount) {
		FrameStorage.BeginAddFigure();
		// TODO Auto-generated method stub
		int width = getWidth();
		int height = getHeight();
		float wk = width / 50f;
		float xoff = 40;
		float yoff = 30;
		for (int i = 0; i < vertexCount - 1; i++) {
			float sx, sy, ex, ey;

			sx = (vertices[i].x + xoff) * wk;
			sy = height - (vertices[i].y + yoff) * wk;

			ex = (vertices[i + 1].x + xoff) * wk;
			ey = height - (vertices[i + 1].y + yoff) * wk;

			canvas.drawLine(sx, sy, ex, ey, p);
			FrameStorage.AddFigureLine(sx, sy, ex, ey);
		}

		float sx, sy, ex, ey;

		sx = (vertices[vertexCount - 1].x + xoff) * wk;
		sy = height - (vertices[vertexCount - 1].y + yoff) * wk;

		ex = (vertices[0].x + xoff) * wk;
		ey = height - (vertices[0].y + yoff) * wk;

		canvas.drawLine(sx, sy, ex, ey, p);
		FrameStorage.AddFigureLine(sx, sy, ex, ey);
		FrameStorage.EndAddFigure();

	}

	boolean updateWorld = true;

	void tick() {
		if (mode == ViewMode.physic) {
			float timeStep = 1.0f / 60.0f;
			int velocityIterations = 6;
			int positionIterations = 2;
			float gravityK = 2.0f;
			world.setGravity(new Vec2(-sensor.getX() * gravityK, -sensor.getY()
					* gravityK));
			world.step(timeStep, velocityIterations, positionIterations);
			gameTimer.schedule(tickTask(), 1000 / 60);
		} else if (mode == ViewMode.timeLapse) {
			FrameStorage.PopFrame();
			if (FrameStorage.isBegin()) {
				// createWorld();
			}
			gameTimer.schedule(tickTask(), 5);
		}
		_act.runOnUiThread(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				invalidate();
			}
		});

	}

	int frameNumber = 0;
	ViewMode mode = ViewMode.physic;

	enum ViewMode {
		physic, timeLapse
	};

}
