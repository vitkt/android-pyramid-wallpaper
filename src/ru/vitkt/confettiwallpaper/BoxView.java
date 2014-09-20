package ru.vitkt.confettiwallpaper;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import org.jbox2d.collision.shapes.PolygonShape;
import org.jbox2d.common.Transform;
import org.jbox2d.common.Vec2;

import org.jbox2d.dynamics.Fixture;


import ru.vitkt.confettiwallpaper.FrameStorage.Frame;

import android.app.Activity;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.Log;
import android.view.GestureDetector;

import android.view.MotionEvent;
import android.view.View;

public class BoxView extends View {

	Activity _act;
	GameSensorManager sensor;
	GestureDetector detector;
	FrameStorage storage = new FrameStorage();
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

	WallpaperWorld wallpaperWorld;

	

	Timer gameTimer;
@Override
protected void onSizeChanged(int w, int h, int oldw, int oldh) {
	// TODO Auto-generated method stub
	super.onSizeChanged(w, h, oldw, oldh);
	scene.setWidth(w);
	scene.setHeight(h);
}
	public BoxView(Activity act, GameSensorManager _sensor) {

		super(act);
		sensor = _sensor;

		
		_act = act;

		wallpaperWorld = new WallpaperWorld(getWidth(),getHeight());

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

			final Vec2 p = scene.ConvertViewToWorld(event.getX(), event.getY());
			wallpaperWorld.onMouseDown(p.x, p.y);

		} else if (event.getAction() == MotionEvent.ACTION_MOVE) {

			final Vec2 p = scene.ConvertViewToWorld(event.getX(), event.getY());
			wallpaperWorld.onMouseMove(p.x, p.y);

		} else if (event.getAction() == MotionEvent.ACTION_UP) {
			Log.i("pyramid", "up");
			final Vec2 p = scene.ConvertViewToWorld(event.getX(), event.getY());
			wallpaperWorld.onMouseUp(p.x, p.y);
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

	

	@Override
	public void draw(Canvas canvas) {

		super.draw(canvas);

		canvas.drawColor(Color.WHITE);
		if (mode == WallpaperViewMode.physic) {
			drawPhysics(canvas);
		} else if (mode == WallpaperViewMode.timeLapse) {
			scene.drawTimeLapse(canvas);
		}

	}


	WallpaperScene scene = new WallpaperScene(storage);
	private void drawPhysics(Canvas canvas) {
		storage.BeginPushFrame();
		for (BoxGuiWrapper boxWrapper : wallpaperWorld.dynamicBodies) {
			scene.drawCube(canvas, boxWrapper,false);
		}
		for (BoxGuiWrapper groundBixWrapper : wallpaperWorld.staticBodies) {
			scene.drawCube(canvas, groundBixWrapper,true);
		}
		storage.EndPushFrame();
	}






	boolean updateWorld = true;

	void tick() {
		if (mode == WallpaperViewMode.physic) {

			float gravityK = 2.0f;
			wallpaperWorld.setGravity(-sensor.getX() * gravityK, -sensor.getY()
					* gravityK);

			wallpaperWorld.nextStep();
			gameTimer.schedule(tickTask(), 1000 / 60);
		} else if (mode == WallpaperViewMode.timeLapse) {
			storage.PopFrame();
			if (storage.isBegin()) {
				wallpaperWorld.createWorld(getWidth(), getHeight());
				mode = WallpaperViewMode.physic;
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
		if (mode == WallpaperViewMode.physic)
			mode = WallpaperViewMode.timeLapse;
	}

	int frameNumber = 0;
	WallpaperViewMode mode = WallpaperViewMode.physic;

	

}
