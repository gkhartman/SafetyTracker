package com.example.android_safetytracker;


class Consumer extends User
{
   private boolean isMonitored;
   private String phone;
   private String email;
   private static Consumer instance = null;
   
   private Consumer() {}
   
   public static synchronized Consumer getInstance()
   {
	   if(instance == null)
		   instance = new Consumer();
	   return instance;
   }
   
   public void setMonitored(boolean isMonitored)
   {
	   this.isMonitored = isMonitored;
   }
   
   public boolean isMonitored() { return isMonitored; }
   
   public void setPhone(String phone) { this.phone = phone; }
   
   public String getPhone() { return phone; }
   
   public void setEmail(String email)
   {
	   this.email = email;
   }
   
   public String getEmail()
   {
	   return email;
   }
}
