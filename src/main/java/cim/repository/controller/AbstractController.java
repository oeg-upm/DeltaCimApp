package cim.repository.controller;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;

import cim.repository.service.LoginService;


public abstract class AbstractController {
	
	@Autowired
	public LoginService loginService;
	
	protected void prepareResponse(HttpServletResponse response) {
		response.setHeader("Server", "Delta Gateway"); // Server type is hidden
		response.setStatus( HttpServletResponse.SC_BAD_REQUEST ); // by default response code is BAD
	}	
	
	
	protected boolean isLogged() {
		return loginService.isLogged();
	}
	
}
