package com.example.android_safetytracker;

import java.util.Calendar;

public class Event {
	
	private Calendar cInstance;
	private String type;
	private double longitude, latitude;
	
	public Event(String typePassed){
		cInstance = Calendar.getInstance();
	//  location = getTheLocation                     Victor said gettin tis was easy
		this.type = typePassed;
		
	}
	
	public Event(String typePassed, double latitude, double longitude)
	{
		cInstance = Calendar.getInstance();
		this.longitude = longitude;
		this.latitude = latitude;
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
	public void setLocation(double lat, double lon){
		longitude = lon;
		latitude = lat;
	}
	public String getLocation(){
		return latitude+" , "+longitude;
	}
	
}
