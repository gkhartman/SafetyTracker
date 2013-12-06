package com.example.android_safetytracker;

import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;

/**
 * This class is for the screen with all the necessary legal information.
 *
 */
public class Legal extends Activity 
{
	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_legal);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) 
	{
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.legal, menu);
		return true;
	}
}
