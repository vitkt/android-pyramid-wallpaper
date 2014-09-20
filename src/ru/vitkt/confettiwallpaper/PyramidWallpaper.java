package ru.vitkt.confettiwallpaper;

import org.jbox2d.common.Vec2;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.os.Handler;
import android.service.wallpaper.WallpaperService;
import android.util.Log;
import android.view.Display;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.WindowManager;
import android.widget.Toast;

public class PyramidWallpaper extends WallpaperService {

	@Override
	public Engine onCreateEngine() {

		return new PyramidWallpaperEngine();
	}

	public class PyramidWallpaperEngine extends WallpaperService.Engine {

		private final Handler handler = new Handler();
		private final GameSensorManager sensor;
		private GestureDetector detector;
		private FrameStorage storage;
		private GestureDetector.OnGestureListener gestureListener = new GestureDetector.OnGestureListener() {

			@Override
			public boolean onSingleTapUp(MotionEvent e) {
				// TODO Auto-generated method stub
				if (mode == WallpaperViewMode.physic) {
					Vec2 p = wallpaperScene.ConvertViewToWorld(e.getX(),
							e.getY());
					wallpaperWorld.moveAllToPoint(p);
				}
				return false;
			}

			@Override
			public void onShowPress(MotionEvent e) {
				// TODO Auto-generated method stub

			}

			@Override
			public boolean onScroll(MotionEvent e1, MotionEvent e2,
					float distanceX, float distanceY) {
				// TODO Auto-generated method stub
				return false;
			}

			@Override
			public void onLongPress(MotionEvent e) {
				// TODO Auto-generated method stub

			}

			@Override
			public boolean onFling(MotionEvent e1, MotionEvent e2,
					float velocityX, float velocityY) {
				// TODO Auto-generated method stub
				return false;
			}

			@Override
			public boolean onDown(MotionEvent e) {
				// TODO Auto-generated method stub
				return false;
			}
		};
		private final Runnable drawRunner = new Runnable() {
			@Override
			public void run() {
				draw();
			}

		};

		public PyramidWallpaperEngine() {
			detector = new GestureDetector(PyramidWallpaper.this,
					gestureListener);
			detector.setOnDoubleTapListener(new GestureDetector.OnDoubleTapListener() {

				@Override
				public boolean onSingleTapConfirmed(MotionEvent e) {
					// TODO Auto-generated method stub
					return false;
				}

				@Override
				public boolean onDoubleTapEvent(MotionEvent e) {
					// TODO Auto-generated method stub
					return false;
				}

				@Override
				public boolean onDoubleTap(MotionEvent e) {
					// TODO Auto-generated method stub
					changeViewMode();
					// Vec2 p = wallpaperScene.ConvertViewToWorld(e.getX(),
					// e.getY());
					// wallpaperWorld.moveAllToPoint(p);
					return false;
				}
			});
			sensor = new GameSensorManager(PyramidWallpaper.this);
			sensor.onResume();
			wManager = ((WindowManager) getSystemService(Context.WINDOW_SERVICE));
		}

		private boolean visible;
		WallpaperScene wallpaperScene;
		WallpaperWorld wallpaperWorld;

		WallpaperViewMode mode = WallpaperViewMode.physic;

		@Override
		public void onSurfaceCreated(SurfaceHolder holder) {
			super.onSurfaceCreated(holder);

		}

		int oldWidth = -1, oldHeight = -1;
		WindowManager wManager;

		@Override
		public void onSurfaceChanged(SurfaceHolder holder, int format,
				int width, int height) {

			Log.i("pyramid", "change");
			// Toast.makeText(PyramidWallpaper.this, "Rot = "+getRotation(),
			// Toast.LENGTH_LONG).show();
			super.onSurfaceChanged(holder, format, width, height);
			if (wallpaperWorld == null)
				wallpaperWorld = new WallpaperWorld(width, height);
			else
				wallpaperWorld.createWorld(width, height);
			mode = WallpaperViewMode.physic;
			storage = new FrameStorage();
			wallpaperScene = new WallpaperScene(storage);
			wallpaperScene.setWidth(width);
			wallpaperScene.setHeight(height);
			wallpaperScene.setWorldWidth(wallpaperWorld.getWorldWidth());
		}

