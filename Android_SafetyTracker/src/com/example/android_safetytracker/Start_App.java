package com.example.android_safetytracker;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class Start_App extends Activity implements View.OnClickListener {
	
	Button startApp_Begin;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_start__app);
		
		startApp_Begin = (Button) findViewById(R.id.startApp_Begin);
		startApp_Begin.setOnClickListener(this);
		

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.start__app, menu);
		return true;
	}
	
	

	@Override
	public void onClick(View v) {
		switch (v.getId()){
			case R.id.startApp_Begin:
				beginClick();
				break;
		}
	}

	private void beginClick() {
		startActivity(new Intent("android.intent.action.BeginActivity"));
	}

}
