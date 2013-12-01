package com.example.android_safetytracker;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

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

/**
 * This class allows for the user to edit his/her information.
 *
 */
public class Edit_UserInfo extends Activity implements OnClickListener
{
	private Button submitButton;
	private String name;
	private int age;
	private boolean firstRun = false;
	private Consumer consumer;

	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_edit__user_info);
		consumer = Consumer.getInstance();
		submitButton = (Button)findViewById(R.id.editUser_SubmitButton);
		submitButton.setOnClickListener(this);
		firstRun = this.getIntent().getBooleanExtra("firstRun", false);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) 
	{
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.edit__user_info, menu);
		return true;
	}

	@Override
	public void onClick(View v) 
	{
		if(v.getId()==R.id.editUser_SubmitButton)
			submitButtonClick();
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
		}
		finish();
		
	}
	
	private boolean parseData()
	{
		EditText input = (EditText)findViewById(R.id.username_input);
		name = input.getText().toString();
		input = (EditText)findViewById(R.id.userAge_input);
		if(input.length() == 0)
		{
			Toast.makeText(this,"Invalid age, please try again.",Toast.LENGTH_SHORT).show();
			return false;
		}
		age = Integer.parseInt((input.getText()).toString());
		name.replace(" ", "");
		if(name.length() <= 1)
		{
			Toast.makeText(this, "Invalid name, please try again.", Toast.LENGTH_SHORT).show();;
			return false;
		}
		if(age <= 10)
		{
			Toast.makeText(this, "Sorry, you are not old enough to drive.", Toast.LENGTH_SHORT).show();
			return false;
		}
		return true;
	}
	
	private void saveInformation()
	{
		String s = name + "\n" + age;
		writeToFile(s);
		consumer.setName(name);
		consumer.setAge(String.valueOf(age));
	}
	
	private void writeToFile(String s)
	{
		BufferedWriter bufferedWriter = null;
		try 
		{
			bufferedWriter = new BufferedWriter(new FileWriter(new 
			        File(getFilesDir() + File.separator + "User.txt")));
			bufferedWriter.write(s);
			bufferedWriter.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public void onBackPressed()
	{
		if(firstRun)
		{
			Intent intent = new Intent(Intent.ACTION_MAIN);
			intent.addCategory(Intent.CATEGORY_HOME);
			intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			startActivity(intent);
		}
		else
			super.onBackPressed();
	}
	
}
