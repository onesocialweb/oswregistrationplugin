<%@ page import="org.onesocialweb.openfire.registration.model.Invitation,
                 java.util.List,
                 org.onesocialweb.openfire.registration.db.DBManager"
%>
<%@ page import="org.jivesoftware.util.ParamUtils" %>

<%@ taglib uri="http://java.sun.com/jstl/core_rt" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jstl/fmt_rt" prefix="fmt" %>

<%  // Get parameters //
    boolean cancel = request.getParameter("cancel") != null;
	boolean invalidate = request.getParameter("invalidate") != null;
    String code = ParamUtils.getParameter(request,"code");
	// Handle a cancel
	if (cancel) {
    	response.sendRedirect("code-summary.jsp");
    return;
	}
    
    if (invalidate) {    	
		DBManager.getInstance().invalidateCode(code);
		response.sendRedirect("code-summary.jsp");
    	return;
    }

%>

<html>
    <head>
        <title><fmt:message key="invite.cancel.title"/></title>      
    </head>
    <body>


<p>
<fmt:message key="invite.cancel.question" />
</p>


<form action="code-invalidate.jsp">
<input type="hidden" name="code" value="<%= code %>">
<input type="submit" name="invalidate" value="<fmt:message key="button.invalidate" />">
<input type="submit" name="cancel" value="<fmt:message key="button.cancel" />">
</form>

    </body>
</html>
