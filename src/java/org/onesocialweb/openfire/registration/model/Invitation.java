package org.onesocialweb.openfire.registration.model;

import java.util.Date;


public interface Invitation {
	

	public  String getCode();

	public  void setCode(String code);

	public  Date getFrom();

	public  void setFrom (Date from);
	
	public  boolean getValid();
	
	public  void setValid(boolean valid);

	public  Date getExpires();

	public  void setExpires(Date expires);

	public  int getTotalDays();
	
	public  int getDaysLeft();
	
	public  String getEmail();
	

	
	public  int getTotalAccounts();

	public  void setTotalDays(int total) ;
	
	public  void setTotalAccounts(int totalUsers);

	public  int getUsed();

	public  void setUsed(int used);
	
	public  boolean hasCode();
	
	public  boolean hasExpires();
	
	public  boolean hasAccounts();
	
	public void setEmail(String email); 


}
