package com.example.android_safetytracker;

import java.util.LinkedList;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.LocationManager;
import android.os.Bundle;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.telephony.SmsManager;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;
import android.os.AsyncTask;
import android.widget.EditText;
import java.io.UnsupportedEncodingException;
import java.util.Properties;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

public class BeginActivity extends Activity implements View.OnClickListener, SensorEventListener
{
	private Button begin_Stop;
	private Sensor accelerometer;
	private SensorManager sensorM;
	private Event infraction;
	private static float xValue, yValue, zValue;
	private Calibrate calibrator;
	private float initialXValue,initialYValue, initialZValue;       //These are  the values that will be set when calibrated
	private boolean notGoodForIntialValues;							
	private float startingTime,startTimer;										// Will be used to give a pause period when logged
	private LinkedList<Event> linkedList;
	private int c = 0;
	private Engine engine;
	
	private static final String username = "carappfeedback@gmail.com";
    private static final String password = "theinterns";
    private EditText emailEdit;
    private EditText subjectEdit;
    private EditText messageEdit;
    private Consumer consumer;
    //used to hold the engine from running if user is prompted to start gps
    private boolean gpsHold;
    private ImageView  gpsImage;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_begin);
		
		begin_Stop = (Button) findViewById(R.id.begin_StopButton);
		begin_Stop.setOnClickListener(this);
		
		gpsImage = (ImageView) findViewById(R.id.gpsContainer);
		gpsImage.setOnClickListener(this);
		
		sensorM = (SensorManager) getSystemService(SENSOR_SERVICE);
		accelerometer = sensorM.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
		
		consumer = Consumer.getInstance();
		
		gpsHold = !checkGPSEnabled(); //if gps is off returns false..makes the hold true
	}
	
	public void OnStart() 
	{
		super.onStart();
	}
	
	@Override
	public void onResume()
	{
		if(gpsHold)
		{
			gpsHold = false;
			promptEnableGPS();
		}
		else
		{
			sensorM.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);
			calibrator = new Calibrate();
			notGoodForIntialValues = true;
			engine = new Engine(this);
			startService(new Intent(getBaseContext(), Engine.class));
			if(checkGPSEnabled())
			{
				setGPSStatusImage("loading");
			}
		}
		super.onResume();
	}
	
	public void setGPSStatusImage(String status)
	{
	  ImageView img = (ImageView) findViewById(R.id.gpsContainer);
	  if(status == "loading")
	  {
		  img.setImageResource(R.drawable.gps_waiting);
		  img.setTag(R.drawable.gps_waiting);
	  }
	  else if(status == "disabled")
	  {
		  img.setImageResource(R.drawable.gps_disabled);
		  img.setTag(R.drawable.gps_disabled);
	  }
	  else if(status == "ready")
	  {
		  img.setImageResource(R.drawable.gps_ready);
		  img.setTag(R.drawable.gps_ready);
	  }
	}
	
	@Override
	public void onPause()
	{
		sensorM.unregisterListener(this,accelerometer);
		stopService(new Intent(getBaseContext(),Engine.class));
		super.onPause();
	}
	
	
	
	public void onStop()
	{
		sensorM.unregisterListener(this,accelerometer);
		stopService(new Intent(getBaseContext(), Engine.class));
		super.onStop();
	}
	
	@Override
	public void onDestroy()
	{
		if(consumer.isMonitored() && engine.logMessageContent.length() > 0)
				setContent(engine.logMessageContent);
		super.onDestroy();
	}
	
	public boolean checkGPSEnabled()
	{
		LocationManager lm = (LocationManager) getSystemService(LOCATION_SERVICE);
		return lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
	}
	
	public void promptEnableGPS()
	{
		AlertDialog.Builder gpsAlert = new AlertDialog.Builder(this);
		gpsAlert.setMessage("Hold On... would you like to enable GPS? (Recommended)")
		.setCancelable(false)
		.setPositiveButton("Settings",
				new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int id)
			{
				startActivity(new Intent(
						android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
			}
		});
		gpsAlert.setNegativeButton("Cancel", 
				new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.cancel();
				onResume();
			}
		});
		gpsAlert.create();
		gpsAlert.show();
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) 
	{
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.begin, menu);
		return true;
	}

	@Override
	public void onClick(View v) 
	{
		switch(v.getId())
		{
			case R.id.begin_StopButton:
				beginClick();
				break;
			case R.id.gpsContainer:
				displayGPSStatusInfo(v);
		}
	}
	
	private void displayGPSStatusInfo(View v)
	{
		if((Integer)gpsImage.getTag()== R.drawable.gps_disabled)
		{
			Toast.makeText(this, "GPS is disabled", Toast.LENGTH_SHORT).show();
		}
		else if((Integer)gpsImage.getTag()== R.drawable.gps_waiting)
		{
			Toast.makeText(this, "GPS is connecting", Toast.LENGTH_SHORT).show();
		}
		else if((Integer)gpsImage.getTag() == R.drawable.gps_ready)
		{
			Toast.makeText(this, "GPS is running", Toast.LENGTH_SHORT).show();
		}
	}
	
	private void beginClick() 
	{
		sensorM.unregisterListener(this,accelerometer);
		finish();
	}
	@Override
	
	public void onAccuracyChanged(Sensor sensor, int accuracy) {}

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
		
	}
