package com.example.android_safetytracker;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;


/**
 * this class is related to activity_user_info.xml 
 *
 */
public class UserInfoActivity extends Activity implements OnClickListener{
	
	private Button editUser, editParent;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_user_info);
		initializeButtons();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.user_info, menu);
		return true;
	}
	
	public void initializeButtons()
	{
		editUser = (Button)findViewById(R.id.userInfo_EditUserButton);
		editUser.setOnClickListener(this);
		
		editParent = (Button)findViewById(R.id.userInfo_EditParentButton);
		editParent.setOnClickListener(this);	
	}
	
	@Override
	public void onClick(View v)
	{
	   switch(v.getId())
	   {
	       case R.id.userInfo_EditUserButton:
		        startActivity(new Intent(""));
	   }
	}

}
