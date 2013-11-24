package com.example.android_safetytracker;


import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;

/**
 * The way this class works is by  first taking in an object of the Engine class.
 * When the engine is referenced now we can call the methods in the engine class.
 * The onLocationChanged method is able to reference the engine if it senses that
 * the user is speeding. The location is update every 1 second and 2 meters.
 * @author victor
 *
 */
public class GPSLocation extends Service implements LocationListener {

	private LocationManager lManager;
	private double longitude;
	private double latitude;

	private static Engine engine;

	// default constructor declaration is necessary.
	public GPSLocation() {}

	/*
	 * constructor used to pass in the engine object variable
	 */
	public GPSLocation(Engine eng) {
		engine = eng;
	}
	
	public double getLongitude()
	{
		return longitude;
	}
	
	public double getLatitude()
	{
		return latitude;
	}


	@Override
	public void onCreate() {
		System.out.println("on create called");
		longitude = 0.0f;
		latitude = 0.0f;
		super.onCreate();
		 lManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
	}

	public int onStartCommand(Intent intent, int flags, int startId) {
		System.out.println("on start called");
		lManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 2,this); //update every 1 second 2 meters (approx 5 mph)
		return START_STICKY;														
	}

	@Override
	public void onDestroy() {
		lManager.removeUpdates(this);
		lManager = null;
		engine = null;
		super.onDestroy();
	}

	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void onLocationChanged(Location location) {
	   float speed = location.getSpeed();
	   latitude = (float) location.getLatitude();
	   longitude = location.getLongitude();
       if(speed > 33.5) //33.5 m/s is approx. 75mph
       {
    	   engine.speeding(speed,longitude,latitude);
       }
	}

	@Override
	public void onProviderDisabled(String provider) {
		engine.gpsDisabled();
	}

	@Override
	public void onProviderEnabled(String provider) {
		engine.gpsEnabled();
	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {
		// TODO Auto-generated method stub

	}

}
