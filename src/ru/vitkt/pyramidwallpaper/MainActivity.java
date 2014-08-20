package ru.vitkt.pyramidwallpaper;

import android.app.Activity;
import android.os.Bundle;

public class MainActivity extends Activity{
	
	
@Override
protected void onCreate(Bundle savedInstanceState) {

	super.onCreate(savedInstanceState);
	
    // Prepare for simulation. Typically we use a time step of 1/60 of a
    // second (60Hz) and 10 iterations. This provides a high quality simulation
    // in most game scenarios.
	sensor = new GameSensorManager(this);
   setContentView(new BoxView(this, sensor));

    // When the world destructor is called, all bodies and joints are freed. This can
    // create orphaned pointers, so be careful about your world management.

}

GameSensorManager sensor;
@Override
	protected void onResume() {
	
		// TODO Auto-generated method stub
		super.onResume();
		sensor.onResume();
	}
@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		sensor.onPause();
	}
}
