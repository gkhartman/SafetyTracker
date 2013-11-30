package com.example.android_safetytracker;

import android.net.Uri;
import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class AboutUs extends Activity implements android.view.View.OnClickListener 
{
	private Button emailUsButton,facebookButton, twitterButton;
	private TextView website;

	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_about_us);
		initializeButtons();		
	}
	
	/**
	 * This method initializes the buttons for the About Us screen in which the user can select a button.
	 */
	private void initializeButtons()
	{
		emailUsButton = (Button)findViewById(R.id.emailUs);
		emailUsButton.setOnClickListener(this);
		
		facebookButton = (Button)findViewById(R.id.facebookButton);
		facebookButton.setOnClickListener(this);
		
		twitterButton = (Button)findViewById(R.id.twitterButton);
		twitterButton.setOnClickListener(this);
		
		website = (TextView)findViewById(R.id.websiteTextLink);
		website.setOnClickListener(this);	
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) 
	{
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.about_us, menu);
		return true;
	}

	@Override
	public void onClick(View v) 
	{
		switch(v.getId())
		{
		case R.id.emailUs:
			beginEmailUsClick();
			break;
		case R.id.websiteTextLink:
			sendToWeb();
			break;
		case R.id.facebookButton:
			sendToFacebookWeb();
			break;
		case R.id.twitterButton:
			sendToTwitterWeb();
			break;
		}
	}
	
	/**
	 * This method directs the user to the Twitter account of our SafetyTracker app.
	 */
	private void sendToTwitterWeb()
	{
		Uri uri = Uri.parse("https://twitter.com/Safety_Tracker");
		startActivity(new Intent(Intent.ACTION_VIEW,uri));
	}
	
	/**
	 * This method directs the user to the Facebook website of our SafetyTracker app.
	 */
	private void sendToFacebookWeb()
	{
		Uri uri = Uri.parse("https://www.facebook.com/safetytrackerapp");
		startActivity(new Intent(Intent.ACTION_VIEW,uri));
	}
	
	/**
	 * This method directs the user to the SafetyTracker website.
	 */
	private void sendToWeb()
	{
		Uri uri = Uri.parse("http://www.jdlam7.wix.com/safetytracker");
		startActivity(new Intent(Intent.ACTION_VIEW,uri));
	}
	
	/**
	 * This method directs the user to the email screen.
	 */
	private void beginEmailUsClick()
	{
		startActivity(new Intent("android.intent.action.EmailScreen"));
	}
}
