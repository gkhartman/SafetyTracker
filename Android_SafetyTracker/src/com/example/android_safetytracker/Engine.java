package com.example.android_safetytracker;

import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.util.LinkedList;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.location.Location;
import android.widget.Toast;

/**
 * This class is the engine of the entire SafetyTracker application.
 *
 */
public class Engine extends Service
{
	//gps variables
	private GPSLocation gps;
	private Location location;
	private boolean usingGPS;
	
	private Orientation orientation;
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
	private boolean timerIsOff = true;
	private double timer;
	private boolean displayedCalibrated = false;
	private int counterAcc, counterDcc, counterTurning;
	public static String logMessageContent; 
	
	
	private static BeginActivity begin;
	
	public Engine() {}
	
	public Engine(BeginActivity ba) { begin = ba; }
	
	@Override
	public void onCreate() 
	{
		super.onCreate();
		orientation = new Orientation(this);
		calibrator = new Calibrate();
		linkedList = new LinkedList<Event>();
		notGoodForIntialValues = true;
		speedingTimer = System.currentTimeMillis() - 300001;
		location = null;
	}
	
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) 
	{
		notGoodForIntialValues = true;
		calibrator = new Calibrate();
		usingGPS = begin.checkGPSEnabled();
		startService(new Intent(getBaseContext(), GPSLocation.class));
		startService(new Intent(getBaseContext(), Orientation.class));
		gps = new GPSLocation(this);
		return START_STICKY;
	}
	
	protected void gpsDisabled() 
	{ 
	   begin.setGPSStatusImage("disabled");
	   usingGPS = false; 
	}
	
	protected void gpsEnabled() 
	{
		begin.setGPSStatusImage("waiting");
		usingGPS = true; 
	}
	
	
	protected void gpsStarted()
	{
		begin.setGPSStatusImage("ready");
	}
	
	@Override
	public void onDestroy() 
	{
		stopService(new Intent(getBaseContext(), GPSLocation.class));
		stopService(new Intent(getBaseContext(), Orientation.class));
		if(linkedList.size() > 0)
		{
			writeToFile();
		}
		super.onDestroy();
	}

	/**
	 * called from the gpsLocation
	 * @param location
	 */
	protected void setLocation(Location loc)
	{
		location = loc;
		if(loc.getSpeed() > 33.5)
			speeding(loc.getSpeed(),loc.getLongitude(),loc.getLatitude());
		gpsStarted();
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
				if(!calibrator.isCalibrated()){
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
		 
		 if(!displayedCalibrated)
		 {
			 Toast.makeText(this, "Calibrated....", Toast.LENGTH_SHORT).show();
			 displayedCalibrated = true;
		 }
	
		 process(accValues[0],accValues[1],accValues[2],
					gyroValues[0], gyroValues[1], gyroValues[2]);
		
	}
	
	private void process(float accXValue, float accYValue, float accZValue,
							float gyroZValue, float gyroXValue, float gyroYValue) 
	{
		boolean theOrientationChanged = false;
		if((Math.abs(gyroXValue - initialGyroX) > 10 && Math.abs(gyroXValue - initialGyroX) <15 )) {
			
			//System.out.println("THE ORIENTATION CHANGED");
			
			theOrientationChanged = true;
		}
		else
		{
			theOrientationChanged = false;
		}
		
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
			//System.out.println(previousXGyro +"      "+ temporaryXGyro);
			
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
		
//		System.out.println(accYValue +"  "+ accZValue);
//		System.out.println(accYValue*accYValue+ accZValue*accZValue);
		
		if(weAreGoingUpOrDown && theOrientationChanged && theIgnoreTimerIsUp)
		{
			// implement what to do when up or down
			 float theOrientationAngle = Math.abs(Math.abs(gyroXValue) - Math.abs(initialGyroX));
			 float lamda = (float) Math.toRadians((90 - theOrientationAngle));
			//System.out.println(lamda);
			//System.out.println(Math.cos(lamda));
			
			 float zGravityComponent = (float) Math.sqrt(Math.cos(lamda)*9.8*9.8);//
			
			//System.out.println(zGravityComponent);

			
			accZValue = Math.abs(accZValue) - zGravityComponent;
			//System.out.println(accZValue);
			theWorldIsFlat(accXValue,  accYValue,  accZValue,
					 gyroXValue,  gyroYValue,  gyroZValue);
			//System.out.println("Do it Paul");
		}
		else
		{ // The world is flat
			//System.out.println("TheWorldIsFlat");
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
	
private void timer(){
		
		if(timerIsOff){
			//System.out.println("NEW Timer");
			timer =  System.currentTimeMillis();
		}
		double actualTime = System.currentTimeMillis();
		//actualTime =(TimeUnit.MILLISECONDS.toSeconds((long) actualTime));
		//timer = (TimeUnit.MILLISECONDS.toSeconds((long) timer));

		
		
		if(Math.abs((actualTime - timer)) < 3000){
			
			timerIsOff = false;
		}
		else{
			timerIsOff = true;
			counterAcc = 0;
			counterDcc = 0;
			counterTurning =0;
		}
	}
	
	private void calculateWhenOrientationChanged(float accXValue,
		float accYValue, float accZValue, float gyroXValue,
		float gyroYValue, float gyroZValue) 
	{

		float theOrientationAngle = Math.abs(Math.abs(gyroXValue) - Math.abs(initialGyroX));
		float lamda = 90 - theOrientationAngle;
		float zGravityComponent = (float)(9.8- Math.sqrt(Math.sin(lamda)*9.8*9.8));
	
		accZValue = accZValue - zGravityComponent;
		float gForce =calculateGforce(accXValue,accYValue,accZValue);
	
		boolean violation =evaluateGForce(gForce);
	
		float virtualX = accXValue - initialXValue;
		float virtualY = accYValue - initialYValue;
		float virtualZ = accZValue - initialZValue;




	}

	private void theWorldIsFlat(float accXValue, float accYValue, float accZValue,
			float gyroXValue, float gyroYValue, float gyroZValue){
		
		
		float gForce =calculateGforce(accXValue,accYValue,accZValue);
		
		boolean violation =evaluateGForce(gForce);
		
		float virtualX = accXValue - initialXValue;
		float virtualY = accYValue - initialYValue;
		float virtualZ = accZValue - initialZValue;
		timer();
		if(violation){						//will use values to tell what violations && tells if the 3 second lag is up			//*****eliminated the firstTime usless
			
			
			if(virtualZ < 0 && Math.abs(virtualX) <1.5)
			{									/// random value that is not right
				
				if(!timerIsOff){
					counterAcc++;
				}
				
				if(counterAcc == 4)
				{
					Toast.makeText(this, "Accelerating", Toast.LENGTH_SHORT).show();
					if(usingGPS &&  location != null)
					{        
						linkedList.addFirst(new Event("Accelerating", location.getLatitude(), location.getLongitude()));
					}
					else
						linkedList.addFirst(new Event("Accelerating"));
					counterAcc++;
				}
			}
			else if(virtualZ> 0 && Math.abs(virtualX)<1.5)
			{
				if(!timerIsOff){
					counterDcc++;
				}
				if(counterDcc ==4)
				{
					Toast.makeText(this, "Braking", Toast.LENGTH_SHORT).show();
					if(usingGPS && location != null)
					{
						linkedList.addFirst(new Event("Braking",location.getLatitude(),location.getLongitude()));
					}
					else
						linkedList.addFirst(new Event("Accelerating"));
					counterDcc++;
				}				
			}
			else if(Math.abs(virtualX) > 1.7)
			{
				if(!timerIsOff)
				{
					counterTurning++;
				}
				if(counterTurning == 4)
				{
					Toast.makeText(this, "Turning", Toast.LENGTH_SHORT).show();
					if(usingGPS && location != null)
					{
						linkedList.addFirst(new Event("Turning",location.getLatitude(),location.getLongitude()));
					}
					else
						linkedList.addFirst(new Event("Turning"));
					counterTurning++;
				}
			}
//			else
//				System.out.println("I dunno");
		}
		
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
		float g = (float) (value/9.806);
		return g;
	}
	
	private boolean evaluateGForce(float gForce) {
		float upperLimit = (float) .31 ;						//This value  was lazily researched     //*********change upper limit to test
		
		if(gForce < upperLimit){
			return false;
		}
		return true;
	}
	
	
	public Event getEvent() { return infraction; }
	
	/**
	 * date, type, latitude, longitude
	 */
	private void writeToFile()
	{
		Event event = null;
		logMessageContent = "";
	    try
	    {
		   FileOutputStream fos = openFileOutput("Logs.txt", MODE_APPEND);
		   OutputStreamWriter osw = new OutputStreamWriter(fos);
		   for(int i = linkedList.size()-1; i >=0 ; --i)
		   {
			   event = linkedList.get(i);
			   logMessageContent += event.toString()+ "\n";
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