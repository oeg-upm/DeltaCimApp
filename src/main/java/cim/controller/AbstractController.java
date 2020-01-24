package cim.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


public abstract class AbstractController {
	

	
	protected void prepareResponse(HttpServletResponse response) {
		response.setHeader("Server", "Delta Gateway"); // Server type is hidden
		response.setStatus( HttpServletResponse.SC_BAD_REQUEST ); // by default response code is BAD
	}	
	
	
	protected boolean isLogged(HttpServletRequest request) {
		return isUser(request) || isAdmin(request);
	}
	
	protected boolean isUser(HttpServletRequest request) {
		return request.isUserInRole("ROLE_USER");
	}
	
	protected boolean isAdmin(HttpServletRequest request) {
		return request.isUserInRole("ROLE_ADMIN");

	}
	
}
