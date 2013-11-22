package com.example.android_safetytracker;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

public class Engine extends Service
{
	// usingGPS - user cannot connect or does not want to use GPS
	private GPSLocation gps;
	private boolean usingGPS; 
	
	private static BeginActivity begin;
	
	public Engine(){}
	public Engine(BeginActivity ba)
	{
		begin = ba;
	}
	
	@Override
	public void onCreate() {
		super.onCreate();
		checkGPS();
		
	}
	
	protected void gpsDisabled()
	{
		usingGPS = false;
	}
	
	protected void gpsEnabled()
	{
		usingGPS = true;
	}
	
	private void checkGPS()
	{
		gps = new GPSLocation(this); //initialize gps and pass a static reference of engine to gps
		if(!gps.checkGPSEnabled())
			begin.promptEnableGPS();
	}

	@Override
	public void onDestroy() {
		stopService(new Intent(getBaseContext(), GPSLocation.class));
		begin = null;
		super.onDestroy();
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		startService(new Intent(getBaseContext(), GPSLocation.class));
		usingGPS = gps.checkGPSEnabled();
		return START_STICKY;
	}

	public void speeding(float speed,double longitude, double latitude)
	{
		//add to user we can do this by using the singleton on user and loading the user data
		//when the app is launched. We can that get an instance of the user for this class.
		//It shouldn't be to difficult.
		//Disclaimer: Only My Opinion
	}

	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}
}
