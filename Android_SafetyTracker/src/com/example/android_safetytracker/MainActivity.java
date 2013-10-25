package com.example.android_safetytracker;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class MainActivity extends Activity implements OnClickListener{
	
	Button startAppButton;
	Button logsButton;
	Button userInfo;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		startAppButton = (Button)findViewById(R.id.menu_StartAppButton);
		startAppButton.setOnClickListener(this);
		
		logsButton = (Button)findViewById(R.id.menu_LogsButton);
		logsButton.setOnClickListener(this);
		
		userInfo = (Button)findViewById(R.id.menu_UserInfo);
		userInfo.setOnClickListener(this);
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public void onClick(View v) {
		switch(v.getId()){
			case R.id.menu_StartAppButton:
				beginClick();
				break;
			case R.id.menu_LogsButton:
				beginLogClick();
				break;
				
			case R.id.menu_UserInfo:
				beginUserInfoClick();
				break;
				
			}
	}

	private void beginUserInfoClick() {
		startActivity(new Intent("android.intent.action.UserInfoActivity"));		
	}

	private void beginLogClick() {
		startActivity(new Intent("android.intent.action.Logs"));
		
	}

	private void beginClick() {
		startActivity(new Intent("android.intent.action.Start_App"));
		
	}

}
