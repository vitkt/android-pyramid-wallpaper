package ru.vitkt.pyramidwallpaper;

import java.util.ArrayList;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import org.jbox2d.callbacks.QueryCallback;
import org.jbox2d.collision.AABB;
import org.jbox2d.collision.shapes.PolygonShape;

import org.jbox2d.common.Transform;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Body;
import org.jbox2d.dynamics.BodyDef;
import org.jbox2d.dynamics.BodyType;

import org.jbox2d.dynamics.Fixture;
import org.jbox2d.dynamics.FixtureDef;
import org.jbox2d.dynamics.World;
import org.jbox2d.dynamics.joints.MouseJoint;
import org.jbox2d.dynamics.joints.MouseJointDef;

import ru.vitkt.pyramidwallpaper.FrameStorage.Frame;

import android.app.Activity;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.Log;
import android.view.GestureDetector;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;

public class BoxView extends View {
	World world;

	Activity _act;
	GameSensorManager sensor;
	GestureDetector detector;
	GestureDetector.OnDoubleTapListener onDoubleTap = new GestureDetector.OnDoubleTapListener() {

		@Override
		public boolean onSingleTapConfirmed(MotionEvent e) {
			return false;
		}

		@Override
		public boolean onDoubleTapEvent(MotionEvent e) {
			return false;
		}

		@Override
		public boolean onDoubleTap(MotionEvent e) {
			changeMode();
			return false;
		}
	};

	ArrayList<BoxGuiWrapper> dynamicBodies;
	ArrayList<BoxGuiWrapper> staticBodies;

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

		fixtureDefGround.density = 0.0f;

		fixtureDefGround.friction = 0.000f;
		fixtureDefGround.restitution = 0.5f;

