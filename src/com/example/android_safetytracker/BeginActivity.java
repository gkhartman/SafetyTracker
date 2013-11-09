package com.example.android_safetytracker;

import java.util.LinkedList;


import com.example.android_safetytracker.R.raw;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;
import android.view.View;
import android.widget.Button;

public class BeginActivity extends Activity implements View.OnClickListener, SensorEventListener{
	
	Button begin_Stop;
	Sensor accelerometer;
	SensorManager sensorM;
	MediaPlayer mp;
	Event infraction;
	static float xValue;
	static float yValue;
	static float zValue;
	Calibrate calibrator;
	float initialXValue,initialYValue, initialZValue;       //These are  the values that will be set when calibrated
	boolean notGoodForIntialValues;							
	float startingTime,startTimer;										// Will be used to give a pause period when logged
	LinkedList<Event> linkedList;
	boolean isFirst = true;;
	
	
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_begin);
		
		begin_Stop = (Button) findViewById(R.id.begin_StopButton);
		begin_Stop.setOnClickListener(this);
		
		sensorM = (SensorManager)getSystemService(SENSOR_SERVICE);
		accelerometer = sensorM.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
		
		sensorM.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);
		mp=MediaPlayer.create(BeginActivity.this,raw.ding);
		
		calibrator = new Calibrate();
		notGoodForIntialValues = true;
		
		
		///////////////////////////////////////Will add an phony event when on creating to check if Linkedlist is workin
		linkedList = Logs.getLinkedList();
		linkedList.addFirst(new Event("accelerating"));
		Logs.setLinkedList(linkedList); 
		
		linkedList = Logs.getLinkedList();
		linkedList.addFirst(new Event("turning"));
		Logs.setLinkedList(linkedList);
		////////////////////////////////////////////
		
		
		
	}
	
	public void OnStart(){
		super.onStart();
		
		
	}
	
	
	
	

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.begin, menu);
		return true;
	}

	@Override
	public void onClick(View v) {
		switch(v.getId()){
		
			case R.id.begin_StopButton:
				beginClick();
				break;
		}
	}





	private void beginClick() {
		//code to save logs if needed
		finish();
	}





	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {
		// TODO Auto-generated method stub
		
	}




/*
 * The way the calibration works by continuing to read the changes on the onSensorChanged.
 * In the calibrator class there are several booleans that tell whether it is calibrated.
 * When the sensor changes, it passes the values to the startCalibrating method. The calibrate class
 * has a boolean variable named "good" and in order for the boolean isCalibrated to be set to true, there 
 * must have passed 5 seconds and the difference from the "x" and "previousX" must be less than the tolerable value
 * that is stored in THRESHOLD.Once that passes, then it will continue with normal function. 
 * WARNING: -If the user continues to move the phone then the "startTime" will continue to be reseted.
 * 			    So it will not stop calibrating until it gets acceptable values
 * 			-It should have a timer that eventually stops and says CANNOT CALIBRATE
 * 			-Also, when I return to the menu the onSensor continues to read. So if we can stop that when
 * 				the user leaves the screen. Also documenting sucks
 *  
 */
	@Override
	public void onSensorChanged(SensorEvent event) {
		
		
		 xValue = event.values[0];
		 yValue = event.values[1];
		 zValue = event.values[2];
		 
		 if(!calibrator.timeIsUp()){
			 if(!calibrator.isCalibrated()){
				calibrator.startCalibrating(xValue,yValue,zValue);
				return;
			 }
				
		   }
		 if(calibrator.timeIsUp() && !calibrator.isCalibrated()){ //NOTE:Debugin0 method to tell me ifts its calibrated...homie
			 System.out.println("Its not even calibrated homie");
		 }
		 
		 // if the time is up and the the values are values then we record the values and set
		 // notGoodForInitialValues to false because it automatically is set with the true value
		 if(calibrator.timeIsUp() && calibrator.isValid()&& notGoodForIntialValues){
			 initialXValue = (float) calibrator.getX();
			 initialYValue = (float) calibrator.getY();
			 initialZValue = (float) calibrator.getZ(); ///might result in loss of precision
			 notGoodForIntialValues = false; 		
			 return;
		 }
		 if(!notGoodForIntialValues){				// NOTE:Debuggin Method to tell me values and if its calibrated
			// System.out.println(initialXValue+"++++"+ initialYValue+"----"+initialZValue);
			 //System.out.println("I AM CALIBRATED HEAR ME ROAR");
		 }
		 
		 
		 
		
		
		float gForce =calculateGforce(xValue,yValue,zValue);
		
		boolean violation =evaluateGForce(gForce);
		
		float virtualX = xValue - initialXValue;
		float virtualY = yValue - initialYValue;
		float virtualZ = zValue - initialZValue;
		
		
		if(violation && isFirst){						//will use values to tell what violations && tells if the 3 second lag is up
			startTimer(isFirst);
			
			if(virtualZ > 1.5 && virtualX <1){									/// random value that is not right
				linkedList = Logs.getLinkedList();
				linkedList.addFirst(new Event("accelerating"));
				Logs.setLinkedList(linkedList); 
			}
			else if(virtualZ< 1.5&& virtualX<1){
				linkedList = Logs.getLinkedList();
				linkedList.addFirst(new Event("decellerating"));
				Logs.setLinkedList(linkedList); 
				
			}
			else{
				linkedList = Logs.getLinkedList();
				linkedList.addFirst(new Event("turning"));
				Logs.setLinkedList(linkedList); 
			}
			
		}
	}





	private boolean evaluateGForce(float gForce) {
		float upperLimit = (float) 1.20;						//This value was lazily researched
		
		if(gForce < upperLimit){
			return false;
		}
		return true;
	}

	private float calculateGforce(float xValue, float yValue, float zValue) {
		
		// The virtualNumbers should read (x,y,z) = (0,9.8,0) since these are the values in a perfect situation
		// where the phone is upright perpendicular to the acceleration vectors
		float virtualNumberX = xValue - initialXValue;
		float virtualNumberY = yValue - initialYValue;
		float virtualNumberZ = zValue - initialZValue;
		
		
		float inaccurateGravityReadingForTheY  = (float) 9.77; 					// this is the value that the phone gives while still
		float inaccurateGravityReadingForTheZ = (float) 0.813;
		virtualNumberY = virtualNumberY + inaccurateGravityReadingForTheY;		// its +inaccurate... because it always reads gravity
		//System.out.println(virtualNumberX+"----"+ virtualNumberY+"++++"+virtualNumberZ); // this should give the value in ideal situation

		float xGforce = convertToGforce(xValue);
		float yGforce = convertToGforce(yValue);
		float zGforce = convertToGforce(zValue);
		
		return (float) Math.sqrt(xGforce * xGforce + yGforce * yGforce + zGforce * zGforce);
		
	}

	private float convertToGforce(float value) {
		float g = (float) (value/9.80665);
		return g;
	}
	
	public Event getEvent(){
		return infraction;
	}
	private void startTimer(boolean isFirstTime){
		if(isFirstTime)
			startTimer = System.currentTimeMillis();
		
		if(Math.abs(startTimer - System.currentTimeMillis())<3000){
			isFirstTime = false;
		}
		else{
			isFirstTime = true;
		}
		
	}
	
}
