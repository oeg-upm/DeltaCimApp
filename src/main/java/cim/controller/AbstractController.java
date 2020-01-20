package cim.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;

import cim.service.ConnectionService;


public abstract class AbstractController {
	
	@Autowired
	public ConnectionService loginService;
	
	protected void prepareResponse(HttpServletResponse response) {
		response.setHeader("Server", "Delta Gateway"); // Server type is hidden
		response.setStatus( HttpServletResponse.SC_BAD_REQUEST ); // by default response code is BAD
	}	
	
	
	protected boolean isLogged() {
		return true;//loginService.isLogged();
	}
	
	protected boolean isUser(HttpServletRequest request) {
		return request.isUserInRole("ROLE_USER");
	}
	
	protected boolean isAdmin(HttpServletRequest request) {
		return request.isUserInRole("ROLE_ADMIN");

	}
	
}
