package com.example.android_safetytracker;

import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class Edit_ParentInfo extends Activity implements OnClickListener{
	
	private Button submit;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_edit__parent_info);
		
		submit = (Button)findViewById(R.id.parentInfo_submit);
		submit.setOnClickListener(this);
		
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.edit__parent_info, menu);
		return true;
	}

	@Override
	public void onClick(View view) {
		if(view.getId() == R.id.parentInfo_submit)
		{
			retrieveInfo();
		}
	}
	
	private void retrieveInfo()
	{
		String email = findViewById(R.id.parentEmail_input).toString();
		email.replace(" ",""); //remove blank spaces
		String phoneNum = findViewById(R.id.parentPhone_input).toString();
		phoneNum.replace(" ","");
		store(email,phoneNum);
	}

	
	private void store(String email, String phone)
	{
		/**
		 *  unimplemented method
		 */
		clickReturn();
	}
    
	private void clickReturn()
	{
		super.onBackPressed();
	}
	
}
