package org.onesocialweb.openfire.registration.exception;

@SuppressWarnings("serial")
public class EmailRegisteredException extends Exception {

	public EmailRegisteredException(){
		super ("The email provided is already registered for a different account");
	}
	
}
