package com.example.android_safetytracker;

import java.util.Calendar;

public class Event {
	
	Calendar cInstance;
	String type;
	
	public Event(String typePassed){
		cInstance = Calendar.getInstance();
	//  location = getTheLocation                     Victor said gettin tis was easy
		this.type = typePassed;
		
	}
	
	
	
	public void setEventType(String type){
		this.type= type;
	}
	
	public String getEventType(){
		return type;
	}
	public void setDateTime(long time){
		 
	}
	public synchronized Calendar getDateTime(){
		return cInstance;
		
	}
	public void setLocation(){
		
	}
	public String getLocation(){
		return "temp string";
	}
	
}
