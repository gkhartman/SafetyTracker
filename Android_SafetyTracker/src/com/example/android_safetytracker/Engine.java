package com.example.android_safetytracker;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.LinkedList;

import android.app.Service;
import android.content.Intent;
import android.location.LocationManager;
import android.os.IBinder;

/**
 * This class is the engine of the entire SafetyTracker application.
 * @author Johnny Lam
 *
 */
public class Engine extends Service
{
	// usingGPS - user cannot connect or does not want to use GPS
	private GPSLocation gps;
	private Orientation orientation;
	private boolean usingGPS; 
	private float [] accValues = {0,0,0};
	private float [] gyroValues = {0,0,0};
	private Calibrate calibrator;
	private boolean notGoodForIntialValues;	
	private float initialXValue,initialYValue, initialZValue,initialGyroX,initialGyroY,initialGyroZ ;  
	private float temporaryXGyro = 0;
	private float previousXGyro = 0;
	private Event infraction;
	private boolean firstTimeStart = true;
	private boolean weAreGoingUpOrDown = false;
	private long startTime, speedingTimer;
	private LinkedList<Event> linkedList;
	private boolean theIgnoreTimerIsUp;

	
	private static BeginActivity begin;
	
	public Engine() {}
	
	public Engine(BeginActivity ba) { begin = ba; }
	
