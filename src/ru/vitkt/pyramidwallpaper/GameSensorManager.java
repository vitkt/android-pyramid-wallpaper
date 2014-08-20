package ru.vitkt.pyramidwallpaper;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

public class GameSensorManager implements SensorEventListener {
	private Context context;

	private final SensorManager mSensorManager;
	private final Sensor mAccelerometer;

	public GameSensorManager(Context _context) {

		context = _context;

		mSensorManager = (SensorManager) context
				.getSystemService(Context.SENSOR_SERVICE);

		mAccelerometer = mSensorManager
				.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

	}

	@Override
	public void onAccuracyChanged(Sensor arg0, int arg1) {
	}

	private float x;

	private float y;

	private float z;

	@Override
	public void onSensorChanged(SensorEvent event) {
		Sensor sensor = event.sensor;
		if (sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
			setX(event.values[0]);
			setY(event.values[1]);
			setZ(event.values[2]);

		}

	}

	public void onResume() {
		if (mAccelerometer != null)
			mSensorManager.registerListener(this, mAccelerometer,
					SensorManager.SENSOR_DELAY_FASTEST);

	}

	public void onPause() {
		mSensorManager.unregisterListener(this);

	}

	float getX() {
		return x;
	}

	void setX(float x) {
		this.x = x;
	}

	float getY() {
		return y;
	}

	void setY(float y) {
		this.y = y;
	}

	float getZ() {
		return z;
	}

	void setZ(float z) {
		this.z = z;
	}

}
