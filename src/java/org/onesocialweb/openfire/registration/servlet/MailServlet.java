package org.onesocialweb.openfire.registration.servlet;

import java.io.IOException;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.jivesoftware.admin.AuthCheckFilter;
import org.jivesoftware.openfire.XMPPServer;
import org.jivesoftware.openfire.user.User;
import org.jivesoftware.openfire.user.UserManager;
import org.jivesoftware.util.EmailService;
import org.jivesoftware.util.JiveGlobals;
import org.onesocialweb.openfire.registration.db.DBManager;
import org.onesocialweb.openfire.registration.exception.EmailRegisteredException;

@SuppressWarnings("serial")
public class MailServlet extends HttpServlet {

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {		
		try{
			doProcess(request, response);
		}
		catch (SQLException e){
			response.sendError(HttpServletResponse.SC_CONFLICT, "Oops! There was an unexpected problem during Registration, please try again!");
		}
		catch (EmailRegisteredException e){	
			response.sendError(HttpServletResponse.SC_FORBIDDEN,e.getMessage());
		}		
	}

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
		try{
			doProcess(request, response);
		}
		catch (SQLException e){
			response.sendError(HttpServletResponse.SC_CONFLICT, "Oops! There was an unexpected problem during Registration, please try again!");
		}
		catch (EmailRegisteredException e){
			response.sendError(HttpServletResponse.SC_FORBIDDEN,e.getMessage());
		}

	}
	
	private void doProcess(HttpServletRequest request, HttpServletResponse response) throws SQLException, IOException, EmailRegisteredException {
		
		String from = "OneSocialWeb";
		String to =request.getParameter("to");
		response.setContentType("text/plain");
		PrintWriter out = response.getWriter();		

		try {
			//if there is an account with that email or an active registration code, do not allow ...
			if (emailRegistered(to))
				throw new EmailRegisteredException();
			
			//Obtain a registration code: 
			int duration = JiveGlobals.getIntProperty("onesocialweb.registration.duration" , 30);
			String code = DBManager.getInstance().createCode(duration, 1, to);
			if (code==null)			
				throw new SQLException();
			
			  		
			EmailService service = EmailService.getInstance();
			
			MimeMessage message = service.createMimeMessage();
            // Set the date of the message to be the current date
            SimpleDateFormat format = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss Z", java.util.Locale.US);
            format.setTimeZone(JiveGlobals.getTimeZone());
            message.setHeader("Date", format.format(new Date()));

            // Set to and from.
            message.setRecipient(Message.RecipientType.TO, new InternetAddress(to, null));
            message.setFrom(new InternetAddress(from, null));
            message.setSubject("OneSocialWeb Registration");
            String body="Thanks for your interest in OneSocialWeb! Here is a registration code to create your new account: "+code;
            body += "\n \nThis code will be valid for a period of " + duration;
            body+=" days, after which it will expire. Please proceed to the Registration tab at the website of your OneSocialWeb provider.\n \n";
            body+="We hope you will enjoy the federation! If you do, please spread the word! \n \n";
            body+="--- \n \n Sent by OneSocialWeb at "+ XMPPServer.getInstance().getServerInfo().getXMPPDomain();
            
            message.setText(body);         
            service.sendMessagesImmediately(Collections.singletonList(message));
		}
		catch (MessagingException me) {
			out.println("There was a problem sending email...");
			me.printStackTrace();
		}			
		
	}
	
	@Override
	public void init(ServletConfig config) throws ServletException {
		super.init(config);

		// Exclude this servlet from requering the user to login
		AuthCheckFilter.addExclude("oswregistrationplugin");	
		AuthCheckFilter.addExclude("oswregistrationplugin/email");	
		AuthCheckFilter.addExclude("oswregistrationplugin/email/");		
		
		
		}
	
	public boolean emailRegistered(String email) throws SQLException{
		
		//check that the email doesn't already belong to an existing user...
		Set<String> fields = new HashSet<String>();
		fields.add("Email");
		Collection <User> users = UserManager.getInstance().findUsers(fields, email);
		
		//if there is an account with that email already...
		for (User user:users){
			if (user.getEmail().equalsIgnoreCase(email))
				return true;
		}
		
		//or if there is an active registration code with that email
		if (DBManager.getInstance().emailIsActive(email))
			return true;
			
		
		return false;
		
		
	}
}
