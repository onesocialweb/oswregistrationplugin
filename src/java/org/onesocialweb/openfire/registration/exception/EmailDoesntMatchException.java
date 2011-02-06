package org.onesocialweb.openfire.registration.exception;

@SuppressWarnings("serial")
public class EmailDoesntMatchException extends Exception{

	public EmailDoesntMatchException(){
		super ("The email specified doesn't match the one provided when requesting the registration code.");
	}
}
