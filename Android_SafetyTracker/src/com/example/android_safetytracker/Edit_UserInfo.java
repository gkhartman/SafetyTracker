package com.example.android_safetytracker;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

/**
 * This class allows for the user to edit his/her information.
 * @author Johnny Lam
 *
 */
public class Edit_UserInfo extends Activity implements OnClickListener
{
	private Button submitButton;
	private String name;
	private int age;

	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_edit__user_info);
		
		submitButton = (Button)findViewById(R.id.editUser_SubmitButton);
		submitButton.setOnClickListener(this);
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
			finish();
		}
		else
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
		System.out.println( this.getApplicationContext().getFilesDir().getAbsolutePath());
		String s = name + "~" + age;
		writeToFile(s);
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
	
	private void readFromFile()
	{
		try
		{
			BufferedReader bufferedReader = new BufferedReader(new FileReader(new 
					File(getFilesDir() + File.separator + "User.txt")));
			String read;
			StringBuilder builder = new StringBuilder("");

			while((read = bufferedReader.readLine()) != null)
			{
				builder.append(read);
			}
			Log.d("Output", builder.toString());
			bufferedReader.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
