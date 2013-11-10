package com.example.android_safetytracker;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

public class Edit_UserInfo extends Activity implements OnClickListener{
	
	private Button submitButton;
	private String name;
	private int age;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_edit__user_info);
		
		submitButton = (Button)findViewById(R.id.editUser_SubmitButton);
		submitButton.setOnClickListener(this);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.edit__user_info, menu);
		return true;
	}

	@Override
	public void onClick(View v) {
		if(v.getId()==R.id.editUser_SubmitButton)
		{
			submitButtonClick();
		}
	}

	private void submitButtonClick()
	{
		if(!parseData())
			return;
		saveInformation();
		CheckBox cb = (CheckBox)findViewById(R.id.setparent_box);
		if(cb.isChecked())
		{
			startActivity(new Intent("android.intent.action.Edit_ParentInfo"));
			finish();
		}
		else
		{
			finish();
		}
		
	}
	
	private boolean parseData()
	{
		EditText input = (EditText)findViewById(R.id.username_input);
		name = input.getText().toString();
		input = (EditText)findViewById(R.id.userAge_input);
		if(input.length()==0)
		{
			Toast.makeText(this,"Invalid Age",Toast.LENGTH_SHORT).show();
			return false;
		}
		age = Integer.parseInt((input.getText()).toString());
		name.replace(" ", "");
		if(name.length() <= 1)
		{
			Toast.makeText(this, "Invalid name", Toast.LENGTH_SHORT).show();;
			return false;
		}
		if(age <= 10)
		{
			Toast.makeText(this, "Not old enough to drive", Toast.LENGTH_SHORT).show();
			return false;
		}
		return true;
		
	}
	
	private void saveInformation()
	{
		//to be implemented
	}
	
	
}
