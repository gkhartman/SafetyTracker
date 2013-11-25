package com.example.android_safetytracker;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class Event {
	
	private String date;
	private String type;
	private double longitude, latitude;
	
	/**
	 * used when not using gps
	 * @param typePassed
	 */
	public Event(String typePassed)
	{
		setCurrentDate();
		this.type = typePassed;
		longitude = 200;
		latitude = 200;
	}
	
	/**
	 * used when creating event from engine
	 * @param type
	 * @param latitude
	 * @param longitude
	 */
	public Event(String type, double latitude, double longitude)
	{
		setCurrentDate();
		this.type = type;
		this.latitude = latitude;
		this.longitude = longitude;
	}
	
	/**
	 * used for creating logs
	 * @param date
	 * @param type
	 * @param latitude
	 * @param longitude
	 */
	public Event(String date, String type, double latitude, double longitude)
	{
		this.date = date;
		this.type = type;
		this.latitude = latitude;
		this.longitude = longitude;
	}
	
	private void setCurrentDate()
	{
		DateFormat df = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
		date = df.format(Calendar.getInstance().getTime());
	}
	
	public String getLocation()
	{
		return latitude + ", " + longitude;
	}
	
	public String getEventType(){
		return type;
	}
	
	public double getLongitude()
	{
		return longitude;
	}
	
	public double getLatitude()
	{
		return latitude;
	}
	
	public String getDate()
	{
		return date;
	}
	
}
