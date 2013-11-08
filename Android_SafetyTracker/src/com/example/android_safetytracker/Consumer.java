package com.example.android_safetytracker;


//uml should be reviewed for this class and report!!
class Consumer extends User
{
   private boolean isMonitored;
   private int phone;
   
   public void setMonitored(boolean isMonitored)
   {
	   this.isMonitored = isMonitored;
   }
   
   public boolean isMonitored()
   {
	   return isMonitored;
   }
   
}
