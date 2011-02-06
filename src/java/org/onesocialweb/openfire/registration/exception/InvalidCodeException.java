package org.onesocialweb.openfire.registration.exception;

@SuppressWarnings("serial")
public class InvalidCodeException extends Exception {

	public InvalidCodeException(){
		super("The specified Registration Code is not valid");
	}
}
