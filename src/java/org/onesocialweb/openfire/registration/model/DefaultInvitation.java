package org.onesocialweb.openfire.registration.model;

import java.util.Calendar;
import java.util.Date;

public class DefaultInvitation extends Invitation
{

	private String code;

	private Date from;
	
	private Date expires;
	
	private int totalUsers;
	
	private boolean valid;
	
	private int totalDays;

	private int used;
	
	
	@Override
	public int getTotalAccounts() {
		return totalUsers;
	}

	@Override
	public void setTotalAccounts(int totalUsers) {
		this.totalUsers = totalUsers;
	}

	@Override
	public int getTotalDays() {
		return totalDays;
	}

	@Override
	public void setTotalDays(int totalDays) {
		this.totalDays = totalDays;
	}	
	
	@Override
	public int getDaysLeft() {
		if (this.getExpires()==null)
			return 0;		
		Date currentDate=Calendar.getInstance().getTime();
		int MILLSECS_PER_DAY=24*60*60*10*10*10;
		long deltaDays = (this.getExpires().getTime() - currentDate.getTime() )/MILLSECS_PER_DAY ;
		int intDelta=(int)deltaDays;
		return intDelta;
	}

	
	@Override
	public String getCode() {
		return code;
	}
	
	@Override
	public void setCode(String code) {
		this.code = code;
	}

	@Override
	public Date getFrom() {
		return from;
	}

	@Override
	public void setFrom(Date from) {
		this.from = from;
	}
	
	@Override
	public boolean getValid(){
		return valid;
	}
	
	@Override
	public void setValid(boolean valid)	{
		this.valid=valid;
	}

	@Override
	public Date getExpires() {
		return expires;
	}

	@Override
	public void setExpires(Date expires) {
		this.expires = expires;
	}


	
	@Override
	public int getUsed() {
		return used;
	}

	@Override
	public void setUsed(int used) {
		this.used = used;
	}

	@Override
	public boolean hasAccounts() {
		if (this.totalUsers>0)
			return true;
		return false;
	}

	@Override
	public boolean hasCode() {	
		return false;
	}

	@Override
	public boolean hasExpires() {		
		return false;
	}

	
}
