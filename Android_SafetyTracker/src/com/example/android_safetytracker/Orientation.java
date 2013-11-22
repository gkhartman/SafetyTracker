package com.example.android_safetytracker;

import android.app.Service;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.IBinder;

public class Orientation extends Service implements SensorEventListener{
	Sensor accelerometer, magneticField;
	SensorManager sensorM;
	
	
	static float [] accValues= new float[3];
	static float [] values = new float [3];
	static float [] inR = new float [9];
	static float [] inclineMatrix = new float [9];
	static float  mInclination;
	static float [] gravityMatrix = new float[3];
	static float [] geomagneticMatrix = new float[3];
	
	/////////////////////////////////////////////Do the xml
	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onSensorChanged(SensorEvent event) {
		
		boolean ready = false;
		int counter=0;
	    switch (event.sensor.getType()) {
	    case Sensor.TYPE_ACCELEROMETER:
	        for(int i=0; i<3; i++){
	            accValues[i] =  event.values[i];
	        }
	        if(geomagneticMatrix[0] != 0)
	            ready = true;

	        break;

	    case Sensor.TYPE_MAGNETIC_FIELD:
	        for(int i=0; i<3; i++){
	        	geomagneticMatrix[i] = event.values[i];
	        }
	        if(accValues[2] != 0)
	            ready = true;

	        break;
	    }

	    if(!ready)
	        return;

	    boolean cek = SensorManager.getRotationMatrix(inR, inclineMatrix, accValues, geomagneticMatrix);
	    System.out.println(cek);

	    if(cek){
	        SensorManager.getOrientation(inR, values);
	        mInclination = SensorManager.getInclination(inclineMatrix);

	        
	        if(counter++ % 15 == 0){
	        	System.out.println("azimuth"+ Math.toDegrees(values[0])+" pitch  "+  Math.toDegrees(values[1])+" roll "+ Math.toDegrees(values[2]));
	            counter = 1;
	        }
	    }
	}

	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}
	public void onCreate(){
		
		sensorM = (SensorManager)getSystemService(SENSOR_SERVICE);
		accelerometer = sensorM.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
		magneticField = sensorM.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
		
		sensorM.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);
		sensorM.registerListener(this, magneticField, SensorManager.SENSOR_DELAY_NORMAL);
	}
	public float[] getAccelerometerValuesArray(){
		return accValues;
	}
	public float[] getOrientationValues(){
		return values;
	}
	public void onDestroy (){
		sensorM.unregisterListener(this,accelerometer);
		sensorM.unregisterListener(this,magneticField);
		super.onDestroy();
		
	}



}
