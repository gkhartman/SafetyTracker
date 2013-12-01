package com.example.android_safetytracker;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

/**
 * this class is related to activity_user_info.xml 
 *
 */
public class UserInfoActivity extends Activity implements OnClickListener
{
	private Button editUser, editParent;
	private String name,age,phone,email;
	private Consumer consumer;

	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_user_info);
		initializeButtons();
		consumer = Consumer.getInstance();
		setDefault();
		setUpUserInfo();
		setUpParentInfo();
	}
	
	private void setDefault() 
	{
		name = "No name entered.";
		age = "No age set.";
		phone = "No number in file.";
		email = "No email entered.";
		TextView textView = (TextView) findViewById(R.id.userInfo_nameOfUser);
		textView.setText(name);
		textView = (TextView) findViewById(R.id.userInfo_theAge);
		textView.setText(age);
		textView = (TextView) findViewById(R.id.userInfo_PhoneNumber);
		textView.setText(phone);
		textView = (TextView) findViewById(R.id.userInfo_Email);
		textView.setText(email);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) 
	{
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
	       case R.id.userInfo_EditParentButton:
		        editParentClick();
		        break;
		        
	       case R.id.userInfo_EditUserButton:
	    	    editUserClick();
	    	    break;
	   }
	}

	private void editParentClick()
	{
		startActivity(new Intent("android.intent.action.Edit_ParentInfo"));
		finish();
	}
	
	private void editUserClick()
	{
		startActivity(new Intent("android.intent.action.Edit_UserInfo"));
		finish();
	}
	
	private void setUpUserInfo() 
	{
		TextView textView = (TextView) findViewById(R.id.userInfo_nameOfUser);
		textView.setText(consumer.getName());
		textView = (TextView) findViewById(R.id.userInfo_theAge);
		textView.setText(consumer.getAge());
	}
	
	private void setUpParentInfo() 
	{
		if(consumer.isMonitored())
		{
			TextView textView = (TextView) findViewById(R.id.userInfo_PhoneNumber);
			textView.setText(consumer.getPhone());
			textView = (TextView) findViewById(R.id.userInfo_Email);
			textView.setText(consumer.getEmail());
		}
	}
}