	@Override
	public void onCreate() 
	{
		super.onCreate();
		orientation = new Orientation(this);
		linkedList = new LinkedList<Event>();
		speedingTimer = System.currentTimeMillis() - 300001;
	}
	
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) 
	{
		notGoodForIntialValues = true;
		calibrator = new Calibrate();
		startService(new Intent(getBaseContext(), GPSLocation.class));
		startService(new Intent(getBaseContext(), Orientation.class));
		checkGPS();
		usingGPS = checkGPSEnabled();
		return START_STICKY;
	}
	
	protected void gpsDisabled() { usingGPS = false; }
	
	protected void gpsEnabled() { usingGPS = true; }
	
	private void checkGPS()
	{
		gps = new GPSLocation(this); //initialize gps and pass a  static reference of engine to gps
		if(!checkGPSEnabled())
			begin.promptEnableGPS();
	}
	
	
	public boolean checkGPSEnabled()
	{
		LocationManager lm = (LocationManager) getSystemService(LOCATION_SERVICE);
		return lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
	}

	@Override
	public void onDestroy() 
	{
		stopService(new Intent(getBaseContext(), GPSLocation.class));
		stopService(new Intent(getBaseContext(), Orientation.class));
		begin = null;
		writeToFile();
		super.onDestroy();
	}

	public void speeding(float speed,double longitude, double latitude)
	{
		//check if it has been at least 5 minutes since last speeding message
		if((System.currentTimeMillis() - speedingTimer) > 300000)
			linkedList.addFirst(new Event("Speeding",latitude,longitude));
	}

	@Override
	public IBinder onBind(Intent intent) { return null; }
	
	/////////////////////////////////////////Gyroscope features///////////
	
	public void setAccelerationValues(float[] accValues)
	{
		this.accValues = accValues;
		beginCalibration();
	}
	
	public void setGyroscopeValues(float[] gyroValues)
	{
		this.gyroValues = gyroValues;
		beginCalibration();
	}
	
	public void beginCalibration ()
	{
		 if(!calibrator.timeIsUp()){
				if(!calibrator.isCalibrated())
				{
					calibrator.startCalibrating(accValues[0],accValues[1],accValues[2],
												gyroValues[0], gyroValues[1], gyroValues[2]);
				
				return;
				}
		   }
		 
		 if(calibrator.timeIsUp() && calibrator.isValid()&& notGoodForIntialValues)
		 {
			 notGoodForIntialValues = false; 		
			 initialXValue = (float) calibrator.getX();
			 initialYValue = (float) calibrator.getY();
			 initialZValue = (float) calibrator.getZ();
			 initialGyroX = (float) calibrator.getGyroX();
			 initialGyroY = (float) calibrator.getGyroY();
			 initialGyroZ = (float) calibrator.getGyroZ();
			 notGoodForIntialValues = false;
			 return;		
		 }
		 
		// System.out.println(initialXValue + " "+ initialYValue+"  "+initialZValue);
		// System.out.println(initialGyroX + "  "+initialGyroY + "  "+initialGyroZ);
	
		 process(accValues[0],accValues[1],accValues[2],
			gyroValues[0], gyroValues[1], gyroValues[2]);
		
	}
	
	private void process(float accXValue, float accYValue, float accZValue,
							float gyroZValue, float gyroXValue, float gyroYValue) 
	{
		boolean theOrientationChanged = false;
		
		//System.out.println(gyroXValue +" "+ initialGyroX);
		if((Math.abs(gyroXValue - initialGyroX) > 45)) {  ///Value Must be changed
				
			System.out.println("THE ORIENTATION CHANGED");
						
			//System.out.println(gyroXValue +"     "+ gyroYValue + "     "+gyroZValue);
			
			theOrientationChanged = true;
		}
		else
			theOrientationChanged = false;
		
		if(theOrientationChanged)
		{
			if(firstTimeStart)
			{
				startTime = System.currentTimeMillis();
				firstTimeStart = false;
			}
			
			previousXGyro = temporaryXGyro;
			temporaryXGyro = gyroXValue;
			theIgnoreTimerIsUp = true;
			System.out.println(previousXGyro +"      "+ temporaryXGyro);
			
			if(Math.abs(System.currentTimeMillis() - startTime) < 3000)
			{
				theIgnoreTimerIsUp = false;
				if((Math.abs(previousXGyro) - Math.abs(temporaryXGyro)) < 5)
					weAreGoingUpOrDown = true;
				else
					weAreGoingUpOrDown = false;
			}
			
		}
		else
		{
			weAreGoingUpOrDown = false;
			theIgnoreTimerIsUp = false;
			firstTimeStart = true;
		}
		
		if(weAreGoingUpOrDown && theOrientationChanged && theIgnoreTimerIsUp)
		{
			// implement what to do when up or down
			System.out.println("Do it Paul");
		}
		else if(weAreGoingUpOrDown && theOrientationChanged && !theIgnoreTimerIsUp)
		{
			System.out.println("Ignore");
			// it was just a road bump, ignore
		}
		else
		{ // The world is flat
			System.out.println("TheWorldIsFlat");
			theWorldIsFlat(accXValue,  accYValue,  accZValue,
							 gyroXValue,  gyroYValue,  gyroZValue);
		}
		
		
////////////////////////////////////////////////////////////////////////////////////////////////////////////////
//	
//		double a =   Math.sqrt( Math.abs ( accZValue * accZValue + accXValue * accXValue + accYValue * accYValue
//																						- 9.2 * 9.2			 ) );
//		if(a > 4.8){
//			//do something
//			System.out.println("I messed up!!!");
//		}
//		System.out.println("-------------   "+ a);
//		
////////////////////////////////////////////////////////////////////////////////////////////////////////////////
		
	}
	
	private void theWorldIsFlat(float accXValue, float accYValue, float accZValue,
			float gyroXValue, float gyroYValue, float gyroZValue)
	{
		float gForce2 = calculateGforce2(accXValue,accYValue,accZValue);
		//float gForce1 = calculateGforce1(gyroXValue,accZValue);
		System.out.printf("accXValue %f, accYValue %f, accZValue %f\n", accXValue, accYValue, accZValue);
		//System.out.printf("initial gyro x %f, gyroXValue %f\n", initialGyroX, gyroXValue);
		//System.out.printf("Method 1: G Force is %f\n", gForce1);
		System.out.printf("Method 2: G Force is %f\n", gForce2); ////////////////////Need to take out when done
		//System.out.printf("Measured G is %f\n", getMeasuredGravity());
		boolean violationOccured = violation(convertToGforce(gForce2)); // original gForce, not gForce1
//		float virtualX = accXValue - initialXValue;
//		float virtualY = accYValue - initialYValue;
//		float virtualZ = accZValue - initialZValue;
		System.out.println("gps is ready? " + gps.isGPSReady() ); 
		
		//System.out.println("XValue*****"+ virtualX+ "***YValue**"+ virtualY+"****zValue"+virtualZ);
		//System.out.println("*******"+gForce);
		
//		if(violationOccured){						//will use values to tell what violations && tells if the 3 second lag is up			//*****eliminated the firstTime usless
//			if(virtualZ < 1.0 && Math.abs(virtualX) <1.5){									/// random value that is not right
//				System.out.println("--ACC----virtual values----"+ virtualX +"   " +virtualZ  );
//				if(!usingGPS)
//					linkedList.addFirst(new Event("Accelerating"));
//				else
//					linkedList.addFirst(new Event("Accelerating",gps.getLatitude(),gps.getLongitude()));
//			}
//			else if(virtualZ> 1.0 && Math.abs(virtualX)<1.5){
//				System.out.println("--DCC-----------virtual values----"+ virtualX +"   " +virtualZ  );
//				if(!usingGPS)
//					linkedList.addFirst(new Event("Decellerating"));
//				else
//					linkedList.addFirst(new Event("Decellerating",gps.getLatitude(),gps.getLongitude()));
//				
//			}
//			else{
//				System.out.println("----TURNING------virtual values----"+ virtualX +"---" +virtualZ  );
//				if(!usingGPS)
//					linkedList.addFirst(new Event("Turning"));
//				else
//					linkedList.addFirst(new Event("Tuning",gps.getLatitude(),gps.getLongitude()));
//			}
//		}
		
		if (violationOccured){
			System.out.println("You Fucked UP!"); // tomorrow start writing on this block, figure out how to id ACCELERATION/BRAKING/TURNING. One way to do it is to monitor the previous accXYZValue, 
		}
	}
	
	private boolean violation(float gForce) 
	{
		float lowerLimit = (float) .30;	// ******lowerLimit is like reading errors, or caused by road imperfection, or just good/safe driving
										// IMPORTANT VALUES *********************************************************************************
		float upperLimit = (float) 1.3; // ******upperLimit is either reading errors, or caused by road bumps
		if(gForce > lowerLimit && gForce < upperLimit)
			return true;
		return false;
	}
	
	private float calculateGforce2(float xValue, float yValue, float zValue) 
	{
			// The virtualNumbers should read (x,y,z) = (0,9.8,0) since these are the values in a perfect situation
			// where the phone is upright perpendicular to the acceleration vectors
//			float virtualNumberX = Math.abs(xValue) - Math.abs(initialXValue);
//			float virtualNumberY = yValue - initialYValue;
//			float virtualNumberZ = Math.abs(zValue) - Math.abs(initialZValue);
//			
//			
//			float inaccurateGravityReadingForTheY  = (float) 9.806; 					// this is the value that the phone gives while stil
//			virtualNumberY = virtualNumberY + inaccurateGravityReadingForTheY;		// its +inaccurate... because it always reads gravity
//			//System.out.println(virtualNumberX+"----"+ virtualNumberY+"++++"+virtualNumberZ); // this should give the value in ideal situation
//	
//			float xGforce = convertToGforce(virtualNumberX);
//			float yGforce = convertToGforce(virtualNumberY);
//			float zGforce = convertToGforce(virtualNumberZ);
//			
//			//return (float) Math.sqrt(xGforce * xGforce + yGforce * yGforce + zGforce * zGforce);
//			return (float) Math.sqrt(xGforce * xGforce + zGforce * zGforce);

		float gForce = (float) Math.sqrt(Math.abs(zValue*zValue + xValue*xValue + yValue*yValue 
				             - getMeasuredGravity()*getMeasuredGravity()));
		return gForce;
	}