//		 xValue = event.values[0];
//		 yValue = event.values[1];
//		 zValue = event.values[2];
//		 
//		 if(!calibrator.timeIsUp()){
//			 if(!calibrator.isCalibrated()){
//				//calibrator.startCalibrating(xValue,yValue,zValue);
//				return;
//			 }
//				
//		   }
//		 if(calibrator.timeIsUp() && !calibrator.isCalibrated()){ //NOTE:Debugin0 method to tell me ifts its calibrated...homie
//			 System.out.println("Its not even calibrated homie");
//		 }
//		 
//		 // if the time is up and the the values are values then we record the values and set
//		 // notGoodForInitialValues to false because it automatic ally is set with the true value
//		 if(calibrator.timeIsUp() && calibrator.isValid()&& notGoodForIntialValues){
//			 initialXValue = (float) calibrator.getX();
//			 initialYValue = (float) calibrator.getY();
//			 initialZValue = (float) calibrator.getZ(); ///might  result in loss of precision
//			 notGoodForIntialValues = false; 		
//			 return;
//		 }
//		 if(!notGoodForIntialValues){				// NOTE:Debuggin Method to tell me values and if its calibrated
//			// System.out.println(initialXValue+"++++"+ initialYValue+"----"+initialZValue);
//			
//				System.out.println("I AM CALIBRATED HEAR ME ROAR");
//				
//				
//			}
//			
//		 }
//		 
//		 
//		 
//		
//		
//		float gForce =calculateGforce(xValue,yValue,zValue);
//		
//		float virtualX = xValue - initialXValue;
//		float virtualY = yValue - initialYValue;
//		float virtualZ = zValue - initialZValue;
//		
//		boolean violation =evaluateGForce(gForce,virtualY);
//		
//		System.out.println("XValue*****"+ virtualX+ "***YValue**"+ virtualY+"****zValue"+virtualZ);
//		
//		
//		//System.out.println("*******"+gForce);
//		
//		if(violation){						//will use values to tell what violations && tells if the 3 second lag is up			//*****eliminated the firstTime usless
//			
//			
//			if(virtualZ < 1.0 && Math.abs(virtualX) <1.5){									/// random value that is not right
//				System.out.println("--ACC----virtual values----"+ virtualX +"   " +virtualZ  );
//				linkedList = Logs.getLinkedList();
//				linkedList.addFirst(new Event("accelerating"));
//				Logs.setLinkedList(linkedList); 
//			}
//			else if(virtualZ> 1.0 && Math.abs(virtualX)<1.5){
//				System.out.println("--DCC-----------virtual values----"+ virtualX +"   " +virtualZ  );
//				linkedList = Logs.getLinkedList();
//				linkedList.addFirst(new Event("decellerating"));
//				Logs.setLinkedList(linkedList); 
//				
//			}
//			else{
//				System.out.println("----TURNING------virtual values----"+ virtualX +"---" +virtualZ  );
//				linkedList = Logs.getLinkedList();
//				linkedList.addFirst(new Event("turning"));
//				Logs.setLinkedList(linkedList); 
//			}
//			
//			long startTimer = System.currentTimeMillis();					//###########Added the do  nothing ITS WRONG
//			while((System.currentTimeMillis() - startTimer) <500){
//				///do nothing
//			}
//			
//		}
//	}
	
	private boolean evaluateGForce(float gForce,float virtualY) 
	{
		float upperLimit = (float) 1.07;			//This value was lazily researched     //*********change upper limit to test
		
		if(gForce < upperLimit || (virtualY > 1.0))
			return false;
		return true;
	}

	private float calculateGforce(float xValue, float yValue, float zValue) 
	{
		// The virtualNumbers should read (x,y,z) = (0,9.8,0) since these are the values in a perfect situation
		// where the phone is upright perpendicular to the acceleration vectors
		float virtualNumberX = xValue - initialXValue;
		float virtualNumberY = yValue - initialYValue;
		float virtualNumberZ = zValue - initialZValue;
		
		
		float inaccurateGravityReadingForTheY  = (float) 9.806; 					// this is the value that the phone gives while still
		float inaccurateGravityReadingForTheZ = (float) 0.0;
		virtualNumberY = virtualNumberY + inaccurateGravityReadingForTheY;		// its +inaccurate... because it always reads gravity
		//System.out.println(virtualNumberX+"----"+ virtualNumberY+"++++"+virtualNumberZ); // this should give the value in ideal situation

		float xGforce = convertToGforce(xValue);
		float yGforce = convertToGforce(yValue);
		float zGforce = convertToGforce(zValue);
		
		return (float) Math.sqrt(xGforce * xGforce + yGforce * yGforce + zGforce * zGforce);
		//return (float) Math.sqrt(xGforce * xGforce + zGforce * zGforce);
	}

	private float convertToGforce(float value) 
	{
		float g = (float) (value/9.80665);
		return g;
	}
	
	public Event getEvent() { return infraction; }
	
	private void startTimer(boolean isFirstTime)
	{
		if(isFirstTime)
			startTimer = System.currentTimeMillis();
		
		if(Math.abs(startTimer - System.currentTimeMillis()) < 3000)
			isFirstTime = false;
		else
			isFirstTime = true;
	}
	
	public void setContent(String message) {
		System.out.println("method called");
		if (consumer.isMonitored()) {
			if (consumer.getEmail() != null && consumer.getEmail().length() > 0)
			{
				System.out.println("inside email\n" + message);
				sendMail(consumer.getEmail(), "Driving Report", message);
				System.out.print(message);
			}
			if (consumer.getPhone() != null && consumer.getPhone().length() >0)
			{
				System.out.println("inside texting");
				sendSMS(message);
			}
		}
	}
	
	private void sendSMS(String message) {
		SmsManager sms = SmsManager.getDefault();
		sms.sendTextMessage(consumer.getPhone(), null, consumer.getName() + " had violations\n" + message, null, null);
	}
	
	private void sendMail(String email, String subject, String messageBody) 
	{
        Session session = createSessionObject();

        try {
            Message message = createMessage(email, subject, messageBody, session);
            new SendMailTask().execute(message);
        } catch (AddressException e) {
            e.printStackTrace();
        } catch (MessagingException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    private Message createMessage(String email, String subject, String messageBody, Session session) throws MessagingException, UnsupportedEncodingException 
    {
        Message message = new MimeMessage(session);
        message.setFrom(new InternetAddress("carappfeedback@gmail.com", "theinterns"));
        message.addRecipient(Message.RecipientType.TO, new InternetAddress(email, email));
        message.setSubject(subject);
        message.setText(messageBody);
        return message;
    }

    private Session createSessionObject() 
    {
        Properties properties = new Properties();
        properties.put("mail.smtp.auth", "true");
        properties.put("mail.smtp.starttls.enable", "true");
        properties.put("mail.smtp.host", "smtp.gmail.com");
        properties.put("mail.smtp.port", "587");

        return Session.getInstance(properties, new javax.mail.Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(username, password);
            }
        });
    }

    private class SendMailTask extends AsyncTask<Message, Void, Void> 
    {
      //  private ProgressDialog progressDialog;

        @Override
        protected void onPreExecute() 
        {
            super.onPreExecute();
           // progressDialog = ProgressDialog.show(BeginActivity.this, "Please wait", "Sending mail", true, false);
        }

        @Override
        protected void onPostExecute(Void aVoid) 
        {
            super.onPostExecute(aVoid);
           // progressDialog.dismiss();
        }

        @Override
        protected Void doInBackground(Message... messages) 
        {
            try {
                Transport.send(messages[0]);
            } catch (MessagingException e) {
                e.printStackTrace();
            }
            return null;
        }
    }
}
