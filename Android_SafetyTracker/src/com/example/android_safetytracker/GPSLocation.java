package com.example.android_safetytracker;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.*;
import android.os.Bundle;
import android.os.IBinder;

/**
 * The way this class works is by first taking in an object of the Engine class.
 * When the engine is referenced now we can call the methods in the engine class.
 * The onLocationChanged method is able to reference the engine if it senses that
 * the user is speeding. The location is update every 1 second and 2 meters.
 */
public class GPSLocation extends Service implements LocationListener 
{
	private LocationManager lManager;
	private Location location;

	private static Engine engine;

	// default constructor declaration is necessary.
	public GPSLocation() {}

	/*
	 * constructor used to pass in the engine object variable
	 */
	public GPSLocation(Engine eng) { engine = eng; }
	

	@Override
	public void onCreate() 
	{
		location = null;
		lManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		super.onCreate();
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) 
	{
		lManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0,0,this); //update every 1 second 2 meters (approx 5 mph)
		return START_STICKY;
	}

	@Override
	public void onDestroy() 
	{
		lManager.removeUpdates(this);
		lManager = null;
		engine = null;
		super.onDestroy();
	}

	@Override
	public IBinder onBind(Intent intent) { return null; }

	@Override
	public void onLocationChanged(Location loc) 
	{
	   if(loc != null)
	   {
		   engine.setLocation(loc);
	   }
	   
	}
	
	@Override
	public void onProviderDisabled(String provider) { engine.gpsDisabled(); }

	@Override
	public void onProviderEnabled(String provider) { engine.gpsEnabled(); }

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {}
}
