package com.example.android_safetytracker;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.LinkedList;

import android.app.Service;
import android.content.Intent;
import android.location.LocationManager;
import android.os.IBinder;

public class Engine extends Service
{
	// usingGPS - user cannot connect or does not want to use GPS
	private GPSLocation gps;
	private Orientation orientation;
	private boolean usingGPS; 
	private float [] accValues = {0,0,0};
	private float [] gyroValues = {0,0,0};
	Calibrate calibrator;
	boolean notGoodForIntialValues;	
	float initialXValue,initialYValue, initialZValue,initialGyroX,initialGyroY,initialGyroZ ;  
	float temporaryXGyro = 0;
	float previousXGyro = 0;
	Event infraction;
	boolean firstTimeStart = true;
	boolean weAreGoingUpOrDown = false;
	long startTime;
	LinkedList<Event> linkedList;
	boolean theIgnoreTimerIsUp;

	
	private static BeginActivity begin;
	
	public Engine(){}
	public Engine(BeginActivity ba)
	{
		begin = ba;
	}
	
	@Override
	public void onCreate() {
		
		super.onCreate();
		orientation = new Orientation(this);
		calibrator = new Calibrate();
		notGoodForIntialValues = true;

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
		gps = new GPSLocation(this); //initialize gps and pass a  static reference of engine to gps
		if(!checkGPSEnabled())
		{
			begin.promptEnableGPS();
		}
	}
	
	
	public boolean checkGPSEnabled()
	{
		LocationManager lm = (LocationManager) getSystemService(LOCATION_SERVICE);
		return lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
	}

