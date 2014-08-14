package ru.vitkt.pyramidwallpaper;

import java.util.Calendar;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import org.jbox2d.callbacks.DebugDraw;
import org.jbox2d.collision.shapes.PolygonShape;
import org.jbox2d.common.IViewportTransform;
import org.jbox2d.common.OBBViewportTransform;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Body;
import org.jbox2d.dynamics.BodyDef;
import org.jbox2d.dynamics.BodyType;
import org.jbox2d.dynamics.FixtureDef;
import org.jbox2d.dynamics.World;

import android.app.Activity;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.View;
import android.widget.ImageView;

public class BoxView extends ImageView {
	World world;
	Body body;
Activity _act;
AndroidDebugDraw dd;
	public BoxView(Activity act) {
		
		
		super(act);
		// TODO Auto-generated method stub
p.setColor(Color.RED);
		_act = act;
		Vec2 gravity = new Vec2(0.0f, -10.0f);
		world = new World(gravity);
		//dd = new AndroidDebugDraw()
		OBBViewportTransform obbt = new OBBViewportTransform();
		obbt.setCamera(0f, 0f, 0.5f);
		
		dd = new AndroidDebugDraw(obbt);
		world.setDebugDraw(dd);
		setImageBitmap(dd.mBitmap);
		
		

		BodyDef groundBodyDef = new BodyDef();
		groundBodyDef.position.set(0.0f, -10.0f);

		// Call the body factory which allocates memory for the ground body
		// from a pool and creates the ground box shape (also from a pool).
		// The body is also added to the world.
		Body groundBody = world.createBody(groundBodyDef);

		// Define the ground box shape.
		PolygonShape groundBox = new PolygonShape();

		// The extents are the half-widths of the box.
		groundBox.setAsBox(50.0f, 10.0f);

		// Add the ground fixture to the ground body.
		groundBody.createFixture(groundBox, 0.0f);

		// Define the dynamic body. We set its position and call the body
		// factory.
		BodyDef bodyDef = new BodyDef();

		bodyDef.type = BodyType.DYNAMIC;
		bodyDef.position.set(0.0f, 4.0f);
		body = world.createBody(bodyDef);

		// Define another box shape for our dynamic body.
		PolygonShape dynamicBox = new PolygonShape();
		dynamicBox.setAsBox(1.0f, 1.0f);

		// Define the dynamic body fixture.
		FixtureDef fixtureDef = new FixtureDef();
		fixtureDef.shape = dynamicBox;

		// Set the box density to be non-zero, so it will be dynamic.
		fixtureDef.density = 1.0f;

		// Override the default friction.
		fixtureDef.friction = 0.3f;

		// Add the shape to the body.
		body.createFixture(fixtureDef);
		// TODO Auto-generated constructor stub
		Timer t = new Timer();
		Calendar c = Calendar.getInstance();
Date d;
d = new Date();
//d.setSeconds(d.getSeconds()+2);
		t.schedule(new TimerTask() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				tick();
			}
		}, d,1000/60);
	}

	Paint p = new Paint();
	
//	@Override
//	public void draw(Canvas canvas) {
//		// TODO Auto-generated method stub
//		super.draw(canvas);
//		canvas.drawColor(Color.WHITE);
//		Vec2 position = body.getPosition();
//	PolygonShape ph=(PolygonShape)body.getFixtureList().getShape();
//	//ph.m_vertices
//	//canvas.draw
//		float angle = body.getAngle();
//		p.setStrokeWidth(10);
//		int w = getWidth();
//	canvas.drawPoint(position.x*4, w - position.y*4, p);
//
//	}

	void tick() {
		float timeStep = 1.0f / 60.0f;
		int velocityIterations = 6;
		int positionIterations = 2;
		world.step(timeStep, velocityIterations, positionIterations);
		_act.runOnUiThread(new Runnable() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				invalidate();	
			}
		});
	
	}

}
