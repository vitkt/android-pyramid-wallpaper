package ru.vitkt.confettiwallpaper;

import java.util.ArrayList;
import java.util.Random;

import org.jbox2d.callbacks.QueryCallback;
import org.jbox2d.collision.AABB;
import org.jbox2d.collision.shapes.PolygonShape;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Body;
import org.jbox2d.dynamics.BodyDef;
import org.jbox2d.dynamics.BodyType;
import org.jbox2d.dynamics.Fixture;
import org.jbox2d.dynamics.FixtureDef;
import org.jbox2d.dynamics.World;
import org.jbox2d.dynamics.joints.MouseJoint;
import org.jbox2d.dynamics.joints.MouseJointDef;

import android.graphics.Color;
import android.util.Log;

public class WallpaperWorld {
	ArrayList<BoxGuiWrapper> dynamicBodies;
	ArrayList<BoxGuiWrapper> staticBodies;

	Random colorRandomizer = new Random();

	int getRandomColor() {
		return Color.argb(255, colorRandomizer.nextInt(256),
				colorRandomizer.nextInt(256), colorRandomizer.nextInt(256));
	}

	World world;
	MouseJoint mj = null;

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

	void createPyramidScene(int sceneWidth, int sceneHeigth) {

		if (sceneWidth > sceneHeigth) {
			worldHeight = 50f;
			worldWidth = ((float) sceneWidth / sceneHeigth) * worldHeight;
		} else {
			worldWidth = 50f;
			worldHeight = ((float) sceneHeigth / sceneWidth) * worldWidth;
		}

		addStaticBody(worldWidth / 2, 0f, worldWidth / 2, 1f);
		groundB = staticBodies.get(staticBodies.size() - 1).getBody();

		addStaticBody(worldWidth / 2, worldHeight, worldWidth / 2, 2f);

		addStaticBody(0f, worldHeight / 2, 1f, worldHeight / 2);

		addStaticBody(worldWidth, worldHeight / 2, 1f, worldHeight / 2);

		float k = 2.0f;
		{

			float a = .5f * k;
			PolygonShape shape = new PolygonShape();
			shape.setAsBox(a, a);

			Vec2 x = new Vec2(10 * k, 10.75f * k);// new Vec2(-14.0f * k,
													// -10.75f * k);
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
					body.setLinearVelocity(getRandomVelocity());
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

	Random velocityRandomizer = new Random();

	private Vec2 getRandomVelocity() {
		final float velocityRange = -50f;
		return new Vec2(2 * velocityRange * velocityRandomizer.nextFloat()
				- velocityRange, 2 * velocityRange
				* velocityRandomizer.nextFloat() - velocityRange);
	}

	void createWorld(int sceneWidth, int sceneHeight) {

		Vec2 gravity = new Vec2(0.0f, -10.0f);
		if (world != null) {
			for (BoxGuiWrapper box : dynamicBodies)
				world.destroyBody(box.getBody());
			for (BoxGuiWrapper box : staticBodies)
				world.destroyBody(box.getBody());
			if (mj != null) {
				world.destroyJoint(mj);
				Log.d("pyramid", "AAAAAAAAAAAA");
				mj = null;
			}
		}
		dynamicBodies.clear();
		staticBodies.clear();
		world = new World(gravity);
		Log.d("pyramid", "world created!");
		createPyramidScene(sceneWidth, sceneHeight);
	}

	public WallpaperWorld(int sceneWidth, int sceneHeight) {
		dynamicBodies = new ArrayList<BoxGuiWrapper>();
		staticBodies = new ArrayList<BoxGuiWrapper>();

		createWorld(sceneWidth, sceneHeight);
	}

	public void nextStep() {
		float timeStep = 1.0f / 60.0f;
		int velocityIterations = 6;
		int positionIterations = 2;

		world.step(timeStep, velocityIterations, positionIterations);
	}

	Body groundB;

	public void setGravity(float x, float y) {
		world.setGravity(new Vec2(x, y));
	}

	void onMouseDown(float x, float y) {
		final Vec2 p = new Vec2(x, y);

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
				Log.d("pyramid", "joint created!");
				return false;
			}
		};
		world.queryAABB(callback, aabb);

	}

	void onMouseMove(float x, float y) {
		Vec2 p = new Vec2(x, y);
		if (mj != null) {
			mj.setTarget(p);
		}

	}

	void onMouseUp(float x, float y) {
		if (mj != null) {
			world.destroyJoint(mj);
			mj = null;
			Log.d("pyramid", "joint destroed!");
		}

	}

	private float worldWidth = 50f;
	private float worldHeight = 50f;

	public float getWorldWidth() {
		return worldWidth;
	}

	public void moveAllToPoint(Vec2 point) {
		for (BoxGuiWrapper wrapper : dynamicBodies) {
			Body body = wrapper.getBody();
			Vec2 diff = new Vec2();
			diff.x = point.x - body.getPosition().x;
			diff.y = point.y - body.getPosition().y;
			diff.x *= -15;
			diff.y *= -15;
			body.setLinearVelocity(diff);
		}

	}

}
