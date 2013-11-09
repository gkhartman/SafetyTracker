package com.example.android_safetytracker;


//uml should be reviewed for this class and report!!
class Consumer extends User
{
   private boolean isMonitored;
   private String phone;
   
   public void setMonitored(boolean isMonitored)
   {
	   this.isMonitored = isMonitored;
   }
   
   public boolean isMonitored()
   {
	   return isMonitored;
   }
   
   public void setPhone(String phone)
   {
	   this.phone = phone;
   }
   
   public String getPhone()
   {
	   return phone;
   }
}
