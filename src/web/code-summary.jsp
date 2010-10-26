<%@ page import="org.jivesoftware.util.*,
                 org.onesocialweb.openfire.registration.model.Invitation,
                 java.util.List,
                 org.onesocialweb.openfire.registration.db.DBManager,   
                 java.text.SimpleDateFormat,              
                 java.net.URLEncoder"
%>


<%@ taglib uri="http://java.sun.com/jstl/core_rt" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jstl/fmt_rt" prefix="fmt"%>
<% 
	
	List<Invitation> invitations= DBManager.getInstance().getCodes(); 	
	String DATE_FORMAT = "dd/MM/yyyy HH:mm";
	SimpleDateFormat sdf =  new SimpleDateFormat(DATE_FORMAT);	

%>

<html>
    <head>
        <title><fmt:message key="registration.props.form.title" /></title>  
        <meta name="pageID" content="codes-summary"/>      
    </head>
    <body>
    
    <p><fmt:message key="registration.props.form.details" /></p>    
    <div class="jive-contentBoxHeader"><fmt:message key="registration.props.form.table.header" /></div>
	<div class="jive-table">
	<table cellpadding="0" cellspacing="0" border="0" width="100%">
	<thead>
    <tr>    
    	 <th>&nbsp;</th>   
        <th nowrap width="10%"><fmt:message key="invitation.code" /></th>
        <th nowrap width="10%"><fmt:message key="invitation.created" /></th>
        <th nowrap width="10%"><fmt:message key="invitation.expires" /></th>
        <th nowrap width="10%"><fmt:message key="invitation.status" /></th>
        <th nowrap width="10%"><fmt:message key="invitation.total.users" /></th>
        <th nowrap width="10%"><fmt:message key="invitation.used" /></th>       
        <th nowrap width="1%"><fmt:message key="invitation.edit" /></th>
        <th nowrap width="1%"><fmt:message key="invitation.invalidate" /></th>      
    </tr>
	</thead>
	<tbody>
	
	    <% if (invitations.size()>0) {%>
		<% for (int i=0; i<invitations.size(); i++) {		
			Invitation inv= invitations.get(i);
		%>			
			<tr class="jive-<%= (((i%2)==0) ? "even" : "odd") %>">
			<td width="1%"> <%= i +1 %></td>
       		 <td width="10%">
            	<%= inv.getCode() %>
       		 </td>
       		  <td width="10%">
            	<%= sdf.format(inv.getFrom()) %>
       		 </td>
       		  <td width="10%">
            	<% if ((inv.getExpires()==null) || (inv.getExpires().compareTo(inv.getFrom())==0)){ %>
            	---
            	<% } else { %>
            	<%= sdf.format(inv.getExpires()) %>
            	<% } %>
       		 </td>
       		  <td width="10%">
       		  <% if (inv.getValid()) {%>
            	<fmt:message key="invitation.valid" />
              <% } else {               
              %>	
                <fmt:message key="invitation.invalid" />
              <% } %>  
       		 </td>
       		  <td width="10%">
            	<%= inv.getTotalAccounts() %>
       		 </td>
       		  <td width="10%">
            	<%= inv.getUsed() %>
       		 </td>
       		 <td width="1%" align="center">       	
       		 <% if (inv.getValid()) { %>	 
            <a href="code-edit.jsp?code=<%= URLEncoder.encode(inv.getCode(), "UTF-8") %>"
             title="<fmt:message key="invite.click_edit" />"
             ><img src="images/edit-16x16.gif" width="16" height="16" border="0" alt="<fmt:message key="global.click_edit" />"></a>
             <% } %>            
        </td>
      
        <td width="1%" align="center" style="border-right:1px #ccc solid;">
           <% if (inv.getValid()) { %>	 
            <a href="code-invalidate.jsp?code=<%= URLEncoder.encode(inv.getCode(), "UTF-8") %>"
             title="<fmt:message key="global.click_invalidate" />"
             ><img src="images/delete-16x16.gif" width="16" height="16" border="0" alt="<fmt:message key="global.click_invalidate" />"></a>
		  <%} else {%>
      	&nbsp;&nbsp;
        <% } %>       
        </td>        
       		
		</tr>
		<% } %>
		<% } else { %>
		<tr><td colspan="9" align="center">
		No Invite codes for this Server
		</td></tr>
		<% } %>
	</tbody>
	</table>
	</div>
	</body>
</html>