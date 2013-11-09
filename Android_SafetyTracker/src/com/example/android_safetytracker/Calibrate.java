package com.example.android_safetytracker;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;

public class Calibrate implements SensorEventListener {
	private double x,y,z,previousX,previousY,previousZ;
	private long startTime;
	private final double  THRESHOLD= .04; //random threshold 
	
	public Calibrate(){
		x = 0;
		y = 0;
		z = 0;
		startCalibrating();
	}
	
	private void startCalibrating(){
	    previousX = x;
	    previousY = y;
	    previousZ = z;
		
		startTime = System.currentTimeMillis();
		
		while(startTime-System.currentTimeMillis() <5000)
		{
			if(Math.abs(x-previousX) > THRESHOLD || Math.abs(y-previousY) > THRESHOLD || Math.abs(z-previousZ) > THRESHOLD)
				startTime = System.currentTimeMillis();
		}
	}
	
	public double getX(){
		return previousX;
	}
	public double getY(){
		return previousY;
	}
	public double getZ(){
		return previousZ;
	}

	@Override
	public void onAccuracyChanged(Sensor arg0, int arg1) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onSensorChanged(SensorEvent event) {
		x= event.values[0];
		y = event.values[1];
		z = event.values[2];
	}
}
