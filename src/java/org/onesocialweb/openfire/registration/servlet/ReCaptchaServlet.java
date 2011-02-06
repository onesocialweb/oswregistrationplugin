package org.onesocialweb.openfire.registration.servlet;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.tanesha.recaptcha.ReCaptchaImpl;
import net.tanesha.recaptcha.ReCaptchaResponse;

import org.jivesoftware.admin.AuthCheckFilter;

@SuppressWarnings("serial")
public class ReCaptchaServlet extends HttpServlet {

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) {				
	
        if (!verifyChallenge(request))
			try {
				response.sendError(HttpServletResponse.SC_FORBIDDEN,"The Captcha text entered didn't match the pattern. Please try again!");
			} catch (IOException e) {
				// TODO Auto-generated catch block				
			}

	}
	
	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response) {
		if (!verifyChallenge(request))
		try {
			response.sendError(HttpServletResponse.SC_FORBIDDEN,"The Captcha text entered didn't match the pattern. Please try again!");
		} catch (IOException e) {
			// TODO Auto-generated catch block				
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
		Properties properties = new Properties();
		InputStream inputStream = this.getClass().getClassLoader().getResourceAsStream("captcha.properties");  
		try {
			properties.load(inputStream);
			key =properties.getProperty("private.key");
		} catch (IOException e) {
		}
		return key;
} 
	
}
