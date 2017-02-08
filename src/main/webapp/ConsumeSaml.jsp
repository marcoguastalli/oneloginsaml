<%@ page language="java" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/custom-tags.tld" prefix="s7Tag" %>
<%@ page import="com.scene7.authentication.AuthUtils" %>
<%@ page import="com.scene7.authentication.AuthMode" %>
<%@ page import="com.scene7.dbaccess.LoginUser" %>
<%@ page import="com.scene7.dbaccess.LoginUserQuery" %>
<%@ page import="com.scene7.dbaccess.dao.LoginUserDAO" %>
<%@ page import="com.scene7.saml.SamlResponseManager" %>
<%@ page import="com.scene7.utils.IPSSystemLog" %>
<%@ page import="com.scene7.utils.ServletConfiguration" %>
<%@ page import="com.scene7.utils.StringOperations" %>
<%@ page import="com.scene7.utils.Params" %>
<%@ page import="com.scene7.encodements.EncodeUtil" %>
<%@ page import="com.onelogin.*, com.onelogin.saml.*" %>
<% request.setCharacterEncoding(EncodeUtil.getUrlEncoding()); %>

<!--(c) 2001-2007 Scene7, Inc. -->

<%@ include file="/includes/BrowserCache.jsp" %>

<%
    try {
	    if (!AuthUtils.isAdminAuthModeEnabled(AuthMode.OKTA)) {
	        IPSSystemLog.logError("ConsumeSaml: Okta login is disabled", IPSSystemLog.inAdmin);
	        AuthUtils.sendLoginErrorRedirect(request, response, "Okta login is disabled.");
	        return;
	    }

        AccountSettings accountSettings = new AccountSettings();
        accountSettings.setCertificate(ServletConfiguration.getProperty("okta.auth.cert"));
        Response samlResponse = new Response(accountSettings, request.getParameter("SAMLResponse"), request.getRequestURL().toString());
        
        LoginUser user = SamlResponseManager.getInstance().getLoginUser(request.getParameter("SAMLResponse"));
	    	    
	    // Successful login, proceed to session setup and redirect
	    String relayState = request.getParameter("RelayState");
	    if (!StringOperations.isEmpty(relayState)) {
	    	session.setAttribute("Page-redirect", relayState);
	    }
	    
	    AuthUtils.setUserSessionInfo(request, response, user);
	    AuthUtils.setSessionLocale(request, response);
	    AuthUtils.setSamlSessionInfo(request, response, samlResponse);
	    AuthUtils.sendLoginRedirect(request, response, user);
    } catch (Throwable t) {
        IPSSystemLog.logException(t);
        AuthUtils.sendLoginErrorRedirect(request, response, "Okta login exception: " + t.getMessage());
    }
 %>