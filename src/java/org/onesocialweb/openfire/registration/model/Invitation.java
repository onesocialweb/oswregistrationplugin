package org.onesocialweb.openfire.registration.model;

import java.util.Date;


public abstract class Invitation {
	

	public abstract String getCode();

	public abstract void setCode(String code);

	public abstract Date getFrom();

	public abstract void setFrom (Date from);
	
	public abstract boolean getValid();
	
	public abstract void setValid(boolean valid);

	public abstract Date getExpires();

	public abstract void setExpires(Date expires);

	public abstract int getTotalDays();
	
	public abstract int getDaysLeft();
	

	
	public abstract int getTotalAccounts();

	public abstract void setTotalDays(int total) ;
	
	public abstract void setTotalAccounts(int totalUsers);

	public abstract int getUsed();

	public abstract void setUsed(int used);
	
	public abstract boolean hasCode();
	
	public abstract boolean hasExpires();
	
	public abstract boolean hasAccounts();


}
