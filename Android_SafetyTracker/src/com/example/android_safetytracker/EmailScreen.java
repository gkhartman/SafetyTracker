package com.example.android_safetytracker;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

/**
 * This class allows for the user to send an email to SafetyTracker.
 *
 */
public class EmailScreen extends Activity implements OnClickListener 
{
	//default used for feedback it automatically sets our email
	//and cannot be edited.
	private final String DEFAULT_EMAIL="carappfeedback@gmail.com";
	private static boolean useDefault = true;
	private Button sendEmailButton; 
	private String recipient, subject, message;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_email_screen);
		if(useDefault)
			setDefaultView();
		
		sendEmailButton = (Button)findViewById(R.id.Email_sendEmail);
		sendEmailButton.setOnClickListener(this);
	}

	private void setDefaultView()
	{
		EditText emailInput = (EditText)findViewById(R.id.emailInput);
		emailInput.setText(DEFAULT_EMAIL);
		emailInput.setEnabled(false);
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) 
	{
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.email_screen, menu);
		return true;
	}

	@Override
	public void onClick(View v) 
	{
		if(v.getId()==R.id.Email_sendEmail)
		   sendEmail();
	}
	
	private void sendEmail()
	{
		//info retrieval
	    EditText input = (EditText)findViewById(R.id.emailInput);
		recipient = input.getText().toString();
		input = (EditText)findViewById(R.id.subjectInput);
		subject = input.getText().toString();
		input = (EditText)findViewById(R.id.messageInput);
		message = input.getText().toString();
		
		Intent email = new Intent(Intent.ACTION_SEND);
			
		//prepare email
		email.putExtra(Intent.EXTRA_EMAIL, new String[]{recipient});
		email.putExtra(Intent.EXTRA_SUBJECT, subject);
		email.putExtra(Intent.EXTRA_TEXT, message);
			
		//this is necessary really don't know why
		email.setType("message/rfc822");
			
		//prompt user to select an account
		startActivity(Intent.createChooser(email, "Choose an account:"));
		
		Toast.makeText(this, "Thank you and we appreciate your feedback.", Toast.LENGTH_SHORT).show();;
		finish();
	}
}

