package ru.vitkt.pyramidwallpaper;

import android.service.wallpaper.WallpaperService;
import android.view.MotionEvent;
import android.view.SurfaceHolder;

public class PyramidWallpaper extends WallpaperService {

	@Override
	public Engine onCreateEngine() {
	
		return new PyramidWallpaperEngine();
	}
	public class PyramidWallpaperEngine extends WallpaperService.Engine
	{
		@Override
		public void onSurfaceChanged(SurfaceHolder holder, int format,
				int width, int height) {
			// TODO Auto-generated method stub
			super.onSurfaceChanged(holder, format, width, height);
		}
		
		@Override
		public void onVisibilityChanged(boolean visible) {
			// TODO Auto-generated method stub
			super.onVisibilityChanged(visible);
		}
		
		@Override
		public void onTouchEvent(MotionEvent event) {
			// TODO Auto-generated method stub
			super.onTouchEvent(event);
		}
	}

}
