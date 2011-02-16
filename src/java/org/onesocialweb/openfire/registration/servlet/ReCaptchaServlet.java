package org.onesocialweb.openfire.registration.servlet;

import java.io.IOException;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.tanesha.recaptcha.ReCaptchaImpl;
import net.tanesha.recaptcha.ReCaptchaResponse;

import org.jivesoftware.admin.AuthCheckFilter;
import org.jivesoftware.util.JiveGlobals;

@SuppressWarnings("serial")
public class ReCaptchaServlet extends HttpServlet {

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) {	
		
		if (readPrivateKey()==null)
			try {
				response.sendError(HttpServletResponse.SC_FORBIDDEN,"There's been a server error. Please try again later or contact the server's admin");
			} catch (IOException e) {
							
			}
	
        if (!verifyChallenge(request))
			try {
				response.sendError(HttpServletResponse.SC_FORBIDDEN,"The Captcha text entered didn't match the pattern. Please try again!");
			} catch (IOException e) {
				
			}

	}
	
	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response) {
		
		if (readPrivateKey()==null)
			try {
				response.sendError(HttpServletResponse.SC_FORBIDDEN,"There's been a server error. Please try again later or contact the server's admin");
			} catch (IOException e) {
				
			}
		
		if (!verifyChallenge(request))
		try {
			response.sendError(HttpServletResponse.SC_FORBIDDEN,"The Captcha text entered didn't match the pattern. Please try again!");
		} catch (IOException e) {
			
		}
	}

	public boolean verifyChallenge(HttpServletRequest request) {
		String remoteAddr = request.getRemoteAddr();
		ReCaptchaImpl reCaptcha = new ReCaptchaImpl();
        reCaptcha.setPrivateKey(readPrivateKey());

        String challenge = request.getParameter("recaptcha_challenge_field");
        String uresponse = request.getParameter("recaptcha_response_field");
		ReCaptchaResponse reCaptchaResponse = reCaptcha.checkAnswer(remoteAddr, challenge, uresponse);
		return reCaptchaResponse.isValid();
	}
	
	@Override
	public void init(ServletConfig config) throws ServletException {
		super.init(config);

		// Exclude this servlet from requering the user to login
		AuthCheckFilter.addExclude("oswregistrationplugin");	
		AuthCheckFilter.addExclude("oswregistrationplugin/captcha");	
		AuthCheckFilter.addExclude("oswregistrationplugin/captcha/");		

	}
	
	private String readPrivateKey(){
		String key=null;
		key=JiveGlobals.getProperty("onesocialweb.captcha.privateKey");
		return key;
} 
	
}
