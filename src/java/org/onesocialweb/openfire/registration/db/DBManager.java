package org.onesocialweb.openfire.registration.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.jivesoftware.database.DbConnectionManager;
import org.jivesoftware.util.Log;
import org.onesocialweb.openfire.registration.exception.EmailDoesntMatchException;
import org.onesocialweb.openfire.registration.model.DefaultInvitationFactory;
import org.onesocialweb.openfire.registration.model.Invitation;
import org.onesocialweb.openfire.registration.model.InvitationFactory;


public class DBManager {

	private final InvitationFactory factory;
	
	private  Connection connection=null;
	
	/**
	 * Singleton: keep a static reference to the only instance
	 */
	private static DBManager instance;

	public static DBManager getInstance() {
		if (instance == null) {
			// Carefull, we are in a threaded environment !
			synchronized (DBManager.class) {
				instance = new DBManager();
			}
		}
		return instance;
	}
	
	
	
	public int createCode(String code, int duration, int total) throws SQLException {
		
		if ((connection==null) || (connection.isClosed()))  connection=this.getConnection();

		if ((code==null) || (code.length()==0))
		 code = factory.code();
		
		long timeMilis= Calendar.getInstance().getTimeInMillis();		
		java.sql.Timestamp sqlDateStart =  new java.sql.Timestamp(timeMilis);
		
		String query= "INSERT INTO invitation(code, created, expires, total, used, valid) VALUES (?,?,?,?,?,?) ";
		PreparedStatement st= connection.prepareStatement(query);
	
		st.setString(1, code);
		st.setTimestamp(2, sqlDateStart);
	
		java.sql.Timestamp sqlDateExp =null;	
		if (duration>0){
			Calendar now = Calendar.getInstance();
			
			now.add(Calendar.DATE, duration);					
			sqlDateExp= new java.sql.Timestamp(now.getTimeInMillis());			
			st.setTimestamp(3, sqlDateExp);
			
		} else  {	
			st.setTimestamp(3, null);
		}
		st.setInt(4, total);
		st.setInt(5, 0);
		st.setBoolean(6, true);	
	
		int i_code = st.executeUpdate();
				
		return i_code;
	}
	
	public String createCode(int duration, int total, String email) throws SQLException {
		
		if ((connection==null) || (connection.isClosed()))  connection=this.getConnection();

		String code = factory.code();
		
		long timeMilis= Calendar.getInstance().getTimeInMillis();		
		java.sql.Timestamp sqlDateStart =  new java.sql.Timestamp(timeMilis);
		
		String query= "INSERT INTO invitation(code, created, expires, total, used, valid, email) VALUES (?,?,?,?,?,?,?) ";
		PreparedStatement st= connection.prepareStatement(query);
	
		st.setString(1, code);
		st.setTimestamp(2, sqlDateStart);
	
		java.sql.Timestamp sqlDateExp =null;	
		if (duration>0){
			Calendar now = Calendar.getInstance();
			
			now.add(Calendar.DATE, duration);					
			sqlDateExp= new java.sql.Timestamp(now.getTimeInMillis());			
			st.setTimestamp(3, sqlDateExp);
			
		} else  {	
			st.setTimestamp(3, null);
		}
		st.setInt(4, total);
		st.setInt(5, 0);
		st.setBoolean(6, true);	
		st.setString(7,email);
	
		int i_code1 = st.executeUpdate();
								
		if (i_code1 ==1 )
			return code;
		
		else return null;
	}

	public List<Invitation> getCodes() throws SQLException { 
		
		if ((connection==null) || (connection.isClosed()))  connection=this.getConnection();
		
		String query= "select code, created, expires, total, used, valid, email from invitation order by valid desc, created desc";
		PreparedStatement st= connection.prepareStatement(query);
		ResultSet rs= st.executeQuery();
		List<Invitation> existingCodes= new ArrayList<Invitation>();
		while (rs.next()){
			Invitation invitation= factory.invitation();
			invitation.setCode(rs.getString("code"));
			invitation.setFrom(rs.getTimestamp("created"));
			invitation.setExpires(rs.getTimestamp("expires"));
			invitation.setTotalAccounts(rs.getInt("total"));
			invitation.setUsed(rs.getInt("used"));
			invitation.setValid(rs.getBoolean("valid"));
			invitation.setEmail(rs.getString("email"));
			
			if (!checkValid(invitation)){
				invalidateCode(invitation.getCode());
				invitation.setValid(false);
			} 
			
			existingCodes.add(invitation);
		}
		return existingCodes;
		
	}	
	
	
	public Invitation getCode(String code) throws SQLException { 
		
		if ((connection==null) || (connection.isClosed())) connection=this.getConnection();
		
		String query= "select code, created, expires, total, used, valid, email from invitation where invitation.code='"+code+"'";
		PreparedStatement st= connection.prepareStatement(query);
		ResultSet rs= st.executeQuery();				
		
		boolean found=rs.next();
		
		if (!found)
			return null;
		
		Invitation invitation= factory.invitation();
		invitation.setCode(rs.getString("code"));
		invitation.setFrom(rs.getTimestamp("created"));
		invitation.setExpires(rs.getTimestamp("expires"));
		invitation.setTotalAccounts(rs.getInt("total"));
		invitation.setUsed(rs.getInt("used"));
		invitation.setValid(rs.getBoolean("valid"));
		invitation.setEmail(rs.getString("email"));

		if (!checkValid(invitation)){
			invalidateCode(invitation.getCode());
			invitation.setValid(false);
		} 
			
		return invitation;
		
	}
	
