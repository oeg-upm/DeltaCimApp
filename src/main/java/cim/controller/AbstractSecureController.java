package cim.controller;

import java.util.logging.Logger;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Controller;

import cim.ConfigTokens;
import cim.security.JwtTokenUtil;
import cim.service.UserService;
import io.jsonwebtoken.ExpiredJwtException;


@Controller
public class AbstractSecureController {

	
	@Autowired
	protected UserService userService;
	@Autowired
	protected JwtTokenUtil jwtTokenUtil;
	
	private Logger log = Logger.getLogger(AbstractSecureController.class.getName());
	
	protected static final String AUTHORTISATION_TOKEN = "Authorization";
	protected static final String BEARER_TOKEN = "Bearer ";
	
	
	protected void prepareResponse(HttpServletResponse response) {
		response.setHeader(ConfigTokens.SERVER_TOKEN, ConfigTokens.SERVER_NAME_TOKEN); // Server type is hidden
	}	

	protected void prepareResponseOK(HttpServletResponse response) {
		response.setHeader(ConfigTokens.SERVER_TOKEN, ConfigTokens.SERVER_NAME_TOKEN); // Server type is hidden
		response.setStatus( HttpServletResponse.SC_OK ); // by default response code is BAD
	}	
	
	protected void prepareResponseBAD(HttpServletResponse response) {
		response.setHeader(ConfigTokens.SERVER_TOKEN, ConfigTokens.SERVER_NAME_TOKEN); // Server type is hidden
		response.setStatus( HttpServletResponse.SC_BAD_REQUEST ); // by default response code is BAD
	}	
	

	protected Boolean authenticated(HttpServletRequest request) {
		String username = null;
		// Retrieve bearer token from cookie or header
		String jwtToken = retrieveTokenFromCookie(request);
		if (jwtToken == null) {
			log.severe(request.getRequestURI());
			log.warning("JWT token missing in cookie");
			jwtToken = retrieveTokenFromHeader(request);
		}
		if (jwtToken != null) {
			try {
				username = jwtTokenUtil.getUsernameFromToken(jwtToken);
			} catch (IllegalArgumentException e) {
				log.severe("Unable to get JWT Token");
			} catch (ExpiredJwtException e) {
				log.severe("JWT Token has expired");
			}
		} else {
			log.severe(request.getRequestURI());
			log.severe("JWT Token missing in header or does not begin with Bearer String");
		}
		
		return username != null && userService.existUsername(username) && jwtTokenUtil.validateToken(jwtToken, username);
	}
	


	protected String retrieveTokenFromCookie(HttpServletRequest request) {
		String jwt = null;
		Cookie[] cookies = request.getCookies();
		if (cookies != null) {
			for (Cookie cookie : cookies) {
				if (cookie.getName().equals("jwt")) {
					jwt = cookie.getValue();
					break;
				}
			}
		}
		return jwt;
	}

	protected String retrieveTokenFromHeader(HttpServletRequest request) {
		String jwt = null;
		String requestTokenHeader = request.getHeader(AUTHORTISATION_TOKEN);
		if (requestTokenHeader!=null && requestTokenHeader.startsWith(BEARER_TOKEN)) {
			jwt = requestTokenHeader.substring(7);
		}
		return jwt;
	}

	
	
}
