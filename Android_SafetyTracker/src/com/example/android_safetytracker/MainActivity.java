package com.example.android_safetytracker;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import android.location.LocationManager;
import android.os.Bundle;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

public class MainActivity extends Activity implements OnClickListener
{
	//main screen buttons
	private Button startAppButton, logsButton, userInfo,
	       turnOnGPSButton, helpButton, legalButton, aboutUsButton;
	private final String USER_INFO = "User.txt";
	private final String PARENT_INFO = "Parent.txt";
	protected Consumer consumer;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		
		//check if file exists
		boolean fileDoesNotExist = true;
		File f = new File(getFilesDir()+File.separator+"User.txt");
		if(f.exists()) 
			fileDoesNotExist = false;
		
		consumer = Consumer.getInstance();
		
		if(fileDoesNotExist)
		{
			Intent intent = new Intent(getApplicationContext(),Edit_UserInfo.class);
			intent.putExtra("firstRun", true);
			startActivity(intent);
		}
		else
			loadInfo();
		setContentView(R.layout.activity_main);
		initializeButtons();
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) 
	{
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
	//initialize instances of buttons
	private void initializeButtons()
	{
		startAppButton = (Button)findViewById(R.id.menu_StartAppButton);
		startAppButton.setOnClickListener(this);
		
		logsButton = (Button)findViewById(R.id.menu_LogsButton);
		logsButton.setOnClickListener(this);
		
		userInfo = (Button)findViewById(R.id.menu_UserInfoButton);
		userInfo.setOnClickListener(this);
		
		turnOnGPSButton = (Button)findViewById(R.id.menu_turnOnGPSButton);
		turnOnGPSButton.setOnClickListener(this);
		
		helpButton = (Button)findViewById(R.id.menu_HelpButton);
		helpButton.setOnClickListener(this);
		
		legalButton = (Button)findViewById(R.id.menu_legalButton);
		legalButton.setOnClickListener(this);
		
		aboutUsButton = (Button)findViewById(R.id.menu_aboutUsButton);
		aboutUsButton.setOnClickListener(this);
	}

	//button click handler
	@Override
	public void onClick(View v) 
	{
		switch(v.getId())
		{
			case R.id.menu_StartAppButton:
				beginClick();
				break;
				
			case R.id.menu_LogsButton:
				beginLogClick();
				break;
				
			case R.id.menu_UserInfoButton:
				 beginUserInfoClick();
				 break;
				 
			case R.id.menu_aboutUsButton:
			     beginAboutUsClick();
			     break;
			     
			case R.id.menu_HelpButton:
				 beginHelpClick();
				 break;
				 
			case R.id.menu_legalButton:
				 beginLegalClick();
				 break;
				 
			case R.id.menu_turnOnGPSButton:
				 beginTurnOnGPSClick();
				 break;	
		}
	}

	/**
	 * This method first checks if the GPS is already on by checking if the location
	 * manager can access the location. If it is not then it sends the user to location
	 * preferences screen. GPS CANNOT BE SET WITHIN THE APP DUE TO SECURITY REASONS. 
	 * This method should be later moved to GPS class
	 */
	private void beginTurnOnGPSClick() 
	{
		LocationManager lm = (LocationManager) getSystemService(LOCATION_SERVICE);
		
		if(lm.isProviderEnabled(LocationManager.GPS_PROVIDER))
			Toast.makeText(this,"GPS is already on", Toast.LENGTH_SHORT).show();
		else
		{
			AlertDialog.Builder gpsAlert = new AlertDialog.Builder(this);
			gpsAlert.setMessage("Would you like to enable GPS from settings?")
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

				}
			});
			gpsAlert.create();
			gpsAlert.show();
		}
	}

	private void beginLegalClick() { startActivity(new Intent("android.intent.action.Legal")); }

	private void beginHelpClick() { startActivity(new Intent("android.intent.action.HelpscreenActivity")); }

	private void beginAboutUsClick() { startActivity(new Intent("android.intent.action.AboutUs")); }

	private void beginUserInfoClick() { startActivity(new Intent("android.intent.action.UserInfoActivity")); }

	private void beginLogClick() { startActivity(new Intent("android.intent.action.Logs")); }

	private void beginClick() { startActivity(new Intent("android.intent.action.Start_App")); }
	
	private void loadInfo()
	{
		try
		{
			File parentFile = new File(getFilesDir()+File.separator+"Parent.txt");
			BufferedReader bf = new BufferedReader(new FileReader(new File(getFilesDir()+File.separator+USER_INFO)));
			consumer.setName(bf.readLine());
			consumer.setAge(bf.readLine());
			bf.close();
			if(parentFile.exists())
			{
				consumer.setMonitored(true);
				bf = new BufferedReader(new FileReader(new File(getFilesDir()+File.separator+PARENT_INFO)));
				consumer.setEmail(bf.readLine());
				consumer.setPhone(bf.readLine());
				bf.close();
			}
			else
				consumer.setMonitored(false);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}

}
