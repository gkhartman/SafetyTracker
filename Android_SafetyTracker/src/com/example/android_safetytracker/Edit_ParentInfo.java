package com.example.android_safetytracker;

import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;

public class Edit_ParentInfo extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_edit__parent_info);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.edit__parent_info, menu);
		return true;
	}

}
