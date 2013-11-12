package com.example.android_safetytracker;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Date;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

public class Logs extends Activity {

	private Spinner logDropDown;
	private TextView text,textMiddle,textRight;
	private static LinkedList<Event> linkedList = new LinkedList<Event>();
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_logs);
		
		logDropDown = (Spinner) findViewById(R.id.log_DropDown);
		text = (TextView)findViewById(R.id.tv);
		
		logDropDown = (Spinner) findViewById(R.id.log_DropDown);
		textMiddle = (TextView)findViewById(R.id.tvMiddle);
		
		logDropDown = (Spinner) findViewById(R.id.log_DropDown);
		textRight = (TextView)findViewById(R.id.tvRight);
		
		ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.spinnerarray,
		android.R.layout.simple_spinner_item);
		
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		
		
		logDropDown.setAdapter(adapter);
		logDropDown.setOnItemSelectedListener(new function());
		
		
		///////////////////////////////////////////////////////////////////////////////////////
		//debug code
		
		
		int theCounter = 0;
		while(linkedList.size()>theCounter){
		
		
		System.out.println(linkedList.get(theCounter).getEventType());
		System.out.println(linkedList.get(theCounter).getDateTime());
		theCounter++;
		}
		
		///////////////////////////////////////////////////////////////////////////////////////

		
	}
	

	
	public class function implements OnItemSelectedListener {

		
		/**
		 * 1 Date, Event, Location
		 * 2 Event, Date, Location
		 * 3 Location, Date, Event
		 */
		@Override
		public void onItemSelected(AdapterView<?> parent, View arg1, int pos,
				long id) {
			
			if(pos ==0){
				String str = parent.getItemAtPosition(pos).toString();
				text.setText(" "+str);
			
				String str2 = parent.getItemAtPosition(pos+1).toString();
				textMiddle.setText(" "+str2);
			
				String str3 = parent.getItemAtPosition(pos+2).toString();
				textRight.setText(" "+str3);
				
			}
			else if(pos ==1){
				String str = parent.getItemAtPosition(pos).toString();
				text.setText(" "+str);
			
				String str2 = parent.getItemAtPosition(pos-1).toString();
				textMiddle.setText(" "+str2);
			
				String str3 = parent.getItemAtPosition(pos+1).toString();
				textRight.setText(" "+str3);
				
			}
			else{
				String str = parent.getItemAtPosition(pos).toString();
				text.setText(" "+str);
			
				String str2 = parent.getItemAtPosition(pos-2).toString();
				textMiddle.setText(" "+str2);
			
				String str3 = parent.getItemAtPosition(pos-1).toString();
				textRight.setText(" "+str3);
				
			}
		}

		@Override
		public void onNothingSelected(AdapterView<?> arg0) {
			// TODO Auto-generated method stub

		}

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.logs, menu);
		return true;
	}
	public static LinkedList<Event> getLinkedList(){
		return linkedList;
		
	}
	public static void setLinkedList(LinkedList<Event> theLogList){
		linkedList = theLogList;
	}
	
	
	class ListViewItem
	{
		public String textLabel;
	}
	

}
