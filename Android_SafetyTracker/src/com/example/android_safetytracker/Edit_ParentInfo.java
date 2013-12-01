package com.example.android_safetytracker;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

/**
 * This class allows for the parental information to be edited by the user/parent.
 *
 */
public class Edit_ParentInfo extends Activity implements OnClickListener
{
	private Button submit;
	private String email, phone;
	private Consumer consumer;

	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		consumer = Consumer.getInstance();
		setContentView(R.layout.activity_edit__parent_info);
		submit = (Button)findViewById(R.id.parentInfo_submit);
		submit.setOnClickListener(this);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) 
	{
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.edit__parent_info, menu);
		return true;
	}

	@Override
	public void onClick(View view) 
	{
		switch(view.getId())
		{
		case R.id.parentInfo_submit:
			if(!parseInfo())
				return;
			saveInformation();
			finish();
			break;
		}
	}

	private boolean parseInfo()
	{
		EditText input = (EditText)findViewById(R.id.parentEmail_input);
		email = input.getText().toString();
		input = (EditText)findViewById(R.id.parentPhone_input);
		phone = input.getText().toString();
		email.replace(" ", "");
		phone.replace(" ","");
		phone.replace("-","");
		phone.replace("(", "");
		phone.replace(")", "");	
	
		if(email.length() == 0 && phone.length() == 0 )
		{
			Toast.makeText(this,"At least one way to contact must be provided.",Toast.LENGTH_SHORT).show();
			return false;
		}
		
		if(phone.length() < 10 && !isEmailValid(email))
		{
			Toast.makeText(this, "Invalid input, please try again.", Toast.LENGTH_SHORT).show();
			return false;
		}
		return true;
	}
	
	public static boolean isEmailValid(String email) 
	{
	    boolean isValid = false;

	    String expression = "^[\\w\\.-]+@([\\w\\-]+\\.)+[A-Z]{2,4}$";
	    CharSequence inputStr = email;

	    Pattern pattern = Pattern.compile(expression, Pattern.CASE_INSENSITIVE);
	    Matcher matcher = pattern.matcher(inputStr);
	    if (matcher.matches())
	        isValid = true;
	    return isValid;
	}
	
	private void saveInformation()
	{
		writeToFile(email + "\n" + phone);
		consumer.setEmail(email);
		consumer.setPhone(phone);
		consumer.setMonitored(true);
	}
	
	private void writeToFile(String s)
	{
		BufferedWriter bufferedWriter = null;
		try 
		{
			bufferedWriter = new BufferedWriter(new FileWriter(new 
			        File(getFilesDir()+File.separator+"Parent.txt")));
			bufferedWriter.write(s);
			bufferedWriter.close();
		} catch (IOException e) 
		{
			e.printStackTrace();
		}	
	}
}