	public boolean emailIsActive(String email) throws SQLException{
		
		if ((connection==null) || (connection.isClosed())) connection=this.getConnection();
		
		String query= "select * from invitation where invitation.email='"+email+"' AND invitation.valid=1";
		PreparedStatement st= connection.prepareStatement(query);
		ResultSet rs= st.executeQuery();	
		
		boolean found=rs.next();
				
		return found;
	}
	
	public int updateCode(Invitation invite) throws SQLException {
		
		if ((connection==null) || (connection.isClosed())) connection=this.getConnection();
		java.sql.Timestamp sqlExpiryDate=null;
		if (invite.getExpires()!=null)
			sqlExpiryDate= new java.sql.Timestamp(invite.getExpires().getTime());
		String sql="UPDATE invitation SET expires=?, total=? where invitation.code='"+invite.getCode()+"'";		 
		PreparedStatement st= connection.prepareStatement(sql);
		st.setTimestamp(1, sqlExpiryDate);
		st.setInt(2, invite.getTotalAccounts());
		int result= st.executeUpdate();		
		return result;
	}
	
	
	public int invalidateCode(String code) throws SQLException{
		
		if ((connection==null) || (connection.isClosed()))  connection=this.getConnection();
				
		String sql ="UPDATE invitation SET valid=false where invitation.code='" +
			code +"'";		
		PreparedStatement st= connection.prepareStatement(sql);
		return st.executeUpdate();
		
	}
	
	private boolean checkValid(Invitation inv){
	
		if (inv.getExpires()!=null)
		{				
			int i= inv.getExpires().compareTo(new Date());
			if (i<0)
				return false;
		}	
		return true;
	}
	
	public boolean isValidCode(String code){
		try {
			Invitation inv=this.getCode(code);
			if (inv==null)
				return false;
			
			if ((this.checkValid(inv)) && (inv.getValid()))
				return true;
			
		} catch (SQLException e){
			
			Log.error(e);
			return false;
		}
		return false;
	}
	
	public void increaseUsed(String code) throws SQLException{
		
		if ((connection==null) || (connection.isClosed())) connection=this.getConnection();
		boolean valid=true;
		
		Invitation inv=this.getCode(code);
		int used=inv.getUsed();
		used++;
		
		if (used==inv.getTotalAccounts()) 
			valid=false;
		
		String sql ="UPDATE invitation SET used=?, valid=? where invitation.code='"+	code +"'";			
		PreparedStatement st= connection.prepareStatement(sql);
		st.setInt(1, used);
		st.setBoolean(2, valid);
		st.executeUpdate();
				
	}
	

	
	public boolean emailMatches(String code, String email)  throws SQLException{
		
		boolean matches=false;
		
		if ((connection==null) || (connection.isClosed())) connection=this.getConnection();
		
		String query= "select email from invitation where invitation.code='"+code+"'";
		PreparedStatement st= connection.prepareStatement(query);
		ResultSet rs= st.executeQuery();	
						
		if (rs.next()){		
			String emailRegistered =rs.getString("email");
			if (emailRegistered!=null) {
				if (emailRegistered.equalsIgnoreCase(email)) {
					matches=true;			
				}
			} else return true;
		} 
		
		return matches;
	}
	
	private Connection getConnection() throws SQLException {
		if ((this.connection==null) || (this.connection.isClosed()))
			connection=DbConnectionManager.getConnection();
	
		return connection;
	}
		

	/*  singleton - private constructor */
	private DBManager() {
		try {
			connection=DbConnectionManager.getConnection();
		}catch(SQLException e){
			
		}
		factory = new DefaultInvitationFactory();
	}
	
}