//	private float calculateGforce1(double gyroXValue, double zValue){
//		float gForce2;
//		gForce2 = (float) (zValue / Math.cos(Math.abs((90-Math.abs(initialGyroX))+(initialGyroX-gyroXValue))));
//		return gForce2;
//	}
	public float getMeasuredGravity ()
	{
		float measuredGravity;
		measuredGravity = (float) Math.sqrt(initialXValue * initialXValue + initialYValue * initialYValue + initialZValue * initialZValue);
		return measuredGravity;
	}
	
	private float convertToGforce(float value) 
	{
		float g = (float) (value/getMeasuredGravity());
		return g;
	}
	public Event getEvent() { return infraction; }
	
	/**
	 * date, type, latitude, longitude
	 */
	private void writeToFile()
	{
		Event event = null;
	    try
	    {
		   FileOutputStream fos = openFileOutput("Logs.txt", MODE_APPEND);
		   OutputStreamWriter osw = new OutputStreamWriter(fos);
		   for(int i = 0; i < linkedList.size(); ++i)
		   {
			   event = linkedList.get(i);
			   osw.append(event.getDate()+"~"+event.getEventType()+"~"+event.getLatitude()+"~"+event.getLongitude()+"\n");
		   }
		   osw.close();
		   fos.close();
	    }
	    catch (Exception e) {
			e.printStackTrace();
		}
	}
}
