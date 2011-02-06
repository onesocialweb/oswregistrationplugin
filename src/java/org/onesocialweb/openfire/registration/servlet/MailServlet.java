package org.onesocialweb.openfire.registration.servlet;

import java.io.IOException;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.jivesoftware.admin.AuthCheckFilter;
import org.jivesoftware.openfire.XMPPServer;
import org.jivesoftware.openfire.user.User;
import org.jivesoftware.openfire.user.UserManager;
import org.jivesoftware.util.JiveGlobals;
import org.onesocialweb.openfire.registration.db.DBManager;
import org.onesocialweb.openfire.registration.exception.EmailRegisteredException;

import sun.net.smtp.SmtpClient;

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

			
			//Get the Mail Server Settings from Openfire
			String mailHost=JiveGlobals.getProperty("mail.smtp.host");
					
			SmtpClient smtp;		
			if (mailHost!=null)
				smtp = new SmtpClient(mailHost);  
			else // assume localhost
				smtp = new SmtpClient();  
		
			//Prepare the email message
			smtp.from(from);
			smtp.to(to);
			PrintStream msg = smtp.startMessage();

			msg.println("To: " + to);  // so mailers will display the To: address
			msg.println("Subject: OneSocialWeb Registration");
			msg.println();
			msg.println("Thanks for your interest in OneSocialWeb! Here is a registration code to create your new account: "+code);
			msg.println();
			msg.print("This code will be valid for a period of ");
			msg.print(duration);
			msg.println(" days, after which it will expire. Please proceed to the Registration tab at the website of your OneSocialWeb provider.");

			msg.println(); 
			msg.println("We hope you will enjoy the federation! If you do, please spread the word!");
			msg.println();
			msg.println("---");
			msg.println("Sent by OneSocialWeb at " + XMPPServer.getInstance().getServerInfo().getXMPPDomain());

			smtp.closeServer();

			out.println("Thanks for the joining the Federation!");
		}
		catch (IOException e) {
			out.println("There was a problem sending email...");
			e.printStackTrace();
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
