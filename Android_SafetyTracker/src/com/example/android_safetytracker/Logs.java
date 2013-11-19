package com.example.android_safetytracker;

import java.util.LinkedList;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

public class Logs extends Activity implements OnClickListener{

	private Spinner logDropDown;
	private TextView text,textMiddle,textRight;
	private static LinkedList<Event> linkedList = new LinkedList<Event>();
	private TextView [][] box;
	private int listPointer, spinnerChoice;
	private Button backButton,nextButton;
	
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
		
		listPointer = 0;
		spinnerChoice = 0;
		
		box = new TextView[7][3];
		initializeBox();
		
        backButton = (Button) findViewById(R.id.logsBackButton);
        backButton.setOnClickListener(this);
        nextButton = (Button) findViewById(R.id.logsNextButton);
        nextButton.setOnClickListener(this);
        
        disableButton(backButton);
        if(linkedList.size() >7)
        {
        	enableButton(nextButton);
        }
        else
        {
        	disableButton(nextButton);
        }
        
      //used for debuggin the map
      //linkedList.add(new Event("speeding",34.069227,-117.627118));
		
	}
	
	private void enableButton(Button button)
	{
		button.setEnabled(true);
	}
	
	private void disableButton(Button button)
	{
		button.setEnabled(false);
	}
	
	private void initializeBox()
	{
		box[0][0]= (TextView) findViewById(R.id.row1Left);
		box[0][1]= (TextView) findViewById(R.id.row1Mid);
		box[0][2]= (TextView) findViewById(R.id.row1Right);
		box[1][0]= (TextView) findViewById(R.id.row2Left);
		box[1][1]= (TextView) findViewById(R.id.row2Mid);
		box[1][2]= (TextView) findViewById(R.id.row2Right);
		box[2][0]= (TextView) findViewById(R.id.row3Left);
		box[2][1]= (TextView) findViewById(R.id.row3Mid);
		box[2][2]= (TextView) findViewById(R.id.row3Right);
		box[3][0]= (TextView) findViewById(R.id.row4Left);
		box[3][1]= (TextView) findViewById(R.id.row4Mid);
		box[3][2]= (TextView) findViewById(R.id.row4Right);
		box[4][0]= (TextView) findViewById(R.id.row5Left);
		box[4][1]= (TextView) findViewById(R.id.row5Mid);
		box[4][2]= (TextView) findViewById(R.id.row5Right);
		box[5][0]= (TextView) findViewById(R.id.row6Left);
		box[5][1]= (TextView) findViewById(R.id.row6Mid);
		box[5][2]= (TextView) findViewById(R.id.row6Right);
		box[6][0]= (TextView) findViewById(R.id.row7Left);
		box[6][1]= (TextView) findViewById(R.id.row7Mid);
		box[6][2]= (TextView) findViewById(R.id.row7Right);
	}
	
	private void redrawLogs()
	{
		if(listPointer != 0)
		{	
		   if(listPointer % 7 == 0 )
		   {
			  listPointer -= 7;
		   }
		   else
		   {
			  int topOfPage = listPointer/7;
		      listPointer = topOfPage*7;
		   }
		}
		addTextViews();
	}
	
	public void addTextViews()
	{
		for(int row = 0; row < 7; ++row)
		{
			if(listPointer >= linkedList.size())
			{
				fillRestWithBlanks(row);
				return;
			}
			addTextToBox(row, linkedList.get(listPointer++));
		}
	}
	
	private void fillRestWithBlanks(int row) 
	{
	   for(;row < 7; ++row)
	   {
		   for(int col = 0; col < 3; ++col)
		   {
			   box[row][col].setText("");
		   }
	   }
	}

	private void addTextToBox(int row, Event e )
	{
		DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
		Calendar cal = e.getDateTime();
		String date = dateFormat.format(cal.getTime());
		switch(spinnerChoice)
		{
		case 0: box[row][0].setText(date);
				addClickableAttributes(row, 0, false);
		        box[row][1].setText(e.getEventType());
		        addClickableAttributes(row, 1, false);
		        box[row][2].setText(e.getLocation());
		        addClickableAttributes(row, 2, true);
		        break;
		case 1: box[row][0].setText(e.getEventType());
				addClickableAttributes(row, 0, false);
		        box[row][1].setText(date);
		        addClickableAttributes(row, 1, false);
		        box[row][2].setText(e.getLocation());
		        addClickableAttributes(row, 2, true);
		        break;
		case 2: box[row][0].setText(e.getLocation());
				addClickableAttributes(row, 0, true);
		        box[row][1].setText(date);
		        addClickableAttributes(row, 1, false);
		        box[row][2].setText(e.getEventType());
		        addClickableAttributes(row, 2, false);
		        break;
		}
	}
	
	private void addClickableAttributes(int row, int col,boolean addAttributes)
	{
		if(addAttributes)
		{
			box[row][col].setOnClickListener(this);
		}
		else
		{
			box[row][col].setOnClickListener(null);
		}
		box[row][col].setClickable(addAttributes);
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
			spinnerChoice = pos;
			if(pos ==0){
				String str = parent.getItemAtPosition(pos).toString();
				text.setText(" "+str);
			
				String str2 = parent.getItemAtPosition(pos+1).toString();
				textMiddle.setText(" "+str2);
			
				String str3 = parent.getItemAtPosition(pos+2).toString();
				textRight.setText(" "+str3);
				
				redrawLogs();
				
			}
			else if(pos ==1){
				String str = parent.getItemAtPosition(pos).toString();
				text.setText(" "+str);
			
				String str2 = parent.getItemAtPosition(pos-1).toString();
				textMiddle.setText(" "+str2);
			
				String str3 = parent.getItemAtPosition(pos+1).toString();
				textRight.setText(" "+str3);
				
				redrawLogs();
				
			}
			else{
				String str = parent.getItemAtPosition(pos).toString();
				text.setText(" "+str);
			
				String str2 = parent.getItemAtPosition(pos-2).toString();
				textMiddle.setText(" "+str2);
			
				String str3 = parent.getItemAtPosition(pos-1).toString();
				textRight.setText(" "+str3);
				
				redrawLogs();
				
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

	@Override
	public void onClick(View v) {
		switch(v.getId())
		{
		case R.id.logsNextButton:
			nextButtonPressed();
			break;
		case R.id.logsBackButton:
			backButtonPressed();
			break;
		default:
			textViewPressed(v);
			break;
		}
	}
	
	private void textViewPressed(View v)
	{
		TextView textview = (TextView) findViewById(v.getId());
		String coordinates = (String) textview.getText();
		String [] brokenString = coordinates.split(",");
		brokenString[0] = brokenString[0].replace(" ", "");
		brokenString[1] = brokenString[1].replace(" ", "");
		double latitude = Double.parseDouble(brokenString[0]);
		double longitude = Double.parseDouble(brokenString[1]);
		String [] values = getOtherValues(v);
		String date = values[0];
		String infraction = values[1];
		Intent intent = new Intent(getApplicationContext(), Map.class);
		intent.putExtra("infraction", infraction);
		intent.putExtra("date", date);
		intent.putExtra("latitude", latitude);
		intent.putExtra("longitude", longitude);
		startActivity(intent);
	}
	
	
	private void nextButtonPressed()
	{
		addTextViews();
		enableButton(backButton);
		if(listPointer >= linkedList.size())
		{
			disableButton(nextButton);
		}
	}
	
	private void backButtonPressed()
	{
		if(listPointer % 7 != 0)
		{
			int temp = listPointer / 7;
			listPointer = temp*7;
			listPointer -= 7;
		}
		else
		{
		   listPointer -= 14;
		}
		addTextViews();
		enableButton(nextButton);
		if(listPointer == 7)
			disableButton(backButton);
	}
	
	private String[] getOtherValues(View v)
	{
		String [] values = new String[2];
		switch(v.getId())
		{
		case R.id.row1Left:
			values[0] = (String) box[0][1].getText();
			values[1] = (String) box[0][2].getText();
			break;
			
		case R.id.row1Right:
			values[0] = (String) box[0][0].getText();
			values[1] = (String) box[0][1].getText();
			break;
			
		case R.id.row2Left:
			values[0] = (String) box[1][1].getText();
			values[1] = (String) box[1][2].getText();
			break;
			
		case R.id.row2Right:
			values[0] = (String) box[1][0].getText();
			values[1] = (String) box[1][1].getText();
			break;
			
		case R.id.row3Left:
			values[0] = (String) box[2][1].getText();
			values[1] = (String) box[2][2].getText();
			break;
			
		case R.id.row3Right:
			values[0] = (String) box[2][0].getText();
			values[1] = (String) box[2][1].getText();
			break;
			
		case R.id.row4Left:
			values[0] = (String) box[3][1].getText();
			values[1] = (String) box[3][2].getText();
			break;
			
		case R.id.row4Right:
			values[0] = (String) box[3][0].getText();
			values[1] = (String) box[3][1].getText();
			break;
			
		case R.id.row5Left:
			values[0] = (String) box[4][1].getText();
			values[1] = (String) box[4][2].getText();
			break;
			
		case R.id.row5Right:
			values[0] = (String) box[4][0].getText();
			values[1] = (String) box[4][1].getText();
			break;
			
		case R.id.row6Left:
			values[0] = (String) box[5][1].getText();
			values[1] = (String) box[5][2].getText();
			break;
			
		case R.id.row6Right:
			values[0] = (String) box[5][0].getText();
			values[1] = (String) box[5][1].getText();
			break;
			
		case R.id.row7Left:
			values[0] = (String) box[6][1].getText();
			values[1] = (String) box[6][2].getText();
			break;
			
		case R.id.row7Right:
			values[0] = (String) box[6][0].getText();
			values[1] = (String) box[6][1].getText();
			break;
		}
		return values;
	}
}
