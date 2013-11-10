package com.example.android_safetytracker;


public class Calibrate {
	private double x,y,z,previousX,previousY,previousZ;
	private long startTime;
	private final double  THRESHOLD= 5.04; //random threshold 
	private boolean isCalibrated, good;
	static boolean timeIsUp;

	
	public Calibrate(){
		x = 0;
		y = 0;
		z = 0;
		isCalibrated = false;
		startTime = System.currentTimeMillis();
		timeIsUp = false;
		
	}
	
	 void startCalibrating(float xValuePassed,float yValuePassed, float zValuePassed){
		//System.out.println("Im calibrating");
		if(Math.abs(startTime-System.currentTimeMillis()) >5000){
			timeIsUp = true;
		}
		
	    previousX = x;
	    previousY = y;
	    previousZ = z;
	    
	    x = xValuePassed;
		y = yValuePassed;
		z = zValuePassed; 

		if(Math.abs(x-previousX) > THRESHOLD || 
				Math.abs(y-previousY) > THRESHOLD || 
					Math.abs(z-previousZ) > THRESHOLD){
			
			good = false;
			startTime = System.currentTimeMillis();
		}
		else{
			good = true;

		}

			
			//System.out.println(good); //dubugger method
			
		if(timeIsUp && good){
			isCalibrated = true;
		}
	}
	
	public double getX(){
		return previousX;
	}
	public double getY(){
		return previousY;
	}
	public double getZ(){
		return previousZ;
	}
	
	public boolean isCalibrated(){
		return isCalibrated;
	}
	public boolean isValid(){
		return good;
	}
	public boolean timeIsUp(){
		return timeIsUp;
	}


	
}