		groundBody.createFixture(fixtureDefGround);
		staticBodies.add(new BoxGuiWrapper(groundBody, Color.BLACK));
	}

	private Random massEps = new Random();

	float getMassEps() {
		return massEps.nextFloat();
	}

	Random colorRandomizer = new Random();

	int getRandomColor() {
		return Color.argb(255, colorRandomizer.nextInt(256),
				colorRandomizer.nextInt(256), colorRandomizer.nextInt(256));
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
		dynamicBodies.add(new BoxGuiWrapper(body, Color.GREEN));
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

	Body groundB;
	MouseJoint mj = null;

	void createPyramidScene() {
		addStaticBody(0f, -30f, 50f, 1f);
		groundB = staticBodies.get(staticBodies.size() - 1).getBody();
		addStaticBody(0f, 49f, 50f, 1f);

		addStaticBody(10f, 50f, 1f, 80f);

		addStaticBody(-40f, 50f, 1f, 80f);

		float k = 2.0f;
		{

			float a = .5f * k;
			PolygonShape shape = new PolygonShape();
			shape.setAsBox(a, a);

			Vec2 x = new Vec2(-14.0f * k, -10.75f * k);
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
					fixture.density = 5.0f;
					fixture.friction = 0.0f;
					fixture.restitution = 0.5f;

					// body.createFixture(shape, 5.0f);
					body.createFixture(fixture);
					dynamicBodies
							.add(new BoxGuiWrapper(body, getRandomColor()));
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
		dynamicBodies.clear();
		staticBodies.clear();
		Vec2 gravity = new Vec2(0.0f, -10.0f);

		world = new World(gravity);

		createPyramidScene();
	}

	public BoxView(Activity act, GameSensorManager _sensor) {

		super(act);
		dynamicBodies = new ArrayList<BoxGuiWrapper>();
		staticBodies = new ArrayList<BoxGuiWrapper>();
		sensor = _sensor;

		p.setColor(Color.BLUE);
		p.setStrokeWidth(10.0f);
		_act = act;

		createWorld();

		detector = new GestureDetector(act,
				new GestureDetector.OnGestureListener() {

					@Override
					public boolean onSingleTapUp(MotionEvent e) {

						return false;
					}

					@Override
					public void onShowPress(MotionEvent e) {
					}

					@Override
					public boolean onScroll(MotionEvent e1, MotionEvent e2,
							float distanceX, float distanceY) {
						return false;
					}

					@Override
					public void onLongPress(MotionEvent e) {
					}

					@Override
					public boolean onFling(MotionEvent e1, MotionEvent e2,
							float velocityX, float velocityY) {

						return false;
					}

					@Override
					public boolean onDown(MotionEvent e) {

						return false;
					}

				});
		detector.setOnDoubleTapListener(onDoubleTap);

		gameTimer = new Timer();
		gameTimer.schedule(tickTask(), 1000 / 60);

	}

	public boolean onTouchEvent(MotionEvent event) {
		detector.onTouchEvent(event);

		if (event.getAction() == MotionEvent.ACTION_DOWN) {

			final Vec2 p = ConvertViewToWorld(event.getX(), event.getY());
			Log.i("pyramid", "down " + p.x + " " + p.y);
			// Make a small box.
			AABB aabb = new AABB();
			Vec2 d = new Vec2();
			d.set(0.001f, 0.001f);
			aabb.lowerBound.x = p.x - d.x;
			aabb.lowerBound.y = p.y - d.y;

			aabb.upperBound.x = p.x + d.x;
			aabb.upperBound.y = p.y + d.y;

			// Query the world for overlapping shapes.
			QueryCallback callback = new QueryCallback() {

				@Override
				public boolean reportFixture(Fixture m_fixture) {

					Log.i("pyramid", "report");

					Body body = m_fixture.getBody();

					MouseJointDef md = new MouseJointDef();

					md.bodyA = groundB;
					md.bodyB = body;
					md.target.set(p);

					md.maxForce = 1000.0f * body.getMass();
					mj = (MouseJoint) world.createJoint(md);
					// body.setAwake(true);

					return false;
				}
			};
			world.queryAABB(callback, aabb);
		} else if (event.getAction() == MotionEvent.ACTION_MOVE) {
			Log.i("pyramid", "move");
			if (mj != null) {
				final Vec2 p = ConvertViewToWorld(event.getX(), event.getY());
				mj.setTarget(p);
			}

		} else if (event.getAction() == MotionEvent.ACTION_UP) {
			Log.i("pyramid", "up");
			if (mj != null)
				world.destroyJoint(mj);
		}

		return true;
	};

	TimerTask tickTask() {
		return new TimerTask() {
			@Override
			public void run() {
				tick();
			}
		};

	}

	Paint p = new Paint();

	@Override
	public void draw(Canvas canvas) {

		super.draw(canvas);

		canvas.drawColor(Color.WHITE);
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
				p.setColor((int) line[4]);
				canvas.drawLine(line[0], line[1], line[2], line[3], p);
			}
		}

	}

	private void drawPhysics(Canvas canvas) {
		FrameStorage.BeginPushFrame();
		for (BoxGuiWrapper boxWrapper : dynamicBodies) {
			drawCube(canvas, boxWrapper);
		}
		for (BoxGuiWrapper groundBixWrapper : staticBodies) {
			drawCube(canvas, groundBixWrapper);
		}
		FrameStorage.EndPushFrame();
	}

	private void drawCube(Canvas canvas, BoxGuiWrapper _box) {
		Transform t = _box.getBody().getTransform();
		Fixture f = _box.getBody().getFixtureList();
		drawShape(canvas, t, f, _box.getColor());

	}

	private void drawShape(Canvas canvas, Transform t, Fixture f, int color) {
		PolygonShape poly = (PolygonShape) f.getShape();
		int vertexCount = poly.m_count;
		Vec2[] vertices = new Vec2[vertexCount];

		for (int i = 0; i < vertexCount; ++i) {
			vertices[i] = Transform.mul(t, poly.m_vertices[i]);

		}

		drawCubeFromBoxToView(canvas, vertices, vertexCount, color);
	}

	float widthK = 50f;
	float xoff = 40;
	float yoff = 30;

	private void drawCubeFromBoxToView(Canvas canvas, Vec2[] vertices,
			int vertexCount, int color) {
		FrameStorage.BeginAddFigure();
		p.setColor(color);
		int width = getWidth();
		int height = getHeight();
		float wk = width / widthK;

		for (int i = 0; i < vertexCount - 1; i++) {
			float sx, sy, ex, ey;

			sx = (vertices[i].x + xoff) * wk;
			sy = height - (vertices[i].y + yoff) * wk;

			ex = (vertices[i + 1].x + xoff) * wk;
			ey = height - (vertices[i + 1].y + yoff) * wk;

			canvas.drawLine(sx, sy, ex, ey, p);
			FrameStorage.AddFigureLine(sx, sy, ex, ey, color);
		}

		float sx, sy, ex, ey;

		sx = (vertices[vertexCount - 1].x + xoff) * wk;
		sy = height - (vertices[vertexCount - 1].y + yoff) * wk;

		ex = (vertices[0].x + xoff) * wk;
		ey = height - (vertices[0].y + yoff) * wk;

		canvas.drawLine(sx, sy, ex, ey, p);
		FrameStorage.AddFigureLine(sx, sy, ex, ey, color);
		FrameStorage.EndAddFigure();

	}

	private Vec2 ConvertViewToWorld(float x, float y) {
		float wk = getWidth() / widthK;
		// sx = (vertices[i].x + xoff) * wk;
		// sy = height - (vertices[i].y + yoff) * wk;
		return new Vec2((x / wk) - xoff, ((y - getHeight()) / (-wk)) - yoff);
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
				createWorld();
				mode = ViewMode.physic;
			}
			gameTimer.schedule(tickTask(), 5);
		}
		_act.runOnUiThread(new Runnable() {

			@Override
			public void run() {

				invalidate();
			}
		});

	}

	private void changeMode() {
		if (mode == ViewMode.physic)
			mode = ViewMode.timeLapse;
	}

	int frameNumber = 0;
	ViewMode mode = ViewMode.physic;

	enum ViewMode {
		physic, timeLapse
	};

}
