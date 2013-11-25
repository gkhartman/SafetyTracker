package com.example.android_safetytracker;


public class Calibrate {
	private double accX,accY,accZ,previousX,previousY,previousZ;
	private double gyroX,gyroY,gyroZ,previousGyroX,previousGyroY,previousGyroZ;
	private long startTime;
	private final double  THRESHOLD= 5.04; //random threshold 
	private final double GYROTHRESH = 5.0000;
	private boolean isCalibrated, good;
	static boolean timeIsUp;

	
	public Calibrate(){
		accX = 0;
		accY = 0;
		accZ = 0;
		gyroX = 0;
		gyroY = 0;
		gyroZ = 0;
		isCalibrated = false;
		startTime = System.currentTimeMillis();
		timeIsUp = false;
		
	}
	
	public void startCalibrating(float xValuePassed,float yValuePassed, float zValuePassed,float gyroZPassed,
			 					float gyroXPassed, float gyroYPassed){
		System.out.println("Im calibrating");
		if(Math.abs(startTime-System.currentTimeMillis()) >5000){
			timeIsUp = true;
		}
		
	    previousX = accX;
	    previousY = accY;
	    previousZ = accZ;
	    previousGyroX = gyroX;
	    previousGyroY = gyroY;
	    previousGyroZ = gyroZ;
	    
	    accX = xValuePassed;
		accY= yValuePassed;
		accZ = zValuePassed; 
		gyroX = gyroXPassed;
		gyroY = gyroYPassed;
		gyroZ = gyroZPassed;
		
		
		if(Math.abs(accX-previousX) > THRESHOLD || 
				Math.abs(accY-previousY) > THRESHOLD || 
					Math.abs(accZ-previousZ) > THRESHOLD ||
						Math.abs(gyroX-previousGyroX) > GYROTHRESH ||
							Math.abs(gyroY - previousGyroY) > GYROTHRESH )
								{
			
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
	public double getGyroX(){
		return previousGyroX;
	}
	public double getGyroY(){
		return previousGyroY;
	}
	public double getGyroZ(){
		return previousGyroZ;
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