		@Override
		public void onSurfaceDestroyed(SurfaceHolder holder) {
			super.onSurfaceDestroyed(holder);
			Log.i("pyramid", "destroy");
			this.visible = false;
			handler.removeCallbacks(drawRunner);
		}

		private int getRotation() {
			return wManager.getDefaultDisplay().getRotation();
		}

		void setWorldGravity() {
			float gravityK = 2.0f;
			float gravityX = -sensor.getX() * gravityK;
			float gravityY = -sensor.getY() * gravityK;

			switch (getRotation()) {
			case Surface.ROTATION_0:
				wallpaperWorld.setGravity(gravityX, gravityY);
				break;
			case Surface.ROTATION_180:
				wallpaperWorld.setGravity(-gravityX, -gravityY);
				break;
			case Surface.ROTATION_90:
				wallpaperWorld.setGravity(-gravityY, gravityX);
				break;
			case Surface.ROTATION_270:
				wallpaperWorld.setGravity(gravityY, -gravityX);
				break;
			default:
				wallpaperWorld.setGravity(gravityX, gravityY);
				break;
			}
		}

		protected void draw() {
			SurfaceHolder holder = getSurfaceHolder();
			Canvas canvas = null;
			try {
				canvas = holder.lockCanvas();

				if (canvas != null) {

					canvas.drawColor(Color.WHITE);

					if (mode == WallpaperViewMode.physic) {

						setWorldGravity();

						wallpaperWorld.nextStep();
						storage.BeginPushFrame();
						for (BoxGuiWrapper boxWrapper : wallpaperWorld.dynamicBodies) {
							wallpaperScene.drawCube(canvas, boxWrapper, false);
						}
						for (BoxGuiWrapper groundBixWrapper : wallpaperWorld.staticBodies) {
							wallpaperScene.drawCube(canvas, groundBixWrapper,
									false);
						}
						storage.EndPushFrame();

					} else if (mode == WallpaperViewMode.timeLapse) {
						wallpaperScene.drawTimeLapse(canvas);
						storage.PopFrame();
						Log.d("pyramid", "pop");
						if (storage.isBegin()) {
							Log.d("pyramid", "before create");
							wallpaperWorld.createWorld(
									wallpaperScene.getWidth(),
									wallpaperScene.getHeight());
							mode = WallpaperViewMode.physic;
						}

					}
				}
			} finally {
				if (canvas != null)
					holder.unlockCanvasAndPost(canvas);
			}

			if (visible) {
				if (mode == WallpaperViewMode.physic)
					handler.postDelayed(drawRunner, 1000 / 60);
				else if (mode == WallpaperViewMode.timeLapse)
					handler.postDelayed(drawRunner, 5);
			}

		}

		@Override
		public void onVisibilityChanged(boolean visible) {

			super.onVisibilityChanged(visible);
			this.visible = visible;

			if (visible) {
				handler.post(drawRunner);
			} else {
				handler.removeCallbacks(drawRunner);
			}
		}

		@Override
		public void onTouchEvent(MotionEvent event) {
			if (mode == WallpaperViewMode.timeLapse)
				return;
			detector.onTouchEvent(event);
			super.onTouchEvent(event);
			if (event.getAction() == MotionEvent.ACTION_DOWN) {

				final Vec2 p = wallpaperScene.ConvertViewToWorld(event.getX(),
						event.getY());
				wallpaperWorld.onMouseDown(p.x, p.y);

			} else if (event.getAction() == MotionEvent.ACTION_MOVE) {

				final Vec2 p = wallpaperScene.ConvertViewToWorld(event.getX(),
						event.getY());
				wallpaperWorld.onMouseMove(p.x, p.y);

			} else if (event.getAction() == MotionEvent.ACTION_UP) {

				final Vec2 p = wallpaperScene.ConvertViewToWorld(event.getX(),
						event.getY());
				wallpaperWorld.onMouseUp(p.x, p.y);
			}

		}

		private void changeViewMode() {
			if (mode == WallpaperViewMode.physic)
				mode = WallpaperViewMode.timeLapse;
		}
	}

}
