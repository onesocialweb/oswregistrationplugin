<%@ page import="org.jivesoftware.util.ParamUtils,
                 org.onesocialweb.openfire.registration.db.DBManager"
				 errorPage="error.jsp"                 
%>
 <%@ page import="java.util.Map"%>
 <%@ page import="java.util.HashMap"%>
                  
<%@ taglib uri="http://java.sun.com/jstl/core_rt" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jstl/fmt_rt" prefix="fmt" %>

<%  
	Map<String, String> errors = new HashMap<String, String>();
	String users = request.getParameter("users");
	String days= request.getParameter("days");
	String code= request.getParameter("code");
	boolean cancel = request.getParameter("cancel") != null;
    boolean create = request.getParameter("create") != null;
    int intDays=0;
    int intUsers=0;

    if (create){    	   
    		

    	if ((days!=null) && (days.length()!=0)) { 
    			try {    			    		
	    			intDays=Integer.parseInt(days.trim());    			
    			} catch (NumberFormatException e) {    			
    				errors.put("daysFormat", "");
    			}
    	}else{    		
    			intDays=0;
    	}
    	
    	if ((users!=null) && (users.length()!=0)){    		
    			try {    			    		
    				intUsers=Integer.parseInt(users.trim());		
    			} catch (NumberFormatException e) {    			
    				errors.put("usersFormat", "");
    			}    		    	    	    	
    	} else {
    		errors.put("users","");
    	}    	    	    
    		
    	if (errors.size() == 0) {

    		DBManager.getInstance().createCode(code, intDays, intUsers);
    		response.sendRedirect("code-summary.jsp");
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
	
	 if (cancel) {
	     response.sendRedirect("code-summary.jsp");
	     return;
	 }

 %>
<html>
    <head>
        <title><fmt:message key="code.new.title"/></title> 
        <meta name="pageID" content="code-new"/>         
        
     
    <script language="JavaScript">

	function enable_code(status)
	{
		status=!status;	
		document.f.code.disabled = !status;
	}

	</script>    
    </head>

<body>
<p><fmt:message key="code.new.info" /></p>
<form name="f" action="code-new.jsp" method="get">

	<div class="jive-contentBoxHeader">
		<fmt:message key="code.new.title" />
	</div>
	<div class="jive-contentBox">
		<table cellpadding="3" cellspacing="0" border="0">
		<tbody>
		<tr>
			<td width="1%" nowrap><label for="codetf"><fmt:message key="code.new.code" />:</label> *</td>
			<td width="19%">
				<input type="text" name="code" size="30" maxlength="75" value="<%= ((code!=null) ? code : "") %>"
				 id="codetf" autocomplete="off">
			</td>
			 <td nowrap align="left" width="80%">
                <input type="checkbox" name="generate" onclick="enable_code(this.checked)" >
                (<fmt:message key="code.generate"/>)
            </td>
						
		</tr>
		<tr>
			<td width="1%" nowrap><label for="expirestf"><fmt:message key="code.expires" />:</label></td>
			<td width="99%" colspan="2">
				<input type="text" name="days" size="10" maxlength="3" value="<%= ((days!=null) ? days : "") %>"
				 id="expirestf"> <fmt:message key="code.days" />
			</td>
		</tr>
		<tr>
			<td width="1%" nowrap><label for="userstf"><fmt:message key="code.users" />:</label> *</td>
			<td width="99%" colspan="2">
				<input type="text" name="users" size="10" maxlength="4" value="<%= ((users!=null) ? users : "") %>"
				 id="userstf">
			</td>
		</tr>
		  <tr>

			<td colspan="2" style="padding-top: 10px;">
				<input type="submit" name="create" value="<fmt:message key="invite.code.create" />">			
				<input type="submit" name="cancel" value="<fmt:message key="button.cancel" />">
			</td>
		</tr>
		
		</tbody>
		</table>
	</div>
	
	<span class="jive-description">
    * <fmt:message key="code.create.required" />
    </span>
	
	
</form>
</body>
</html>