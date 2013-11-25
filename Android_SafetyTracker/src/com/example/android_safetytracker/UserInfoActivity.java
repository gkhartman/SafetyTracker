package com.example.android_safetytracker;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.util.Log;
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
		System.out.println( this.getApplicationContext().getFilesDir().getAbsolutePath());
		readFromFile("User.txt");
		readFromFile("Parent.txt");
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
	private void readFromFile(String fileName){
		try{
			 BufferedReader bufferedReader = new BufferedReader(new FileReader(new 
	                 File(getFilesDir()+File.separator+fileName)));
			 String read;
			 StringBuilder builder = new StringBuilder("");
	
			 while((read = bufferedReader.readLine()) != null){
				 builder.append(read);
			 }
			 Log.d("Output", builder.toString());
			 bufferedReader.close();
			 System.out.println(builder.toString());
		} catch (IOException e) {
				// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
}
