package com.example.android_safetytracker;

import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;
import android.view.View;
import android.widget.Button;

public class BeginActivity extends Activity implements View.OnClickListener{
	
	Button begin_Stop;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_begin);
		
		begin_Stop = (Button) findViewById(R.id.begin_StopButton);
		begin_Stop.setOnClickListener(this);
	}
	
	
	
	

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.begin, menu);
		return true;
	}

	@Override
	public void onClick(View v) {
		switch(v.getId()){
		
			case R.id.begin_StopButton:
				beginClick();
				break;
		}
	}





	private void beginClick() {
		//code to save logs if needed
		finish();
	}

}
