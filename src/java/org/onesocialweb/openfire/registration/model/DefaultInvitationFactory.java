package org.onesocialweb.openfire.registration.model;

import java.math.BigInteger;
import java.security.SecureRandom;

public class DefaultInvitationFactory implements InvitationFactory{

	@Override
	public Invitation invitation(){
		return new DefaultInvitation();
	}
	
	@Override
	public String code(){
		SecureRandom random = new SecureRandom();
		return new BigInteger(130, random).toString(32);
	}
}
