<%@ page import="org.onesocialweb.openfire.registration.db.DBManager, 
				org.onesocialweb.openfire.registration.model.Invitation,
				java.text.SimpleDateFormat, java.util.Calendar, 
				java.net.URLEncoder,
				java.util.Date" %>
				
 <%@ page import="java.util.Map"%>
 <%@ page import="java.util.HashMap"%>				


<%@ taglib uri="http://java.sun.com/jstl/core_rt" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jstl/fmt_rt" prefix="fmt" %>
<%

	
	String DATE_FORMAT = "dd/MM/yyyy HH:mm";
	SimpleDateFormat sdf =  new SimpleDateFormat(DATE_FORMAT);		
	String created = "";
  	String expires = null;
  	String daysLeft= "";
  	String totalUsers="";
  	String used="";
  	int intDaysLeft=0;
  	int intUsers=0;

  Map<String, String> errors = new HashMap<String, String>(); 
  boolean cancel = request.getParameter("cancel") != null;
  boolean save = request.getParameter("save") != null;
  Invitation inv=null;
  
  String code= request.getParameter("code"); 
  if (code!=null) {
  	 inv= DBManager.getInstance().getCode(code);
  	 created = sdf.format(inv.getFrom());
  	 if (inv.getExpires()!=null)
  	 	expires = sdf.format(inv.getExpires());
  	 daysLeft= ""+ inv.getDaysLeft();
  	 totalUsers=""+inv.getTotalAccounts();
  	 used=""+inv.getUsed();
  }

 
  
  if (cancel) {	  	
	     response.sendRedirect("code-summary.jsp");
	     return;
	 }
  
  if (save) {	
	  
	  totalUsers=request.getParameter("totalUsers"); 
	  daysLeft=  request.getParameter("daysLeft"); 
	  

	  if ((daysLeft!=null)  && (!daysLeft.contains("---")) && (daysLeft.length()!=0)){
	  		try {    			    		
	  			intDaysLeft=Integer.parseInt(daysLeft.trim());   	  			
	    	} catch (NumberFormatException e) {    			
	    			errors.put("daysFormat", "");
	    		}
	    	}
	  else{    		
		  intDaysLeft=0;
	  }	 
	    	
	  if ((totalUsers!=null) && (totalUsers.length()!=0)){
	    		try {    			    		
	    			intUsers=Integer.parseInt(totalUsers.trim());		
	    		} catch (NumberFormatException e) {    			
	    			errors.put("usersFormat", "");
	    		}    		    		
	  } else {
		  errors.put("users","");
	  }


	  	 if (inv!=null){
	  		 
	  		if (totalUsers.length()!=0) 
	  			inv.setTotalAccounts(Integer.parseInt(totalUsers));
	  		
	  		if ( ((inv.getExpires()==null) && (!daysLeft.contains("-"))) ||  (inv.getDaysLeft()!=intDaysLeft) ) {
	  				int duration= intDaysLeft;	  				
	  				if (duration>0){
	  					Calendar now = Calendar.getInstance();	  				
	  					now.add(Calendar.DATE, duration);
	  					Date newExpires= new Date(now.getTimeInMillis());
	  					inv.setExpires(newExpires);
	  				}else{
	  		  			inv.setExpires(null);
	  		  		}	  			
	  		}	  		
	  		
	  		if (errors.size() == 0) {
		 		DBManager.getInstance().updateCode(inv);
		 		response.sendRedirect("code-summary.jsp");
	  		}
	  	 }
	  	 
	    	if (!errors.isEmpty()) { %>

	        <div class="jive-error">
	        <table cellpadding="0" cellspacing="0" border="0">
	        <tbody>
	            <tr>
	                <td class="jive-icon"><img src="images/error-16x16.gif" width="16" height="16" border="0" alt=""/></td>
	                <td class="jive-icon-label">
	              
	                <% if (errors.get("users") != null) { %>
	                    <fmt:message key="code.create.invalid_users" />
	                <% }  else if (errors.get("usersFormat") != null) { %>
	                    <fmt:message key="code.create.invalid_users_format" />
	                <% }  else if (errors.get("daysFormat") != null) { %>
	                    <fmt:message key="code.create.invalid_days_format" />
	                <% }  %> 
	                </td>
	            </tr>
	        </tbody>
	        </table>
	        </div>
	        <br>

	    <%  } 
	  
	 }
  
  
%>
<html>
    <head>
        <title><fmt:message key="invite.edit.title"/></title>   
        <meta name="pageID" content="code-edit"/>        
    </head>
    <body>
 
<form action="code-edit.jsp?code=<%= URLEncoder.encode(inv.getCode(), "UTF-8") %>" method="post">
    <legend><fmt:message key="code.edit.properties" /></legend>
    <div>
    <table cellpadding="3" cellspacing="0" border="0" width="100%">
    <tbody>
    <tr>
    <td width="1%" nowrap><label for="codetf"><fmt:message key="code.new.code" />:</label> *</td>
			<td width="19%">
				<input type="text" disabled="disabled" name="code" size="30" maxlength="75" value="<%= ((code!=null) ? code : "") %>"
				 id="codetf" autocomplete="off">
		    </td>
	</tr>
	<tr>
			<td width="1%" nowrap><label for="createdtf"><fmt:message key="code.created" />:</label></td>
			<td width="99%" colspan="2">
				<input type="text" disabled="disabled" name="created" size="20" value="<%= ((created!=null) ? created : "") %>"
				 id="createdtf">
			</td>
	</tr>
	<tr>
			<td width="1%" nowrap><label for="expirestf"><fmt:message key="code.expires.date" />:</label></td>
			<td width="19%">
				<% if (expires==null) { %>
				<input type="text" disabled="disabled" name="expires" size="20"  value="---"  id="expirestf"> 
			    <%  } else { %>
				<input type="text" disabled="disabled" name="expires" size="20"  value="<%= expires %>"  id="expirestf">
				<% } %>
			</td>
			<td width="80%" align="left">
			<% if (expires==null) { %>
				<input type="text" name="daysLeft" size="10" maxlength="3" value="---"
				 id="expirestf"> <fmt:message key="code.days.left" />
			<% } else { %>	 
			
				<input type="text" name="daysLeft" size="10" maxlength="3" value="<%= daysLeft %>"
				 id="expirestf"> <fmt:message key="code.days.left" />
			<% } %>	 
			</td>
	</tr>	
	<tr>
			<td width="1%" nowrap><label for="userstf"><fmt:message key="code.users" />:</label> *</td>
			<td width="19%">
				<input type="text" name="totalUsers" size="10" maxlength="4" value="<%= ((totalUsers!=null) ? totalUsers : "") %>"
				 id="userstf">
			</td>
			<td width="80%">
				<input type="text" disabled="disabled" name="used" size="10" maxlength="4" value="<%= ((used!=null) ? used : "") %>"
				 id="userstf"> <fmt:message key="code.used" />
			</td>  
		</tr>
	
	
	</tbody>
	</table>
	</div>
	
	
<br><br>

<input type="submit" name="save" value="<fmt:message key="invite.code.save" />">
	<input type="submit" name="cancel" value="<fmt:message key="button.cancel" />">

</form>


<br/>
<span class="jive-description">
    * <fmt:message key="code.create.required" />
    </span>
    
    
    </body>
</html>

    