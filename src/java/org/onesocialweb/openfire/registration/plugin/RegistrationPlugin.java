package org.onesocialweb.openfire.registration.plugin;

import java.io.File;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.jivesoftware.database.DbConnectionManager;
import org.jivesoftware.openfire.IQRouter;
import org.jivesoftware.openfire.XMPPServer;
import org.jivesoftware.openfire.container.Plugin;
import org.jivesoftware.openfire.container.PluginManager;
import org.jivesoftware.util.Log;
import org.onesocialweb.openfire.registration.handler.IQRegisterHandler;


public class RegistrationPlugin implements Plugin {
	
	private IQRegisterHandler iqRegisterHandler;
	
	 public void initializePlugin(PluginManager manager, File pluginDirectory) {
		
	       if (!table_exists())
	        	create_table();
	       iqRegisterHandler = new IQRegisterHandler();
	       IQRouter iqRouter = XMPPServer.getInstance().getIQRouter();
	       iqRouter.addHandler(iqRegisterHandler);
	       	      
	    }

	    public void destroyPlugin() {
	        // Your code goes here
	    	IQRouter iqRouter = XMPPServer.getInstance().getIQRouter();
	    	iqRouter.removeHandler(iqRegisterHandler);
	    }
	    
	    private boolean table_exists(){
	    	int i=0;
	    	try {
	    		Statement st= DbConnectionManager.getConnection().createStatement();		
	    		String query= "show tables like '%invitation%'";		
	    		ResultSet rs= st.executeQuery(query);
	    		while (rs.next())
	    			i++;
	    	} catch (SQLException e)
	    	{
	    		Log.error("An unxpected DB error ocurred: " , e);
	    	}
	    	
			if (i>0)
				return true;
			
	    	return false;
	    }
	    
	    private void create_table(){
	    	String createSQL = "create TABLE invitation (code char(50) PRIMARY KEY, created DATETIME, "
	    		+"expires DATETIME DEFAULT NULL, total int, used int, valid boolean)";
	    	try {
	    		Statement st= DbConnectionManager.getConnection().createStatement();	
	    		st.executeUpdate(createSQL);
	    		
	    	} catch (SQLException e)
	    	{
	    		Log.error("An unxpected DB error ocurred: " , e);
	    	}
	    }
	    
	    
		

}
