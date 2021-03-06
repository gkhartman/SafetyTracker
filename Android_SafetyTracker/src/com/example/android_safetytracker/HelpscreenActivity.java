package com.example.android_safetytracker;

import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;

/**
 * This class is for the help screen in the SafetyTracker application.
 *
 */
public class HelpscreenActivity extends Activity 
{
	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.helpscreen);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) 
	{
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.helpscreen, menu);
		return true;
	}
}