	@Override
	public void onDestroy() {
		stopService(new Intent(getBaseContext(), GPSLocation.class));
		stopService(new Intent(getBaseContext(), Orientation.class));
		begin = null;
		super.onDestroy();
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		startService(new Intent(getBaseContext(), GPSLocation.class));
		startService(new Intent(getBaseContext(), Orientation.class));
		checkGPS();
		usingGPS = checkGPSEnabled();
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
	/////////////////////////////////////////Gyroscope features///////////
	
	
	
	public void setAccelerationValues(float[] accValues){
		
		this.accValues = accValues;
		
		beginCalibration();
		
	}
	public void setGyroscopeValues(float[] gyroValues){
		
		this.gyroValues = gyroValues;
		
		beginCalibration();
		

	}
	
	public void beginCalibration (){
		
		
		 
		 if(!calibrator.timeIsUp()){
				if(!calibrator.isCalibrated()){
					calibrator.startCalibrating(accValues[0],accValues[1],accValues[2],
												gyroValues[0], gyroValues[1], gyroValues[2]);
				
				return;
				}
				
		   }
		
		
		
		 if(calibrator.timeIsUp() && calibrator.isValid()&& notGoodForIntialValues){
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
							float gyroZValue, float gyroXValue, float gyroYValue) {
		boolean theOrientationChanged = false;
		
		//System.out.println(gyroXValue +" "+ initialGyroX);
		if((Math.abs(gyroXValue - initialGyroX) > 15)) {
				
			System.out.println("THE ORIENTATION CHANGED");
						
			//System.out.println(gyroXValue +"     "+ gyroYValue + "     "+gyroZValue);
			
			theOrientationChanged = true;
		}
		else{
			theOrientationChanged = false;
		}
		
		if(theOrientationChanged){
			if(firstTimeStart){
				startTime = System.currentTimeMillis();
				firstTimeStart = false;
			}
			
			previousXGyro = temporaryXGyro;
			temporaryXGyro = gyroXValue;
			
			theIgnoreTimerIsUp = true;
			
			//System.out.println(previousXGyro +"      "+ temporaryXGyro);
			
			if(Math.abs(System.currentTimeMillis() - startTime) < 3000){
				theIgnoreTimerIsUp = false;
				if((Math.abs(previousXGyro) - Math.abs(temporaryXGyro)) <5){
					weAreGoingUpOrDown = true;
					
				}else{
					weAreGoingUpOrDown = false;
					
				}
				
			}
			
		}
		else{
			weAreGoingUpOrDown = false;
			theIgnoreTimerIsUp = false;
			firstTimeStart = true;
			
		}
		
	
		
		if(weAreGoingUpOrDown && theOrientationChanged && theIgnoreTimerIsUp){
			// implement what to do when up or down
			System.out.println("Do it Paul");
		}
		else if(weAreGoingUpOrDown && theOrientationChanged && !theIgnoreTimerIsUp){
			System.out.println("Ignore");
			// it was just a road bump, ignore
		
		}
		else{ // The world is flat
			System.out.println("TheWorldIsFlat");
			theWorldIsFlat(accXValue,  accYValue,  accZValue,
							 gyroXValue,  gyroYValue,  gyroZValue);
		}
		
		
		///////////////////////////////////////////////////////////////////////////////////////////////////////
	
		
//		double a =   Math.sqrt( Math.abs ( accZValue * accZValue + accXValue * accXValue + accYValue * accYValue
//																						- 9.2 * 9.2			 ) );
//		if(a > 4.8){
//			//do something
//			System.out.println("I messed up!!!");
//		}
//		System.out.println("-------------   "+ a);
		
		//////////////////////////////////////////////////////////////////////////////////////////////////////////
		
	}
	
	private void theWorldIsFlat(float accXValue, float accYValue, float accZValue,
			float gyroXValue, float gyroYValue, float gyroZValue){
		
		
		float gForce =calculateGforce(accXValue,accYValue,accZValue);
		
		boolean violation =evaluateGForce(gForce);
		
		float virtualX = accXValue - initialXValue;
		float virtualY = accYValue - initialYValue;
		float virtualZ = accZValue - initialZValue;
		//System.out.println("XValue*****"+ virtualX+ "***YValue**"+ virtualY+"****zValue"+virtualZ);
		
		
		//System.out.println("*******"+gForce);
		
		if(violation){						//will use values to tell what violations && tells if the 3 second lag is up			//*****eliminated the firstTime usless
			
			
			if(virtualZ < 1.0 && Math.abs(virtualX) <1.5){									/// random value that is not right
				System.out.println("--ACC----virtual values----"+ virtualX +"   " +virtualZ  );
				linkedList = Logs.getLinkedList();
				linkedList.addFirst(new Event("accelerating"));
				Logs.setLinkedList(linkedList); 
			}
			else if(virtualZ> 1.0 && Math.abs(virtualX)<1.5){
				System.out.println("--DCC-----------virtual values----"+ virtualX +"   " +virtualZ  );
				linkedList = Logs.getLinkedList();
				linkedList.addFirst(new Event("decellerating"));
				Logs.setLinkedList(linkedList); 
				
			}
			else{
				System.out.println("----TURNING------virtual values----"+ virtualX +"---" +virtualZ  );
				linkedList = Logs.getLinkedList();
				linkedList.addFirst(new Event("turning"));
				Logs.setLinkedList(linkedList); 
			}
			
		}
		
	}
	private boolean evaluateGForce(float gForce) {
		float upperLimit = (float) .30;						//This value  was lazily researched     //*********change upper limit to test
		
		if(gForce < upperLimit){
			return false;
		}
		return true;
	}
	
	private float calculateGforce(float xValue, float yValue, float zValue) {
			
			// The virtualNumbers should read (x,y,z) = (0,9.8,0) since these are the values in a perfect situation
			// where the phone is upright perpendicular to the acceleration vectors
			float virtualNumberX = Math.abs(xValue) - Math.abs(initialXValue);
			float virtualNumberY = yValue - initialYValue;
			float virtualNumberZ = Math.abs(zValue) - Math.abs(initialZValue);
			
			
			float inaccurateGravityReadingForTheY  = (float) 9.806; 					// this is the value that the phone gives while stil
			virtualNumberY = virtualNumberY + inaccurateGravityReadingForTheY;		// its +inaccurate... because it always reads gravity
			//System.out.println(virtualNumberX+"----"+ virtualNumberY+"++++"+virtualNumberZ); // this should give the value in ideal situation
	
			float xGforce = convertToGforce(virtualNumberX);
			float yGforce = convertToGforce(virtualNumberY);
			float zGforce = convertToGforce(virtualNumberZ);
			
			//return (float) Math.sqrt(xGforce * xGforce + yGforce * yGforce + zGforce * zGforce);
			return (float) Math.sqrt(xGforce * xGforce + zGforce * zGforce);
			
		}
	
	private float convertToGforce(float value) {
		float g = (float) (value/9.80665);
		return g;
	}
	public Event getEvent(){
		return infraction;
	}
	private void writeToFile(String s){
		BufferedWriter bufferedWriter = null;
		try {
			bufferedWriter = new BufferedWriter(new FileWriter(new 
			        File(getFilesDir()+File.separator+"Logs.txt")));
			bufferedWriter.write(s);
			bufferedWriter.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	
	
}

	
	
	

	
	
	
	
	
	

